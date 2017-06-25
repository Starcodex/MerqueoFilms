package test.julian.merqueo.Presenters.Helpers;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

import test.julian.merqueo.Models.DB.DBManager;
import test.julian.merqueo.Models.Film;
import test.julian.merqueo.Models.SortedList;


/**
 * Created by JulianStack on 20/06/2017.
 */

public class HelperPresenter {

    private static String TAG = "HelperPresenter";

    public ArrayList<SortedList> getGenresListLocalDB(DBManager manager){
        ArrayList<SortedList> arrSortedLists = new ArrayList<>();
        Cursor genres = manager.getGroupsFilter("Genre");
        Log.d(TAG, "RECORDS GENRES : "+String.valueOf(genres.getCount()));
        genres.moveToFirst();
        while(!genres.isAfterLast()) {
            arrSortedLists.add(new SortedList(genres.getString(genres.getColumnIndex(manager.G_ID)),genres.getString(genres.getColumnIndex(manager.G_NAME)),null)); //add item
            genres.moveToNext();
        }
        return arrSortedLists;
    }
    // get Films from Local DB by Genre Id
    public ArrayList<Film> getFilmListLocalDB(DBManager manager, String mFilter, String genreId){

            ArrayList<Film> films = new ArrayList<>();
            Cursor filmcursor = manager.getFilmsByFilterQuery(mFilter,"Genre",genreId);

            filmcursor.moveToFirst();
            while(!filmcursor.isAfterLast()) {

                films.add(new Film(filmcursor.getString(filmcursor.getColumnIndex(manager.F_ID)),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_TITLE)),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_VOTE_COUNT)),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_VOTE_AVERAGE)),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_LANGUAGE)),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_POSTER)),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_IMG)),
                        filmcursor.getInt(filmcursor.getColumnIndex(manager.F_DOWNLOADED))!=0,
                        convertStringToArray(filmcursor.getString(filmcursor.getColumnIndex(manager.F_GENRES))),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_OVERVIEW)),
                        filmcursor.getString(filmcursor.getColumnIndex(manager.F_RELEASE))

                ));
                filmcursor.moveToNext();
            }

        return films;
    }

    // convert Genres Array to String
    public static String convertArrayToString(String[] array){

        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append minus at the end of last element
            if(i!=array.length-1){
                str = str+"-";
            }
        }
        return str;
    }
    // convert Genres String to Array
    public static String[] convertStringToArray(String str){
        String[] arr = str.split("-");
        return arr;
    }

    // get Films from Local DB by Genre Id
    public ArrayList<Film> getFilmSearch(DBManager manager, String mFilter, String genreId){

        ArrayList<Film> films = new ArrayList<>();
        Cursor filmcursor = manager.getFilmsByFilterQuery(mFilter,"Search",mFilter);

        filmcursor.moveToFirst();
        while(!filmcursor.isAfterLast()) {

            films.add(new Film(filmcursor.getString(filmcursor.getColumnIndex(manager.F_ID)),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_TITLE)),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_VOTE_COUNT)),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_VOTE_AVERAGE)),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_LANGUAGE)),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_POSTER)),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_IMG)),
                    filmcursor.getInt(filmcursor.getColumnIndex(manager.F_DOWNLOADED))!=0,
                    convertStringToArray(filmcursor.getString(filmcursor.getColumnIndex(manager.F_GENRES))),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_OVERVIEW)),
                    filmcursor.getString(filmcursor.getColumnIndex(manager.F_RELEASE))

            ));
            filmcursor.moveToNext();
        }

        return films;
    }
}
