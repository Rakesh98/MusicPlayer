package com.rakesh.mobile.musicmasti.utils;

import java.util.ArrayList;

import com.rakesh.mobile.musicmasti.model.PlayListData;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import android.content.Context;

/**
 * Created by rakesh.jnanagari on 17/12/16.
 */

public class DBManager {

  private static final String DB_NAME = "MUSIC_DATA_BASE";
  private static final String RECENTLY_PLAYED_KEY = "RECENTLY_PLAYED_DB_KEY";
  private static final String PLAYLIST_KEY = "PLAY_LIST_DB_KEY";
  private static final String PLAYLIST_NAMES_KEY = "PLAY_LIST_NAMES_KEY";

  private DB mSnappyDb;

  public DBManager(Context context) {
    try {
      mSnappyDb = DBFactory.open(context, DB_NAME);
    } catch (SnappydbException e) {

    }
  }

  public ArrayList<String> getPlayListNames() {
    ArrayList<String> returnList = new ArrayList<>();
    try {
      String[] array = mSnappyDb.get(PLAYLIST_NAMES_KEY, String[].class);
      for (int i = 0; i < array.length; i++) {
        returnList.add(array[i]);
      }
    } catch (SnappydbException e) {

    }
    return returnList;
  }

  public void setPlayListNames(ArrayList<String> playListNames) {
    String[] array = new String[playListNames.size()];
    for (int i = 0; i < playListNames.size(); i++) {
      array[i] = playListNames.get(i);
    }
    try {
      mSnappyDb.put(PLAYLIST_NAMES_KEY, array);
    } catch (SnappydbException e) {

    }
  }

  public ArrayList<ArrayList<Integer>> getPlayList() {
    try {
      PlayListData playListData = mSnappyDb.getObject(PLAYLIST_KEY, PlayListData.class);
      return playListData.getPlayList();
    } catch (SnappydbException e) {

    }
    return new ArrayList<ArrayList<Integer>>();
  }

  public void setPlayList(ArrayList<ArrayList<Integer>> playList) {
    PlayListData playListData = new PlayListData();
    playListData.setPlayList(playList);
    try {
      mSnappyDb.put(PLAYLIST_KEY, playListData);
    } catch (SnappydbException e) {

    }
  }

  public ArrayList<Integer> getRecentlyPlayedList() {
    ArrayList<Integer> returnList = new ArrayList<>();
    try {
      int[] array = mSnappyDb.get(RECENTLY_PLAYED_KEY, int[].class);
      for (int i = 0; i < array.length; i++) {
        returnList.add(array[i]);
      }
    } catch (SnappydbException e) {

    }
    return returnList;
  }

  public void setRecentlyPlayedList(ArrayList<Integer> recentlyPlayedList) {
    try {
      int[] array = new int[recentlyPlayedList.size()];
      for (int i = 0; i < recentlyPlayedList.size(); i++) {
        array[i] = recentlyPlayedList.get(i);
      }
      mSnappyDb.put(RECENTLY_PLAYED_KEY, array);
    } catch (SnappydbException e) {

    }
  }
}
