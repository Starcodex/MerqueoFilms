package test.julian.merqueo.Views;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import test.julian.merqueo.Presenters.FilmsListPresenter;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IView;
import test.julian.merqueo.Presenters.Service.BackgroundService;
import test.julian.merqueo.Presenters.SplashPresenter;
import test.julian.merqueo.R;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by JulianStack on 17/06/2017.
 */

public class FilmsList extends AppCompatActivity implements IView, ServiceConnection {

    private static String TAG = "FilmsView";
    FilmsListPresenter presenter;

    // BIND SERVICE
    BackgroundService mBackground;
    volatile boolean mServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filmlist);
        // Change ActionBar Icon And Title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logoblanco);
        getSupportActionBar().setTitle("");
        Log.d(TAG,"FilmsList View");
        BindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnBindService();
    }


    public void UnBindService(){
        unbindService(this);
        presenter.unregisterReceivers();
    }


    public void BindService(){
        //Bind Service
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    // On Service Bound
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mBackground = ((BackgroundService.MyBinder) iBinder).getService();
        Log.d(TAG, "onServiceConnected");
        mServiceBound = true;
        // Pass View and Bundle to presenter
        presenter = new FilmsListPresenter(this,this,this, mBackground, mServiceBound);
        // Register ImageReceiver
        presenter.registerReceivers();
        presenter.processView(getWindow().getDecorView().findViewById(android.R.id.content),null);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mBackground = null;
        mServiceBound = false;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.films_view_menu, menu);
        presenter.menuCreated(menu);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.genre:
                presenter.PopulateListByFilter("Genre");
                return true;
            case R.id.year:
                presenter.PopulateListByFilter("Year");
                return true;
            case R.id.alphabet:
                presenter.PopulateListByFilter("Alphabet");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finishView() {
        finish();
    }

}
