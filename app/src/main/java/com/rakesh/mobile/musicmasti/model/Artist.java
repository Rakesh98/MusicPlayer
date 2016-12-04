package com.rakesh.mobile.musicmasti.model;

/**
 * Created by rakesh.jnanagari on 09/07/16.
 */
public class Artist {
    private int albumId;
    private String artist;
    private int count;

    public Artist() {

    }

    public Artist(int albumId, String artist, int count) {
        this.albumId = albumId;
        this.artist = artist;
        this.count = count;
    }

    public int getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
