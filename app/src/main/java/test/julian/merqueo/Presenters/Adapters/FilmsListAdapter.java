package test.julian.merqueo.Presenters.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import test.julian.merqueo.Models.DB.DBManager;
import test.julian.merqueo.Models.Film;
import test.julian.merqueo.Models.SortedList;
import test.julian.merqueo.R;

import static test.julian.merqueo.Presenters.Helpers.HelperPresenter.convertArrayToString;

/**
 * Created by JulianStack on 17/06/2017.
 */

public class FilmsListAdapter extends BaseExpandableListAdapter {

    private static String TAG = "FilmsListAdapter";

    ArrayList<SortedList> List = new ArrayList<>();
    DBManager dbManager;
    Context context;

    // constructor
    public FilmsListAdapter(Context contxt, ArrayList<SortedList> GList) {
        List.clear();
        this.context = contxt;
        dbManager  = new DBManager(context);
        this.List = GList;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return List.get(groupPosition).getFilmList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return Integer.parseInt(List.get(groupPosition).getFilmList().get(childPosition).getId());
    }

    // ChildView
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Film film = List.get(groupPosition).getFilmList().get(childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listitem, null);
        }

        // objects
        TextView title = (TextView) convertView.findViewById(R.id.filmtitle);
        TextView overview = (TextView) convertView.findViewById(R.id.filmoverview);
        TextView release = (TextView) convertView.findViewById(R.id.filmrelease);
        TextView genres = (TextView) convertView.findViewById(R.id.filmgenres);
        ImageView im = (ImageView) convertView.findViewById(R.id.imageView2);

        // Set Objects values
        title.setText(film.getTitle());
        overview.setText(film.getOverView());
        release.setText(film.getReleaseDate());
        genres.setText(convertArrayToString(film.getGenres()));


        if(film.getDirectoryPath()!=null){
            im.setImageURI(Uri.parse(film.getDirectoryPath()));
        }

        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return List.get(groupPosition).getFilmList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return List.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return List.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return Integer.parseInt(List.get(groupPosition).getId());
    }

    // Parent's view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        SortedList group = (SortedList) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.listgroup, null);
        }

        // set Title Group
        TextView tv = (TextView) convertView.findViewById(R.id.listTitle);
        tv.setText(group.getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}


