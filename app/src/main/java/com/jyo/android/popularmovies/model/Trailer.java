package com.jyo.android.popularmovies.model;

/**
 * Created by JohnTangarife on 13/09/15.
 */
public class Trailer {
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
}
