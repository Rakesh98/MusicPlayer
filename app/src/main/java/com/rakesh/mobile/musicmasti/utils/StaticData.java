package com.rakesh.mobile.musicmasti.utils;

import android.content.Intent;

import com.rakesh.mobile.musicmasti.model.Song;

import java.util.List;

/**
 * Created by rakesh.jnanagari on 15/11/16.
 */

public class StaticData {
    public static List<Song> songList;
    private static List<Integer> playListSelected;
    private static List<Integer> recentlyPlayed;
    public static int toolBarHeight;

    public static List<Integer> getPlayListSelected() {
        return StaticData.playListSelected;
    }

    public static void setPlayListSelected(List<Integer> playListSelected) {
        StaticData.playListSelected = playListSelected;
    }

    public static List<Integer> getRecentlyPlayed() {
        return StaticData.recentlyPlayed;
    }

    public static void setRecentlyPlayed(List<Integer> recentlyPlayed) {
        StaticData.recentlyPlayed = recentlyPlayed;
    }

}
