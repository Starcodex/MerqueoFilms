package test.julian.merqueo.Presenters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;

import test.julian.merqueo.Models.Film;
import test.julian.merqueo.Presenters.Adapters.FilmsListAdapter;
import test.julian.merqueo.Models.DB.DBManager;
import test.julian.merqueo.Models.SortedList;
import test.julian.merqueo.Presenters.Adapters.SearchFilmsAdapter;
import test.julian.merqueo.Presenters.Asynctasks.getSearchFilm;
import test.julian.merqueo.Presenters.Interfaces.Interfaces;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IPresenter;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IView;
import test.julian.merqueo.Presenters.Service.BackgroundService;
import test.julian.merqueo.R;
import test.julian.merqueo.Views.FilmDetails;

import static android.view.View.GONE;

/**
 * Created by JulianStack on 17/06/2017.
 */

public class FilmsListPresenter implements IPresenter , Interfaces.UIMSG, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    public static String TAG = "FilmsListPresenter";

    // Load Filtered List
    public BroadcastReceiver filmsreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String status = bundle.getString("status");
            String groups = bundle.getString("groups");
            String message = bundle.getString("message");
            Log.d(TAG,message);
            if(status.equals("Finish")){
                Log.d(TAG,"Load List in View");
                Items.addAll(mBackground.getArray());
                adapter.notifyDataSetChanged();
            }
        }
    };


    // Bind Service
    BackgroundService mBackground;
    volatile boolean mServiceBound = false;

    Context FContext;
    Activity FActivity;
    private IView view;
    DBManager dbManager;

    FilmsListAdapter adapter;
    SearchFilmsAdapter searchAdapter;
    ArrayList<SortedList> Items = new ArrayList<>();
    ExpandableListView expandableListView;
    ArrayList<Film> Films = new ArrayList<>();
    ListView listView;


    // constructor
    public FilmsListPresenter(Context context, Activity activity, IView view, BackgroundService background, boolean bound) {
        this.FContext = context;
        this.FActivity = activity;
        this.mBackground = background;
        this.mServiceBound = bound;
        this.view = view;
    }


    @Override
    public void processView(View ly, Bundle bundle) {

        Log.d(TAG,"FilmsListPresenter");
        mBackground.setUI(this);
        expandableListView = (ExpandableListView)ly.findViewById(R.id.expandableListView);
        listView = (ListView)ly.findViewById(R.id.listview);
        dbManager = new DBManager(FContext);

        // get List From Background Service
        Items = mBackground.getArray();

        searchAdapter = new SearchFilmsAdapter(Films,FContext);
        listView.setAdapter(searchAdapter);
        listView.setVisibility(GONE);

        adapter = new FilmsListAdapter(FContext,Items);
        expandableListView.setAdapter(adapter);

        // show Film
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Film film = Items.get(i).getFilmList().get(i1);
                Bundle bundle = new Bundle();
                bundle.putParcelable("film",film);
                bundle.putString("title",film.getTitle());
                FActivity.startActivityForResult(new Intent(FActivity,FilmDetails.class).putExtras(bundle),FContext.getResources().getInteger(R.integer.film_results));
                return true;
            }
        });

        // show Film
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Film film = Films.get(i);
                Bundle bundle = new Bundle();
                bundle.putParcelable("film",film);
                bundle.putString("title",film.getTitle());
                FActivity.startActivityForResult(new Intent(FActivity,FilmDetails.class).putExtras(bundle),FContext.getResources().getInteger(R.integer.film_results));
            }
        });
    }


    // get Filtered List
    public void PopulateListByFilter(String item){
        Log.d(TAG,"PopulateList By Filter");
        if (Items != null) {
            Items.clear();
            adapter.notifyDataSetChanged();
        }
        mBackground.getFilteredList(item,FContext.getResources().getString(R.string.films_receiver),dbManager);
    }


    // Register Receivers
    public void registerReceivers(){
        Log.d(TAG,"RegisterReceivers");
        FContext.registerReceiver(filmsreceiver, new IntentFilter(FContext.getResources().getString(R.string.films_receiver)));
    }

    // unregister Receivers
    public void unregisterReceivers(){
        FContext.unregisterReceiver(filmsreceiver);
    }

    // update Ui if need it
    @Override
    public void updateMessageUI(String message) {

    }


    public void menuCreated(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        MenuItemCompat.setActionView(searchItem, searchView);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem,this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG,"Submit");
        // get Films
        new getSearchFilm(dbManager, FContext, query, new getSearchFilm.getSearchFilmInterface() {
            @Override
            public void Success(String message, ArrayList<Film> films) {
                Log.d(TAG,message);
                if(Films.size()>0){
                    Films.clear();
                }
                Films.addAll(films);
                searchAdapter.notifyDataSetChanged();
            }
            @Override
            public void Cancelled(String message) {

            }
        }).execute();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        return true;
    }

    // Hide ExpandableList
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        expandableListView.setVisibility(GONE);
        listView.setVisibility(View.VISIBLE);
        return true;
    }
    // Hide ListView
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        expandableListView.setVisibility(View.VISIBLE);
        listView.setVisibility(GONE);
        return true;
    }
}
