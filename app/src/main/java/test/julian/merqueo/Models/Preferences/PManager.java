package test.julian.merqueo.Models.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import test.julian.merqueo.R;

/**
 * Created by JulianStack on 25/06/2017.
 */

public class PManager {


    public static String TAG = "PreferencesManager";
    private Activity PContext;
    // Constructor
    public PManager(Activity context){
        this.PContext = context;
    }

    // save Download Images process Status
    public void setStatusProcess(String status,String parent, String child){
        Log.d(TAG,"Setting preferences Status: "+status+" Parent: "+parent+" Child: "+child);
        SharedPreferences sharedPref = PContext.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PContext.getResources().getString(R.string.process_status),status);
        editor.putString(PContext.getResources().getString(R.string.parent_position), parent);
        editor.putString(PContext.getResources().getString(R.string.child_position), child);
        editor.apply();
    }
    // retieve Download Images process Status
    public String[] getStatusProcess(){
        String[] array = new String[3];
        SharedPreferences sharedPref = PContext.getPreferences(Context.MODE_PRIVATE);
        array[0]= sharedPref.getString(PContext.getResources().getString(R.string.process_status), "Retrieving");
        array[1]= sharedPref.getString(PContext.getResources().getString(R.string.parent_position), "0");
        array[2]= sharedPref.getString(PContext.getResources().getString(R.string.child_position), "0");
        Log.d(TAG,"Getting preferences Status: "+array[0]+" Parent: "+array[1]+" Child: "+array[2]);
        return array;
    }

}
