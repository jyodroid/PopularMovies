package com.jyo.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnTangarife on 13/09/15.
 */
public class Trailer implements Parcelable {
    private String id;
    private String name;
    // for YouTube intent www.youtube.com/watch?v=key
    private String key;
    private boolean fromYouTube;
    private String site;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isFromYouTube() {
        return fromYouTube;
    }

    public void setFromYouTube(boolean fromYouTube) {
        this.fromYouTube = fromYouTube;
    }

    //Creator for Parcelable
    public static final Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {
        public Trailer createFromParcel(Parcel parcel) {
            Trailer trailer = new Trailer();
            trailer.setId(parcel.readString());
            trailer.setName(parcel.readString());
            trailer.setKey(parcel.readString());
            trailer.setFromYouTube(parcel.readByte() != 0);
            trailer.setSite(parcel.readString());
            return trailer;
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(key);
        dest.writeByte((byte) (isFromYouTube() ? 1 : 0));
        dest.writeString(site);
    }

}
