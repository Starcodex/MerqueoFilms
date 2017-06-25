package test.julian.merqueo.Models.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import test.julian.merqueo.R;

/**
 * Created by JulianStack on 17/06/2017.
 */

public class DBManager {

    ////////////////////// TABLES /////////////////////////

    // GENRES TABLE
    public final String TABLE_GENRES_NAME = "genres";
    public final String G_ID = "id";
    public final String G_NAME = "name";

    // GENRES TABLE
    public final String TABLE_RELEASE_NAME = "filmsrelease";
    public final String R_ID = "id";
    public final String R_NAME = "name";

    // FILMS TABLE
    public  final String TABLE_FILMS_NAME = "films";
    public  final String F_ID = "id";
    public  final String F_TITLE = "title";
    public  final String F_VOTE_AVERAGE = "voteaverage";
    public  final String F_VOTE_COUNT = "votecount";
    public  final String F_LANGUAGE = "originalLanguage";
    public  final String F_POSTER = "posterpath";
    public  final String F_IMG = "directorypath";
    public  final String F_DOWNLOADED = "downloaded";
    public  final String F_GENRES = "genres";
    public  final String F_OVERVIEW = "overview";
    public  final String F_RELEASE = "releasedate";

    // FILMS BELONG GENRES RELATION MANY TO MANY
    public  final String TABLE_FILMS_BELONGS_GENRE = "films_genres";
    public  final String FG_ID = "id";
    public  final String GENRE_ID = "genre_id";
    public  final String FILM_ID = "film_id";

    // FILMS BELONG RELEASE RELATION MANY TO MANY - Not used
    public  final String TABLE_FILMS_BELONGS_RELEASE = "films_release";
    public  final String FR_ID = "id";
    public  final String RELEASE_ID = "release_id";


    //////////////////// TABLES STRUCTURE ///////////////////////

    // Genres
    public  final String[] GenresList = new String[] {G_ID,G_NAME};
    // Release
    public  final String[] ReleaseList = new String[] {R_ID,R_NAME};
    // Films
    public  final String[] FilmsList = new String[] {F_ID,F_TITLE,F_VOTE_COUNT,F_VOTE_AVERAGE,F_LANGUAGE,F_POSTER,F_IMG,F_DOWNLOADED,F_GENRES,F_OVERVIEW,F_RELEASE};
    // RelationShips
    public  final String[] FilmsRelated = new String[] {FG_ID,GENRE_ID,FILM_ID};

    public  final String[] FilmsRelease = new String[] {FR_ID,RELEASE_ID,FILM_ID};

    ////////////////////////// SQLITE QUERIES ///////////////

    // Create Genres Table
    public  final String CREATE_GENRES_TABLE = "create table "+TABLE_GENRES_NAME+" ("+
            G_ID+" integer primary key not null , "+
            G_NAME+" text not null );";

    // Create Release Table
    public  final String CREATE_RELEASE_TABLE = "create table "+TABLE_RELEASE_NAME+" ("+
            R_ID+" integer primary key not null , "+
            R_NAME+" text not null );";

    // Create Films Table
    public  final String CREATE_FILMS_TABLE = "create table "+TABLE_FILMS_NAME+" ("+
            F_ID+" integer primary key not null , "+
            F_TITLE+" text not null , "+
            F_VOTE_COUNT+" text , "+
            F_VOTE_AVERAGE+" text , "+
            F_LANGUAGE+" text , "+
            F_POSTER+" longtext , "+
            F_IMG+" longtext not null , "+
            F_DOWNLOADED+" integer , "+
            F_GENRES+" longtext , "+
            F_OVERVIEW+" longtext , "+
            F_RELEASE+" text );";

    // Create RelationShip Table
    public  final String CREATE_FILMS_BELONGS_GENRE_TABLE = "create table " +
            TABLE_FILMS_BELONGS_GENRE + " (" +
            FG_ID + " integer primary key autoincrement, " +
            GENRE_ID + " integer , " +
            FILM_ID + " integer , " +
            " FOREIGN KEY ("+GENRE_ID+") REFERENCES "+TABLE_GENRES_NAME+" ("+G_ID+") , " +
            "FOREIGN KEY ("+FILM_ID+") REFERENCES "+TABLE_FILMS_NAME+" ("+F_ID+"));";

    // Create RelationShip Table
    public  final String CREATE_FILMS_BELONGS_RELEASE_TABLE = "create table " +
            TABLE_FILMS_BELONGS_RELEASE + " (" +
            FR_ID + " integer primary key autoincrement, " +
            RELEASE_ID + " integer , " +
            FILM_ID + " integer , " +
            " FOREIGN KEY ("+RELEASE_ID+") REFERENCES "+TABLE_RELEASE_NAME+" ("+R_ID+") , " +
            "FOREIGN KEY ("+FILM_ID+") REFERENCES "+TABLE_FILMS_NAME+" ("+F_ID+"));";



    public String GET_GENRES_GROUPS = "SELECT * FROM genres ORDER BY genres.name ASC ;";
    //Get RELEASE GROUPS AND FILMS
    public  String GET_RELEASE_YEARS = "SELECT strftime('%Y',films.releasedate) AS name FROM films GROUP BY strftime('%Y',films.releasedate) ORDER BY strftime('%Y',films.releasedate) DESC ;";
    public  String GET_FILMS_BY_RELEASE = "SELECT * FROM films WHERE strftime('%Y',films.releasedate) =? ;";

    //Get Films GROUPS AND FILMS
    public  String GET_ALPHABET_LETTERS = "SELECT DISTINCT substr(films.title, 1, 1) AS name FROM films GROUP BY substr(films.title, 1, 1) ORDER BY substr(films.title, 1, 1) ASC ;";
    public  String GET_FILMS_BY_ALPHABET = "SELECT * FROM films WHERE substr(films.title, 1, 1) =? ORDER BY films.title ASC ;";


    //Get Films By GENRE
    public  String GET_FILMS_BY_GENRE = "SELECT * FROM  " +
            " films  " +
            " LEFT JOIN "+TABLE_FILMS_BELONGS_GENRE+" ON films.id = films_genres.film_id " +
            " LEFT JOIN  genres ON films_genres.genre_id = genres.id WHERE films_genres.genre_id = ? ORDER BY films.title ASC;";

    String var = "%1$s";
    public  String GET_FILMS_BY_QUERY = "SELECT * FROM films WHERE films.title LIKE  ? ;";

    ////////////////////////////////////////////////////////////

    private String TAG = "DBManager";


    //DB
    private DbHelper helper;
    SQLiteDatabase db;

    /// Constructor
    public DBManager(Context context) {
        // Start DB
        helper = new DbHelper(context,this);
        db = helper.getWritableDatabase();
    }


    // Get Films By Filter /// ID for Genres // Name for Another Lists
    public Cursor getFilmsByFilterQuery(String filter, String item,String genre){
        switch (item){
            case "Search":
                return db.rawQuery(GET_FILMS_BY_QUERY, new String[]{"%"+filter+"%"});
            case "Genre" :
                return db.rawQuery(GET_FILMS_BY_GENRE, new String[]{genre});
            case "Year" :
                return db.rawQuery(GET_FILMS_BY_RELEASE, new String[]{filter});
            case "Alphabet" :
                return db.rawQuery(GET_FILMS_BY_ALPHABET, new String[]{filter});

        }
        return db.rawQuery(GET_FILMS_BY_RELEASE, new String[]{item});
    }



    public Cursor getGroupsFilter(String item){
        switch (item){
            case "Search":
                break;
            case "Genre" :
                return db.rawQuery(GET_GENRES_GROUPS,null);
            case "Year" :
                return db.rawQuery(GET_RELEASE_YEARS,null);
            case "Alphabet" :
                return db.rawQuery(GET_ALPHABET_LETTERS,null);

        }
        return db.rawQuery(GET_FILMS_BY_RELEASE, new String[]{item});
    }




    // Insert into table
    public void insert(String TableName,ContentValues values){
        db.insert(TableName,null,values);
    }

    // Update Record
    public void update(String TableName,ContentValues values, String id){
        db.update(TableName,values,F_ID+"=?",new String[]{id});
    }

    // Get Single Record
    public Cursor getRecord(String TableName, String id){
        return db.query(TableName,getTableStructure(TableName),G_ID+"=?",new String[]{id},null,null,null);
    }

    // Check if Record exists in table
    public boolean recordExists(String TableName,String dbfield, String fieldValue) {
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    // get All Records from table
    public Cursor getFullTable(String TableName){
        return db.query(TableName,getTableStructure(TableName),null,null,null,null,null);
    }



    // Return Array TABLE STRUCTURE
    private String[] getTableStructure(String TableName){
        String[] Columns;
        switch (TableName){
            case TABLE_FILMS_NAME:
                Columns = FilmsList;
                break;
            case TABLE_GENRES_NAME :
                Columns = GenresList;
                break;
            case TABLE_FILMS_BELONGS_GENRE :
                Columns = FilmsRelated;
                break;
            case TABLE_RELEASE_NAME :
                Columns = ReleaseList;
                break;
            case TABLE_FILMS_BELONGS_RELEASE :
                Columns = ReleaseList;
                break;
            default:
                Columns = new String[]{};
                break;
        }
        return Columns;
    }

}
