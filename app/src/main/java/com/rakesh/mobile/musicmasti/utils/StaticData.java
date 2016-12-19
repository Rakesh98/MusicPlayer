package com.rakesh.mobile.musicmasti.utils;

import android.content.Intent;

import com.rakesh.mobile.musicmasti.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh.jnanagari on 15/11/16.
 */

public class StaticData {
    public static List<Song> songList;
    private static List<Integer> playListSelected;
    private static ArrayList<Integer> recentlyPlayed;
    private static ArrayList<ArrayList<Integer>> playListLibrary;
    private static ArrayList<String> playListNames;
    public static int toolBarHeight;

    public static List<Integer> getPlayListSelected() {
        return StaticData.playListSelected;
    }

    public static void setPlayListSelected(List<Integer> playListSelected) {
        StaticData.playListSelected = playListSelected;
    }

    public static ArrayList<Integer> getRecentlyPlayed() {
        return StaticData.recentlyPlayed;
    }

    public static void setRecentlyPlayed(ArrayList<Integer> recentlyPlayed) {
        StaticData.recentlyPlayed = recentlyPlayed;
    }

    public static ArrayList<String> getPlayListNames() {
        return StaticData.playListNames;
    }

    public static void setPlayListNames(ArrayList<String> playListNames) {
        StaticData.playListNames = playListNames;
    }

    public static ArrayList<ArrayList<Integer>> getPlayListLibrary() {
        return StaticData.playListLibrary;
    }

    public static void setPlayListLibrary(ArrayList<ArrayList<Integer>> playListLibrary) {
        StaticData.playListLibrary = playListLibrary;
    }
}
