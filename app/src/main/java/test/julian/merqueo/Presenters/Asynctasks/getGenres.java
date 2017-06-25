package test.julian.merqueo.Presenters.Asynctasks;
import android.content.ContentValues;
import android.content.Context;
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
import test.julian.merqueo.Models.SortedList;
import test.julian.merqueo.R;


/**
 * Created by JulianStack on 17/06/2017.
 */

public class getGenres extends AsyncTask<String, String, ArrayList<SortedList>> {

    public static String TAG = "getGenresList";

    //
    private HttpURLConnection connection = null;
    private JSONObject jsonObject;
    private Context mContext;
    private GenresListInterface mInterface;
    private String mURL;
    private String APiKey;

    DBManager dbManager;

    ArrayList<SortedList> Items = new ArrayList<>();
    // Constructor
    public getGenres(DBManager dbmanager, Context context, GenresListInterface minterface) {
        this.dbManager = dbmanager;
        this.mContext = context;
        this.mInterface = minterface;
        mURL = context.getResources().getString(R.string.genres);
        APiKey = context.getResources().getString(R.string.api_key_v3);
    }

    // Interface
    public interface GenresListInterface {
        void Success(String message,ArrayList<SortedList> Items);
        void Cancelled(String message);
    }

    @Override
    protected ArrayList<SortedList> doInBackground(String... args) {
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

            // Retrieve Data from Response
            jsonObject = new JSONObject(aux);
            JSONArray genres = jsonObject.getJSONArray("genres");

            // Insert Into SQLite DB
            for(int i = 0; i<genres.length(); i++){
                JSONObject genre = genres.getJSONObject(i);
                // Create ContentValues object And put Values from JSON

                ContentValues values = new ContentValues();
                values.put(dbManager.G_ID, String.valueOf(genre.getInt("id")));
                values.put(dbManager.G_NAME, genre.getString("name"));

                if(!dbManager.recordExists(dbManager.TABLE_GENRES_NAME,dbManager.G_ID,String.valueOf(genre.getInt("id")))){
                    dbManager.insert(dbManager.TABLE_GENRES_NAME,values);
                }

                // Add Values to ArrayList
                Items.add(new SortedList(String.valueOf(genre.getInt("id")),genre.getString("name"),null));

            }

            //Print response
            Log.d(TAG,"RESPONSE : "+ aux);
            // close connection
            rd.close();
            return Items;

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
    protected void onPostExecute(ArrayList<SortedList> result) {
        mInterface.Success(mContext.getResources().getString(R.string.request_success),result);
    }

    // Failed
    @Override
    protected void onCancelled(ArrayList<SortedList> sortedList){
        mInterface.Cancelled(mContext.getResources().getString(R.string.request_error_genres));
    }

}

