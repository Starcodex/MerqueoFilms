package test.julian.merqueo.Presenters.Interfaces;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;

/**
 * Created by JulianStack on 17/06/2017.
 */

public class Interfaces {

    // Generic Presenter
    public interface IPresenter {
        void processView(View ly, Bundle bundle);

    }

    //
    public interface IView {
        void finishView();
    }
    // Update UI messages
    public interface UIMSG {
        void updateMessageUI(String message);
    }


}
