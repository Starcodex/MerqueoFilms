package test.julian.merqueo.Presenters.Asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import test.julian.merqueo.Models.DB.DBManager;
import test.julian.merqueo.Models.Film;
import test.julian.merqueo.Presenters.Helpers.HelperPresenter;
import test.julian.merqueo.R;

/**
 * Created by JulianStack on 24/06/2017.
 */

public class getSearchFilm extends AsyncTask<String, String, ArrayList<Film>> {

    public static String TAG = "getSearchFilmByGroup";

    private Context mContext;
    String Group;
    private getSearchFilmInterface mInterface;

    DBManager dbManager;
    HelperPresenter helper = new HelperPresenter();

    // Constructor
    public getSearchFilm(DBManager dbmanager, Context context, String group, getSearchFilmInterface minterface) {
        this.dbManager = dbmanager;
        this.mContext = context;
        this.Group = group;
        this.mInterface = minterface;
    }

    // Interface
    public interface getSearchFilmInterface {
        void Success(String message, ArrayList<Film> films);

        void Cancelled(String message);
    }

    @Override
    protected ArrayList<Film> doInBackground(String... args) {
        try {
            return helper.getFilmSearch(dbManager,Group,Group);
        } catch (Exception e) {
            //On Exception Cancel Task
            cancel(true);
            e.printStackTrace();
            return null;
        }
    }

    // Success
    @Override
    protected void onPostExecute(ArrayList<Film> films) {
        Log.d(TAG, "Size getSearchFilm : " + films.size());
        mInterface.Success(mContext.getResources().getString(R.string.request_success_localdb), films);
    }

    // Failed
    @Override
    protected void onCancelled(ArrayList<Film> Films) {
        mInterface.Cancelled(mContext.getResources().getString(R.string.request_error_localdb));
    }
}
