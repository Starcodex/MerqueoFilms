package test.julian.merqueo.Presenters.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

import test.julian.merqueo.Models.DB.DBManager;
import test.julian.merqueo.Models.Film;
import test.julian.merqueo.Models.Preferences.PManager;
import test.julian.merqueo.Models.SortedList;
import test.julian.merqueo.Presenters.Asynctasks.getFile;
import test.julian.merqueo.Presenters.Asynctasks.getFilms;
import test.julian.merqueo.Presenters.Asynctasks.getFilmsLocalDB;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.UIMSG;
import test.julian.merqueo.R;

import static test.julian.merqueo.Presenters.Asynctasks.getFilms.*;
import static test.julian.merqueo.Presenters.Asynctasks.getFilmsLocalDB.*;
import static test.julian.merqueo.Presenters.Helpers.HelperPresenter.convertStringToArray;


/**
 * Created by JulianStack on 20/06/2017.
 */

public class BackgroundService extends Service {


    private static String TAG = "TestService";
    private IBinder mBinder = new MyBinder();

    ArrayList<SortedList> list = new ArrayList<>();
    public UIMSG UI;


    static Context mServiceContext;

    public Context getServiceContext() {
        synchronized(mServiceContext) {
            return mServiceContext;
        }
    }

    // UI Messages
    public UIMSG getUI(){
        return UI;
    }
    public void setUI(UIMSG ui){
        this.UI = ui;
    }

    // List Sorted
    public  ArrayList<SortedList> getArray(){
        return this.list;
    }
    public void setArray(ArrayList<SortedList> d){
        this.list=d;
    }


    @Override
    public void onCreate() {
        mServiceContext = BackgroundService.this;
        Log.d(TAG, "onCreate called");
    }

    // called on Bind start service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand executed");
        return Service.START_NOT_STICKY;
    }

    // service bound
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "in onBind");
        return mBinder;
    }
    // service bound
    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "in onRebind");
        super.onRebind(intent);
    }
    // unBind
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "in onDestroy");
    }

    // get List by Filters
    public void getFilteredList(String item,String intentFilter, DBManager dbManager){
        list.clear();
        Log.d(TAG,item);
        Cursor cursorGroups = dbManager.getGroupsFilter(item);
        setArray(sortFilmsbyFilter(cursorGroups,dbManager,intentFilter,item));
        sendBoradcastFiltered(getArray().size(),"Finish","Films List Retrieved succesfull",intentFilter,item);
    }

    public void printResultedCursor(Cursor cursor){
        Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
    }

    //Send status to presenter
    public void sendBoradcastFiltered(int groups, String status,String message, String intentFilter, String item){
        Bundle b = new Bundle();
        b.putString("groups",String.valueOf(groups));
        b.putString("status",status);
        b.putString("message",message);
        b.putString("item",item);
        Intent intent =  new Intent(intentFilter);
        intent.putExtras(b);
        getServiceContext().sendBroadcast(intent);
    }

    // Binder
    public class MyBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }


    // Get Films
    public void
    getFilmsbyGenre(final DBManager dbManager, final String[] sharedData, final Context context, final ArrayList<SortedList> Items, final String intentFilter){

        final int itemsSize = Items.size();
        Log.d(TAG,"Items Size : "+itemsSize);
        for(int i = 0; i<itemsSize; i++){
            final int finalI = i;
            final String genreName = Items.get(i).getName();

                // retrieve from remote server
                UI.updateMessageUI("Retrieving data ... this action can take few minutes, please wait.");
                new getFilms(dbManager, context, Items.get(i).getId(), new getFilmsInterface() {
                    @Override
                    public void Success(String message, ArrayList<Film> films) {
                        UI.updateMessageUI(message+" retrieved : "+films.size()+" records by "+genreName+" genre");
                        Log.d(TAG,message);
                        Items.get(finalI).setFilmList(films);
                        // download images if all films ar downloaded
                        if(itemsSize-1==finalI){
                            UI.updateMessageUI("Preparing to download images");
                            getFilmsImages(films,sharedData,intentFilter,dbManager);
                        }
                    }
                    @Override
                    public void Cancelled(String message) {
                        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                        UI.updateMessageUI(message);
                    }
                }).execute();

        }
    }

    private void getFilmsImages(ArrayList<Film> peliculas, String[] shared,String intentFilter, DBManager dbManager) {
        //Check if Images Are downloaded
        if(shared[0].equals("Retrieving")){
            int size = peliculas.size();
            for(int i = Integer.parseInt(shared[1]); i<size; i++){
                getImages(peliculas,i);
            }
        }else{
           // Retrieve films By Genres From Local DB
            getFilteredList("Genre",intentFilter,dbManager);
        }
    }

    // get Images
    private void getImages(final ArrayList<Film> peliculas, int i) {

        new getFile(i, getServiceContext(), peliculas, new getFile.DownloadInterface() {
            @Override
            public void success(String message,final String results, ArrayList<Film> Array, final int pos) {
                Array.get(pos).setDirectoryPath(results);

                UI.updateMessageUI(message+" downloaded image for "+Array.get(pos).getTitle());
                // update image Film
                Film film = Array.get(pos);
                film.setDownloaded(true);
                film.setDirectoryPath(results);
                Array.set(pos,film);
                // send films to presenter for update record in localDB and sharedpreferences status image
                String Status = (Array.size()-1==pos) ? "Finish":"Retrieving";
                sendSingleFilm(Status,pos,0,film);

            }
            @Override
            public void failed(String message,int childpos) {
                UI.updateMessageUI(message+" "+peliculas.get(childpos).getTitle());
                Log.d(TAG, "ERROR");
            }
        }).execute();

    }


    /// Send films throught Bundle
    private void sendSingleFilm(String status, int parentpos, int childpos, Film film){
        Log.d(TAG,"Sending Broadcast");
        Bundle bundle = new Bundle();
        bundle.putParcelable("film",film);
        bundle.putString("status",status);
        bundle.putString("parent", String.valueOf(parentpos));
        bundle.putString("child", String.valueOf(childpos));
        Intent mintent = new Intent(getServiceContext().getResources().getString(R.string.img_receiver));
        mintent.putExtras(bundle);
        getServiceContext().sendBroadcast(mintent);
    }


    ////////// get Full List Sorted
    public ArrayList<SortedList> sortFilmsbyFilter(Cursor cursorGroups, DBManager dbManager, String intentFilter, String item){
        UI.updateMessageUI("Sorting Films by "+item);
        ArrayList<SortedList> sortedlist = new ArrayList<>();
        sortedlist.addAll(getArrayGroups(dbManager,cursorGroups,item));

        // Pass status to view
        Log.d(TAG,"Sending Broadcast");
        sendBoradcastFiltered(sortedlist.size(),"Retrieving","retrieving films by "+item,intentFilter, item);

        for(int j = 0; j<sortedlist.size(); j++){
            ArrayList<Film> arrFilms = new ArrayList<>();
            String filter = sortedlist.get(j).getName();
            String Gfilter = sortedlist.get(j).getId();

            Cursor films = dbManager.getFilmsByFilterQuery(filter,item,Gfilter);

            films.moveToFirst();
            while(!films.isAfterLast()) {
                arrFilms.add(new Film(films.getString(films.getColumnIndex(dbManager.F_ID)),
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
            sortedlist.get(j).setFilmList(arrFilms);

            Log.d(TAG,"Sending Broadcast "+filter);
            sendBoradcastFiltered(sortedlist.size(),"retrieving","retrieving films from "+filter+" year",filter,item);
        }
        return sortedlist;
    }

    ///////// Sort Groups List
    private Collection<? extends SortedList> getArrayGroups(DBManager dbManager, Cursor group,String item) {
        ArrayList<SortedList> list = new ArrayList<>();
        if(item.equals("Genre")){
            group.moveToFirst();
            while(!group.isAfterLast()) {
                list.add(new SortedList(group.getString(group.getColumnIndex(dbManager.G_ID)),group.getString(group.getColumnIndex(dbManager.G_NAME)),null)); //add item
                group.moveToNext();
            }

        }else{
            int i = 1;
            group.moveToFirst();
            while(!group.isAfterLast()) {
                list.add(new SortedList(String.valueOf(i),group.getString(group.getColumnIndex(dbManager.G_NAME)),null)); //add item
                group.moveToNext();
                i++;
            }
        }
        return list;
    }

}