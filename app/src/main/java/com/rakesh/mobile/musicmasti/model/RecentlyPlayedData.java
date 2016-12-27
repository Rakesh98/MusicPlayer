package com.rakesh.mobile.musicmasti.model;

import java.util.ArrayList;

/**
 * Created by rakesh.jnanagari on 21/12/16.
 */

public class RecentlyPlayedData {

    private ArrayList<Integer> recentlyPlayedList;

    public RecentlyPlayedData() {
    }

    public ArrayList<Integer> getRecentlyPlayedList() {
        return this.recentlyPlayedList;
    }

    public void setRecentlyPlayedList(ArrayList<Integer> recentlyPlayedList) {
        this.recentlyPlayedList = recentlyPlayedList;
    }
}
