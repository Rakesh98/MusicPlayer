package com.rakesh.mobile.musicmasti.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.comparators.SongComparator;
import com.rakesh.mobile.musicmasti.utils.SharedPreference;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rakesh.jnanagari on 24/06/16.
 */
public class Splash extends AppCompatActivity {
  private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
  private Splash sourceContext;
  private SharedPreference mSharedPreference;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    sourceContext = this;
    mSharedPreference = SharedPreference.getInstance(this);
    initApp();
    initConfiguration();
    getScreenSize();
    Utils.initGridItemSize(this);
    initPlayList();
    initRecentlyPlayed();
    if (ContextCompat.checkSelfPermission(sourceContext,
        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      showPermissionDialog();
    } else {
      readMedia();
      if (null == PlayerService.getInstance() || !PlayerService.isServiceRunning) {
        Intent intent = new Intent(Splash.this, PlayerService.class);
        startService(intent);
      }
      startActivity(new Intent(this, MusicContainer.class));
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE == requestCode) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(sourceContext, getString(R.string.permission_granted_thank_message),
            Toast.LENGTH_SHORT).show();
        readMedia();
        if (null == PlayerService.getInstance() || !PlayerService.isServiceRunning) {
          Intent intent = new Intent(Splash.this, PlayerService.class);
          startService(intent);
        }
        startActivity(new Intent(this, MusicContainer.class));
      } else {
        Toast.makeText(sourceContext, getString(R.string.permission_denied_error_message),
            Toast.LENGTH_LONG).show();
        finish();
      }
    }
  }

  private void initApp() {
    if(getResources().getBoolean(R.bool.isTablet)) {
      StaticData.toolBarHeight = (int) (112 * getResources().getDisplayMetrics().density);
    } else {
      StaticData.toolBarHeight = (int) (104 * getResources().getDisplayMetrics().density);
    }
  }

  private void initPlayList() {
    if (mSharedPreference.containsKey(Constants.PLAY_LIST_SELECTED)) {
      StaticData.setPlayListSelected(Utils.jsonToList(mSharedPreference.getSharedPref(Constants.PLAY_LIST_SELECTED)));
    } else {
      StaticData.setPlayListSelected(new ArrayList<Integer>());
    }
  }

  private void initRecentlyPlayed() {
    if (mSharedPreference.containsKey(Constants.RECENTLY_PLAYED_KEY)) {
      StaticData.setRecentlyPlayed(Utils.jsonToList(mSharedPreference.getSharedPref(Constants.RECENTLY_PLAYED_KEY)));
    } else {
      StaticData.setRecentlyPlayed(new ArrayList<Integer>());
    }
  }

  private void showPermissionDialog() {
    new AlertDialog.Builder(sourceContext).setTitle(getString(R.string.permission_dialog_title))
        .setMessage(getString(R.string.permission_dialog_message))
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            ActivityCompat.requestPermissions(sourceContext,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
          }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(sourceContext, getString(R.string.permission_denied_error_message),
                Toast.LENGTH_LONG).show();
            finish();
          }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
  }

  private void readMedia() {
    if (StaticData.songList == null) {
      StaticData.songList = new ArrayList<>();
    } else {
      StaticData.songList.clear();
    }
    ContentResolver mContentResolver = getContentResolver();
    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
    String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
    Cursor cursor = mContentResolver.query(uri, null, selection, null, sortOrder);
    int count;

    if (cursor != null) {
      count = cursor.getCount();
      if (count > 0) {
        Song song;
        while (cursor.moveToNext()) {
          song = new Song();
          song.setId(Integer
              .parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))));
          song.setDisplayName(
              cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
          song.setAlbumId(Integer
              .parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));
          song.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
          song.setArtistId(Integer
              .parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))));
          song.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
          song.setComposer(
              cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)));
          if (null != song.getComposer()) {
            song.setComposer(song.getComposer().replace("Music : ", ""));
            song.setComposer(song.getComposer().replace("Music: ", ""));
          }
          song.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
          song.setDuration(
              cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
          song.setDateAdded(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));
          song.setRingTone(Boolean.parseBoolean(
              cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE))));
          song.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
          song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
          song.setContentUri(
              Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
          Uri uri1 = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
//          song.setInternalUri(Uri.parse(cursor.getString(
//              cursor.getColumnIndex(MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString()))));
//          song.setExternalUri(Uri.parse(cursor.getString(
//              cursor.getColumnIndex(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()))));
          StaticData.songList.add(song);

          Log.d("debug", "stop");
        }
        Collections.sort(StaticData.songList, new SongComparator());
      }
    }
    cursor.close();
  }

  public void getScreenSize() {
    if (mSharedPreference.containsKey(Constants.POTRAIT_GRID_COUNT_KEY)
        && mSharedPreference.containsKey(Constants.LANDSCAPE_GRID_COUNT_KEY)
        && mSharedPreference.containsKey(Constants.POTRAIT_LIST_COUNT_KEY)
        && mSharedPreference.containsKey(Constants.LANDSCAPE_LIST_COUNT_KEY)) {
      Configuration.potrateGridCount =
          mSharedPreference.getSharedPrefInt(Constants.POTRAIT_GRID_COUNT_KEY);
      Configuration.landscapeGridCount =
          mSharedPreference.getSharedPrefInt(Constants.LANDSCAPE_GRID_COUNT_KEY);
      Configuration.potrateListCount =
          mSharedPreference.getSharedPrefInt(Constants.POTRAIT_LIST_COUNT_KEY);
      Configuration.landscapeListCount =
          mSharedPreference.getSharedPrefInt(Constants.LANDSCAPE_LIST_COUNT_KEY);
    } else {
      setScreenSize();
      mSharedPreference.putSharedPrefInt(Constants.POTRAIT_GRID_COUNT_KEY,
          Configuration.potrateGridCount);
      mSharedPreference.putSharedPrefInt(Constants.LANDSCAPE_GRID_COUNT_KEY,
          Configuration.landscapeGridCount);
      mSharedPreference.putSharedPrefInt(Constants.POTRAIT_LIST_COUNT_KEY,
          Configuration.potrateListCount);
      mSharedPreference.putSharedPrefInt(Constants.LANDSCAPE_LIST_COUNT_KEY,
          Configuration.landscapeListCount);
    }
  }

  public void setScreenSize() {
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    float yInches = metrics.heightPixels / metrics.ydpi;
    float xInches = metrics.widthPixels / metrics.xdpi;
    double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
    if (diagonalInches < 4.4) {
      Configuration.potrateGridCount = 2;
      Configuration.landscapeGridCount = 3;
      Configuration.potrateListCount = 1;
      Configuration.landscapeListCount = 1;
    } else if (diagonalInches < 5) {
      Configuration.potrateGridCount = 3;
      Configuration.landscapeGridCount = 5;
      Configuration.potrateListCount = 1;
      Configuration.landscapeListCount = 2;
    } else if (diagonalInches <= 6.5) {
      Configuration.potrateGridCount = 3;
      Configuration.landscapeGridCount = 5;
      Configuration.potrateListCount = 1;
      Configuration.landscapeListCount = 2;
    } else if (diagonalInches <= 8) {
      Configuration.potrateGridCount = 4;
      Configuration.landscapeGridCount = 6;
      Configuration.potrateListCount = 2;
      Configuration.landscapeListCount = 3;
    } else {
      Configuration.potrateGridCount = 5;
      Configuration.landscapeGridCount = 7;
      Configuration.potrateListCount = 2;
      Configuration.landscapeListCount = 3;
    }
  }

  private void initConfiguration() {
    Configuration.gridView = mSharedPreference.getSharedPrefBoolean(Constants.GRID_VIEW_KEY, true);
    Configuration.albums = mSharedPreference.getSharedPrefBoolean(Constants.ALBUMS_KEY, true);
    Configuration.allSongs = mSharedPreference.getSharedPrefBoolean(Constants.ALL_SONGS_KEY, true);
    Configuration.composers = mSharedPreference.getSharedPrefBoolean(Constants.COMPOSERS_KEY, true);
    Configuration.playlists = mSharedPreference.getSharedPrefBoolean(Constants.PLAYLIST_KEY, true);
    Configuration.isShakeSongSkipEnabled = mSharedPreference.getSharedPrefBoolean(Constants.IS_SHAKE_SKIP_SONG_KEY, false);
    Configuration.isHeadSetControlEnabled = mSharedPreference.getSharedPrefBoolean(Constants.IS_HEAD_SET_CONTROL_KEY, true);
    Configuration.sortType = mSharedPreference.getSharedPrefInt(Constants.SORT_KEY);
  }
}
