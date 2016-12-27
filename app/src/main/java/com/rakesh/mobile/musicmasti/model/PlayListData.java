package com.rakesh.mobile.musicmasti.model;

import java.util.ArrayList;

/**
 * Created by rakesh.jnanagari on 19/12/16.
 */

public class PlayListData {
  private ArrayList<ArrayList<Integer>> playList;

  public PlayListData() {}

  public PlayListData(ArrayList<ArrayList<Integer>> playList) {
    this.playList = playList;
  }

  public ArrayList<ArrayList<Integer>> getPlayList() {
    return this.playList;
  }

  public void setPlayList(ArrayList<ArrayList<Integer>> playList) {
    this.playList = playList;
  }
}
