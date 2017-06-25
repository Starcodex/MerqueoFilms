package test.julian.merqueo.Presenters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import test.julian.merqueo.Models.Film;
import test.julian.merqueo.Presenters.Interfaces.Interfaces;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IPresenter;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IView;
import test.julian.merqueo.R;

import static test.julian.merqueo.Presenters.Helpers.HelperPresenter.convertArrayToString;

/**
 * Created by JulianStack on 24/06/2017.
 */

public class FilmDetailsPresenter implements IPresenter {

    Context FDContext;
    Activity FDActivity;
    private IView View;

    // objects
    TextView Title;
    TextView Overview;
    TextView Release;
    TextView Language;
    TextView Genres;
    ImageView Poster;

    // constructor
    public FilmDetailsPresenter(Context context, Activity activity, IView view){
        this.FDContext = context;
        this.FDActivity = activity;
        this.View = view;
    }

    @Override
    public void processView(View ly, Bundle bundle) {

        // find Objects
        Title = (TextView)ly.findViewById(R.id.title);
        Overview = (TextView)ly.findViewById(R.id.overview);
        Release = (TextView)ly.findViewById(R.id.release);
        Language = (TextView)ly.findViewById(R.id.language);
        Genres = (TextView)ly.findViewById(R.id.genres);
        Poster = (ImageView)ly.findViewById(R.id.poster);

        Film film = bundle.getParcelable("film");
        setValues(film);

    }

    // set Film Values
    private void setValues(Film film) {
        String lang = FDContext.getResources().getString(R.string.lang)+film.getOriginalLanguage();
        Language.setText(lang);
        Title.setText(film.getTitle());
        Overview.setText(film.getOverView());
        Release.setText(film.getReleaseDate());
        Genres.setText(convertArrayToString(film.getGenres()));
        Poster.setImageURI(Uri.parse(film.getDirectoryPath()));
    }
}
