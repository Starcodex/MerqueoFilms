package test.julian.merqueo.Models.DB;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JulianStack on 17/06/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "merqueo.sqlite";
    private static final int DB_SCHEME_VERSION = 1;


    DBManager dbManager;
    public DbHelper(Context context, DBManager dbmanager) {
        super(context, DB_NAME, null, DB_SCHEME_VERSION);
        dbManager = dbmanager;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(dbManager.CREATE_GENRES_TABLE);
        db.execSQL(dbManager.CREATE_FILMS_TABLE);
        db.execSQL(dbManager.CREATE_FILMS_BELONGS_GENRE_TABLE);
        db.execSQL(dbManager.CREATE_RELEASE_TABLE); // not used
        db.execSQL(dbManager.CREATE_FILMS_BELONGS_RELEASE_TABLE); // not used
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
