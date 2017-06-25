package test.julian.merqueo.Presenters.Asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import test.julian.merqueo.Models.Film;
import test.julian.merqueo.R;

/**
 * Created by JulianStack on 20/06/2017.
 */

public class getFile extends AsyncTask<String, Integer, String> {


    private static String TAG = "DownloadFile";
    Context context;
    String pathFile;
    String OutPath;
    DownloadInterface DInterface;
    int Pos;
    ArrayList<Film> Array = new ArrayList<>();

    public getFile(int pos,Context context, ArrayList<Film> array, DownloadInterface mInterface) {
        this.context = context;
        this.Array = array;
        this.pathFile = array.get(pos).getPosterPath();
        this.Pos = pos;
        this.DInterface = mInterface;
    }

    public interface DownloadInterface{
        void success (String message, String results,ArrayList<Film> Array, int pos);
        void failed (String message, int pos);
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(context.getResources().getString(R.string.movie_img)+pathFile);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            File direct = new File(Environment.getExternalStorageDirectory().getPath()+"/Pictures/Merqueo/");

            if(!direct.exists()) {
                if(direct.mkdir()); //directory is created;
            }
            OutPath = direct.getPath() + pathFile;
            input = connection.getInputStream();
            output = new FileOutputStream(OutPath);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {

                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            DInterface.failed(context.getResources().getString(R.string.request_failed),Pos);
            return e.toString();

        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return OutPath;
    }


    // Success
    @Override
    protected void onPostExecute(String outpath) {
        DInterface.success(context.getResources().getString(R.string.request_success),outpath,Array,Pos);
    }

    // Failed
    @Override
    protected void onCancelled(String message){
        DInterface.failed(context.getResources().getString(R.string.request_failed),Pos);
    }



}
