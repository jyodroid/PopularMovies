package com.jyo.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 13/09/15.
 */
public class Review implements Parcelable {

    private String author;
    private String content;
    private String reviewId;

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //Creator for Parcelable
    public static final Parcelable.Creator<Review> CREATOR = new Creator<Review>(){
        public Review createFromParcel(Parcel parcel){
            Review review = new Review();
            review.setAuthor(parcel.readString());
            review.setContent(parcel.readString());
            review.setReviewId(parcel.readString());
            return review;
        }

        public Review[] newArray(int size){
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(reviewId);
    }
}
