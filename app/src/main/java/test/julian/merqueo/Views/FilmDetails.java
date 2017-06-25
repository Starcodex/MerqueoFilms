package test.julian.merqueo.Views;

import android.app.Activity;
import android.os.Bundle;

import test.julian.merqueo.Presenters.FilmDetailsPresenter;
import test.julian.merqueo.Presenters.Interfaces.Interfaces;
import test.julian.merqueo.Presenters.Interfaces.Interfaces.IView;
import test.julian.merqueo.R;

/**
 * Created by JulianStack on 24/06/2017.
 */

public class FilmDetails extends Activity implements IView{

    // Presenter
    FilmDetailsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filmdetails);

        // Process View
        presenter = new FilmDetailsPresenter(this,this,this);
        Bundle bundle = getIntent().getExtras();
        presenter.processView(getWindow().getDecorView().findViewById(android.R.id.content),bundle);
    }

    @Override
    public void finishView() {
        finish();
    }
}
