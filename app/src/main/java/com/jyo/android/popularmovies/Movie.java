package com.jyo.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 5/08/15.
 */
public class Movie implements Parcelable {

    //Attributes
    private String posterURL;
    private String title;
    private String plot;
    private double rating;
    private String releaseDate;

    //Getters and setters
    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    //Creator for Parcelable
    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>(){
        public Movie createFromParcel(Parcel parcel){
            Movie movie = new Movie();
            movie.setPosterURL(parcel.readString());
            movie.setTitle(parcel.readString());
            movie.setPlot(parcel.readString());
            movie.setRating(parcel.readDouble());
            movie.setReleaseDate(parcel.readString());
            return movie;
        }

        public Movie[] newArray(int size){
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterURL);
        dest.writeString(title);
        dest.writeString(plot);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
    }
}
