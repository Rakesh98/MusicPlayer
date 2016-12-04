package com.rakesh.mobile.musicmasti.model;

import java.util.Locale;

/**
 * Created by rakesh.jnanagari on 08/07/16.
 */
public class Album {
    private long albumId;
    private String title;
    private String subTitle;
    private int count;
    private String dateAdded;
    private int recentlyPlayedPosition = -1;

    public Album() {

    }

    public Album(long albumId, String title, String subTitle, int count, String dateAdded, int recentlyPlayedPosition) {
        this.albumId = albumId;
        this.title = title;
        this.subTitle = subTitle;
        this.count = count;
        this.dateAdded = dateAdded;
        this.recentlyPlayedPosition = recentlyPlayedPosition;
    }

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDateAdded() {
        return this.dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getRecentlyPlayedPosition() {
        return this.recentlyPlayedPosition;
    }

    public void setRecentlyPlayedPosition(int recentlyPlayedPosition) {
        this.recentlyPlayedPosition = recentlyPlayedPosition;
    }
}
