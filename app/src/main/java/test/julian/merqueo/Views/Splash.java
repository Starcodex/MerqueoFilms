package test.julian.merqueo.Views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import test.julian.merqueo.Presenters.Interfaces.Interfaces;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IView;
import test.julian.merqueo.Presenters.Service.BackgroundService;
import test.julian.merqueo.Presenters.SplashPresenter;
import test.julian.merqueo.R;

/**
 * Created by JulianStack on 17/06/2017.
 */

public class Splash extends AppCompatActivity implements IView, ServiceConnection {

    private static String TAG = "SplashView";
    SplashPresenter presenter;


    // BIND SERVICE
    BackgroundService mBackground;
    volatile boolean mServiceBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

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

    @Override
    public void finishView() {
        finish();
    }


    public void UnBindService(){
        unbindService(this);
        presenter.unregisterReceiver();
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
        presenter = new SplashPresenter(Splash.this,Splash.this,Splash.this, mBackground, mServiceBound);
        presenter.registerReceiver();
        presenter.processView(getWindow().getDecorView().findViewById(android.R.id.content),null);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mBackground = null;
        mServiceBound = false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        presenter.permissionResults(requestCode,permissions,grantResults);
    }


}
