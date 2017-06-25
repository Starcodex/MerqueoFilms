package test.julian.merqueo.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by JulianStack on 18/06/2017.
 */

public class SortedList implements Parcelable {

    String Id;
    String Name;
    ArrayList<Film> FilmList = new ArrayList<>();

    public SortedList(String id, String name, ArrayList<Film> filmList) {
        Id = id;
        Name = name;
        FilmList = filmList;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<Film> getFilmList() {
        return FilmList;
    }

    public void setFilmList(ArrayList<Film> filmList) {
        FilmList = filmList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.Name);
        dest.writeList(this.FilmList);
    }

    protected SortedList(Parcel in) {
        this.Id = in.readString();
        this.Name = in.readString();
        this.FilmList = new ArrayList<Film>();
        in.readList(this.FilmList, Film.class.getClassLoader());
    }

    public static final Parcelable.Creator<SortedList> CREATOR = new Parcelable.Creator<SortedList>() {
        @Override
        public SortedList createFromParcel(Parcel source) {
            return new SortedList(source);
        }

        @Override
        public SortedList[] newArray(int size) {
            return new SortedList[size];
        }
    };
}
