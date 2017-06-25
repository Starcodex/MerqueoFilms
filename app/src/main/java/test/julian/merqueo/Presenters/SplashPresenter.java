package test.julian.merqueo.Presenters;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import test.julian.merqueo.Models.DB.DBManager;
import test.julian.merqueo.Models.Film;
import test.julian.merqueo.Models.Preferences.PManager;
import test.julian.merqueo.Models.SortedList;
import test.julian.merqueo.Presenters.Asynctasks.getGenres;
import test.julian.merqueo.Presenters.Helpers.HelperPresenter;
import test.julian.merqueo.Presenters.Interfaces.Interfaces;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IView;
import test.julian.merqueo.Presenters.Service.BackgroundService;
import test.julian.merqueo.R;
import test.julian.merqueo.Views.FilmsList;


/**
 * Created by JulianStack on 17/06/2017.
 */

public class SplashPresenter implements Interfaces.IPresenter,Interfaces.UIMSG {

    private String TAG = "SplashPresenter";
    HelperPresenter helperPresenter = new HelperPresenter();
    public static final int permissionCode = 44;


    // Receiver Load View
    private BroadcastReceiver imagereceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update image path
            Log.d(TAG,"Image Receiver");
            Bundle bundle = intent.getExtras();
            String status = bundle.getString("status");


            Film film = bundle.getParcelable("film");

            ContentValues values = new ContentValues();
            values.put(dbManager.F_IMG,film.getDirectoryPath());
            values.put(dbManager.F_DOWNLOADED,film.isDownloaded() ? 1 :  0);
            dbManager.update(dbManager.TABLE_FILMS_NAME,values,film.getId());

            Shared.setStatusProcess(status,bundle.getString("parent"),bundle.getString("child"));

            Log.d(TAG,"Record Updated");

            assert status != null;
            if(status.equals("Finish")){
                // Get Films By Genre
                mBackground.getFilteredList("Genre",SplashContext.getResources().getString(R.string.start_fimls_view),dbManager);
                finishView();
            }

        }
    };


    // Receiver Load View
    private BroadcastReceiver strtreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Close current view and Load App MainView
            Log.d(TAG,"Load FilmsList View Receiver");
            //Items.addAll(mBackground.getArray());
            finishView();
        }
    };


    // BIND SERVICE
    BackgroundService mBackground = null;
    volatile boolean mServiceBound = false;

    // objects
    private Context SplashContext;
    private Activity SplashActivity;
    private DBManager dbManager;
    private IView view;
    private TextView UiText;
    private PManager Shared;

    ArrayList<SortedList> Items = new ArrayList<>();

    // Constructor
    public SplashPresenter(Context context, Activity activity, IView splashview, BackgroundService background, boolean bound) {
        this.SplashContext = context;
        this.SplashActivity = activity;
        this.view = splashview;
        this.mBackground = background;
        this.mServiceBound = bound;
        this.Shared = new PManager(activity);
    }


    // Get Objects in View
    @Override
    public void processView(View ly, Bundle bundle) {

        dbManager = new DBManager(SplashContext);
        mBackground.setUI(this);
        UiText = (TextView)ly.findViewById(R.id.uitext);
        // check if user granted permission
        checkPermission();
    }

    // finish View
    public void finishView() {
        Log.d(TAG, "Finish View");
        Intent i = new Intent(SplashContext, FilmsList.class);
        SplashContext.startActivity(i);
        view.finishView();
    }


    // Register Receivers
    public void registerReceiver(){
        SplashContext.registerReceiver(strtreceiver, new IntentFilter(SplashContext.getResources().getString(R.string.start_fimls_view)));
        SplashContext.registerReceiver(imagereceiver, new IntentFilter(SplashContext.getResources().getString(R.string.img_receiver)));

    }

    // Unregister Receivers
    public void unregisterReceiver(){
        SplashContext.unregisterReceiver(strtreceiver);
        SplashContext.unregisterReceiver(imagereceiver);
    }


    // get Data
    public void getData(){

        Cursor genreTable = dbManager.getGroupsFilter("Genre");
        if(genreTable.getCount()==0){
            // local DB has no records
            // retrieve data from server
            Toast.makeText(SplashContext,"Retrieving Genres List",Toast.LENGTH_SHORT).show();
            new getGenres(dbManager,SplashContext,new getGenres.GenresListInterface() {

                @Override
                public void Success(String message,ArrayList<SortedList> items) {

                    Items.addAll(helperPresenter.getGenresListLocalDB(dbManager));
                    // Download FIlms By Genre
                    mBackground.getFilmsbyGenre(dbManager,Shared.getStatusProcess(),SplashContext,Items,SplashContext.getResources().getString(R.string.start_fimls_view));
                }
                @Override
                public void Cancelled(String message) {
                    Log.d(TAG,message);
                    Toast.makeText(SplashContext,message,Toast.LENGTH_LONG).show();
                }
            }).execute();

        }else{
            // local DB has Genre records
            if(Shared.getStatusProcess()[0].equals("Finish")){
                Log.d(TAG,"Retieving Films From local DB");
                mBackground.getFilteredList("Genre",SplashContext.getResources().getString(R.string.start_fimls_view),dbManager);
            }else{
                // Download Films By Genres
                Items.addAll(helperPresenter.getGenresListLocalDB(dbManager));
                mBackground.getFilmsbyGenre(dbManager,Shared.getStatusProcess(),SplashContext,Items,SplashContext.getResources().getString(R.string.start_fimls_view));
            }

        }
    }

    // check if Read and Write external storage permission is granted
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(SplashContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(SplashContext,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // request permission to user
                ActivityCompat.requestPermissions(SplashActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE },
                        SplashContext.getResources().getInteger(R.integer.permissions));
        }else{
            // permission granted
            getData();
        }
    }

    public void permissionResults(int requestCode,String permissions[], int[] grantResults){
        switch (requestCode) {

            case permissionCode :
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == permissionCode) {
                    Log.d(TAG,"Permission Granted");
                    getData();
                } else {
                    // permission denied
                    Toast.makeText(SplashContext,SplashContext.getResources().getString(R.string.grant_permission),Toast.LENGTH_LONG).show();
                    view.finishView();
                }
            return;
        }

    }

    // update UI messages in Splash View
    @Override
    public void updateMessageUI(String message) {
        UiText.setText(message);
    }
}
