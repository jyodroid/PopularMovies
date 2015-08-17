package com.jyo.android.popularmovies;

import java.io.Serializable;

/**
 * Created by JohnTangarife on 5/08/15.
 */
public class Movie implements Serializable {

    private static final long serialVersionUID = 1;

    private String posterURL;
    private String title;
    private String plot;
    private double rating;
    private String releaseDate;

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
}
