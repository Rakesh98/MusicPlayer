package com.rakesh.mobile.musicmasti.model;

import java.util.List;

/**
 * Created by rakesh.jnanagari on 17/07/16.
 */
public class Queue {
    private Song song;
    private boolean isPlayed;

    public Queue() {
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }
}
