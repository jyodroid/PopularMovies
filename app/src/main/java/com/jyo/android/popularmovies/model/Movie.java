package com.jyo.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 5/08/15.
 */
public class Movie implements Parcelable {

    //Attributes
    private String movieID;
    private String posterURL;
    private String title;
    private String plot;
    private double rating;
    private String releaseDate;
    private byte[] posterBA;

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

    public byte[] getPosterBA() {
        return posterBA;
    }

    public void setPosterBM(byte[] posterBA) {
        this.posterBA = posterBA;
    }

    //Creator for Parcelable
    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>(){
        public Movie createFromParcel(Parcel parcel){
            Movie movie = new Movie();
            movie.setMovieID(parcel.readString());
            movie.setPosterURL(parcel.readString());
            movie.setTitle(parcel.readString());
            movie.setPlot(parcel.readString());
            movie.setRating(parcel.readDouble());
            movie.setReleaseDate(parcel.readString());
//            int parcelInt = parcel.readInt();
//            if (parcelInt < 0){
//                parcelInt = 0;
//            }
//            byte[] posterBA = new byte[parcelInt];
//            parcel.createByteArray();
//            parcel.readByteArray(posterBA);
            movie.setPosterBM(parcel.createByteArray());
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
        dest.writeString(movieID);
        dest.writeString(posterURL);
        dest.writeString(title);
        dest.writeString(plot);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeByteArray(posterBA);
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }
}
