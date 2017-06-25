package test.julian.merqueo.Presenters.Adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import test.julian.merqueo.Models.Film;
import test.julian.merqueo.R;

import static test.julian.merqueo.Presenters.Helpers.HelperPresenter.convertArrayToString;

/**
 * Created by JulianStack on 24/06/2017.
 */

public class SearchFilmsAdapter extends BaseAdapter {

    ArrayList<Film> Films = new ArrayList<>();
    Context context;

    public SearchFilmsAdapter(ArrayList<Film> films, Context context) {
        Films = films;
        this.context = context;
    }

    @Override
    public int getCount() {
        return Films.size();
    }

    @Override
    public Object getItem(int i) {
        return Films.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listitem, null);
        }

        Film film = Films.get(i);

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
}
