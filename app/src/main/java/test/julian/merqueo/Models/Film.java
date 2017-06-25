package test.julian.merqueo.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JulianStack on 19/06/2017.
 */

public class Film implements Parcelable {

    String Id;
    String Title;
    String VoteCount;
    String VoteAverage;
    String OriginalLanguage;
    String PosterPath;
    String OverView;
    String ReleaseDate;
    String[] Genres;
    String DirectoryPath;
    boolean Downloaded;



    public Film(String id, String title, String voteCount,String voteAverage, String originalLanguage, String posterPath, String img,  boolean downloaded,  String[] genres, String overView, String releaseDate) {
        Id = id;
        Title = title;
        VoteCount = voteCount;
        VoteAverage = voteAverage;
        OriginalLanguage = originalLanguage;
        DirectoryPath = img;
        PosterPath = posterPath;
        OverView = overView;
        ReleaseDate = releaseDate;
        Genres = genres;
        Downloaded = downloaded;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getVoteCount() {
        return VoteCount;
    }

    public void setVoteCount(String voteCount) {
        VoteCount = voteCount;
    }

    public String getOriginalLanguage() {
        return OriginalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        OriginalLanguage = originalLanguage;
    }

    public String getDirectoryPath() {
        return DirectoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        DirectoryPath = directoryPath;
    }

    public String getPosterPath() {
        return PosterPath;
    }

    public void setPosterPath(String posterPath) {
        PosterPath = posterPath;
    }

    public String getOverView() {
        return OverView;
    }

    public void setOverView(String overView) {
        OverView = overView;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate = releaseDate;
    }

    public boolean isDownloaded() {
        return Downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        Downloaded = downloaded;
    }

    public String[] getGenres() {
        return Genres;
    }

    public void setGenres(String[] genres) {
        Genres = genres;
    }

    public String getVoteAverage() {
        return VoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        VoteAverage = voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.Title);
        dest.writeString(this.VoteCount);
        dest.writeString(this.VoteAverage);
        dest.writeString(this.OriginalLanguage);
        dest.writeString(this.PosterPath);
        dest.writeString(this.OverView);
        dest.writeString(this.ReleaseDate);
        dest.writeStringArray(this.Genres);
        dest.writeString(this.DirectoryPath);
        dest.writeByte(this.Downloaded ? (byte) 1 : (byte) 0);
    }

    protected Film(Parcel in) {
        this.Id = in.readString();
        this.Title = in.readString();
        this.VoteCount = in.readString();
        this.VoteAverage = in.readString();
        this.OriginalLanguage = in.readString();
        this.PosterPath = in.readString();
        this.OverView = in.readString();
        this.ReleaseDate = in.readString();
        this.Genres = in.createStringArray();
        this.DirectoryPath = in.readString();
        this.Downloaded = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Film> CREATOR = new Parcelable.Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel source) {
            return new Film(source);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };
}
