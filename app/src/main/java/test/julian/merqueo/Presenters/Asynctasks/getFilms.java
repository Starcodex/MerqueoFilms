package test.julian.merqueo.Presenters.Asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import test.julian.merqueo.Models.DB.DBManager;
import test.julian.merqueo.Models.Film;
import test.julian.merqueo.R;
import static test.julian.merqueo.Presenters.Helpers.HelperPresenter.convertArrayToString;
import static test.julian.merqueo.Presenters.Helpers.HelperPresenter.convertStringToArray;


/**
 * Created by JulianStack on 19/06/2017.
 */

public class getFilms extends AsyncTask<String, String, ArrayList<Film>> {

    public static String TAG = "getFilmsByGenre";

    //
    private HttpURLConnection connection = null;
    private JSONObject jsonObject;
    private Context mContext;
    private getFilmsInterface mInterface;
    private String mURL;
    private String APiKey;
    DBManager dbManager;
    ArrayList<Film> Films = new ArrayList<>();

    // Constructor
    public getFilms(DBManager dbmanager, Context context, String genre, getFilmsInterface minterface) {
        this.dbManager = dbmanager;
        this.mContext = context;
        this.mInterface = minterface;
        Resources res = mContext.getResources();
        mURL = String.format(res.getString(R.string.movies_by_genres), genre);
        APiKey = context.getResources().getString(R.string.api_key_v3);
    }

    // Interface
    public interface getFilmsInterface {
        void Success(String message, ArrayList<Film> films);
        void Cancelled(String message);
    }

    @Override
    protected ArrayList<Film> doInBackground(String... args) {
        try {

            URL url;

            //Create connection
            url = new URL(mURL+APiKey);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            // Accept Response
            connection.setUseCaches(false);
            connection.setDoInput(true);

            // Build Response
            String aux = "";
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                aux += line;
            }

            //Print response
            Log.d(TAG,"RESPONSE : "+ aux);

            // Retrieve Data from Response
            jsonObject = new JSONObject(aux);
            JSONArray results = jsonObject.getJSONArray("results");

            // Insert Into SQLite DB and create List
            for(int i = 0; i<results.length(); i++){

                JSONObject film = results.getJSONObject(i);
                // get Genres Array From Film JSONObject
                JSONArray arrJson = film.getJSONArray("genre_ids");
                String[] arrGenres = new String[arrJson.length()];
                String Path = "";
                // Create ContentValues object And put Values into DB from JSON
                if(!dbManager.recordExists(dbManager.TABLE_FILMS_NAME,dbManager.F_ID,String.valueOf(film.getInt("id")))){


                    Log.d(TAG,"COUNT : "+String.valueOf(arrJson.length()));
                    // Add Records
                    for(int j = 0; j<arrJson.length(); j++){

                        ContentValues relationvalues = new ContentValues();
                        relationvalues.put(dbManager.FILM_ID, String.valueOf(film.getInt("id")));
                        relationvalues.put(dbManager.GENRE_ID, String.valueOf(arrJson.getInt(j)));
                        // Insert Records
                        dbManager.insert(dbManager.TABLE_FILMS_BELONGS_GENRE,relationvalues);

                        Cursor genre = dbManager.getRecord(dbManager.TABLE_GENRES_NAME, String.valueOf(arrJson.getInt(j)));
                        // get Genres into Array String names
                        genre.moveToFirst();
                        while(!genre.isAfterLast()) {
                            arrGenres[j] = genre.getString(genre.getColumnIndex(dbManager.G_NAME));
                            genre.moveToNext();
                        }

                    }

                    // convert to single String
                    String GenresList = convertArrayToString(arrGenres);
                    // Insert Film into Local DB
                    ContentValues values = new ContentValues();
                    values.put(dbManager.F_ID, String.valueOf(film.getInt("id")));
                    values.put(dbManager.F_TITLE, film.getString("title"));
                    values.put(dbManager.F_VOTE_COUNT, film.getString("vote_count"));
                    values.put(dbManager.F_VOTE_AVERAGE, film.getString("vote_average"));
                    values.put(dbManager.F_LANGUAGE, film.getString("original_language"));
                    values.put(dbManager.F_POSTER, film.getString("poster_path"));
                    values.put(dbManager.F_IMG, Path);
                    values.put(dbManager.F_DOWNLOADED, 0);
                    values.put(dbManager.F_GENRES, GenresList);
                    values.put(dbManager.F_OVERVIEW, film.getString("overview"));
                    values.put(dbManager.F_RELEASE, film.getString("release_date"));

                    // Insert Values in DB SQlite
                    dbManager.insert(dbManager.TABLE_FILMS_NAME,values);

                }
            }

            // Get Films from Local DB
            Cursor films = dbManager.getFullTable(dbManager.TABLE_FILMS_NAME);
            films.moveToFirst();
            while(!films.isAfterLast()) {

                Films.add(new Film(films.getString(films.getColumnIndex(dbManager.F_ID)),
                        films.getString(films.getColumnIndex(dbManager.F_TITLE)),
                        films.getString(films.getColumnIndex(dbManager.F_VOTE_COUNT)),
                        films.getString(films.getColumnIndex(dbManager.F_VOTE_AVERAGE)),
                        films.getString(films.getColumnIndex(dbManager.F_LANGUAGE)),
                        films.getString(films.getColumnIndex(dbManager.F_POSTER)),
                        films.getString(films.getColumnIndex(dbManager.F_IMG)),
                        films.getInt(films.getColumnIndex(dbManager.F_DOWNLOADED))!=0,
                        convertStringToArray(films.getString(films.getColumnIndex(dbManager.F_GENRES))),
                        films.getString(films.getColumnIndex(dbManager.F_OVERVIEW)),
                        films.getString(films.getColumnIndex(dbManager.F_RELEASE))));
                films.moveToNext();
            }


            // close connection
            rd.close();
            return Films;

        } catch (Exception e) {
            //On Exception Cancel Task
            cancel(true);
            e.printStackTrace();
            return null;

        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    // Success
    @Override
    protected void onPostExecute(ArrayList<Film> films) {
        Log.d(TAG,"Size getFilms : "+films.size());
        mInterface.Success(mContext.getResources().getString(R.string.request_success),films);
    }

    // Failed
    @Override
    protected void onCancelled(ArrayList<Film> Films){
        mInterface.Cancelled(mContext.getResources().getString(R.string.request_error_films));
    }

}

