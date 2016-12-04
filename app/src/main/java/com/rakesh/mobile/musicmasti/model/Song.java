package com.rakesh.mobile.musicmasti.model;

import android.net.Uri;

/**
 * Created by rakesh.jnanagari on 04/07/16.
 */
public class Song {
  private int id;
  private String displayName;
  private int albumId;
  private String album;
  private int artistId;
  private String artist;
  private String composer;
  private String data;
  private String dateAdded;
  private String duration;
  private boolean ringTone;
  private String size;
  private String title;
  private Uri contentUri;
  private Uri internalUri;
  private Uri externalUri;


  public Song() {

  }

  public Song(int id, String displayName, int albumId, String album, int artistId, String artist, String composer, String data, String dateAdded, String duration, boolean ringTone, String size, String title, Uri contentUri, Uri internalUri, Uri externalUri) {
    this.id = id;
    this.displayName = displayName;
    this.albumId = albumId;
    this.album = album;
    this.artistId = artistId;
    this.artist = artist;
    this.composer = composer;
    this.data = data;
    this.dateAdded = dateAdded;
    this.duration = duration;
    this.ringTone = ringTone;
    this.size = size;
    this.title = title;
    this.contentUri = contentUri;
    this.internalUri = internalUri;
    this.externalUri = externalUri;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public int getAlbumId() {
    return this.albumId;
  }

  public void setAlbumId(int albumId) {
    this.albumId = albumId;
  }

  public String getAlbum() {
    return this.album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  public int getArtistId() {
    return this.artistId;
  }

  public void setArtistId(int artistId) {
    this.artistId = artistId;
  }

  public String getArtist() {
    return this.artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getComposer() {
    return this.composer;
  }

  public void setComposer(String composer) {
    this.composer = composer;
  }

  public String getData() {
    return this.data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getDuration() {
    return this.duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public boolean isRingTone() {
    return this.ringTone;
  }

  public void setRingTone(boolean ringTone) {
    this.ringTone = ringTone;
  }

  public String getSize() {
    return this.size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Uri getContentUri() {
    return this.contentUri;
  }

  public void setContentUri(Uri contentUri) {
    this.contentUri = contentUri;
  }

  public Uri getInternalUri() {
    return this.internalUri;
  }

  public void setInternalUri(Uri internalUri) {
    this.internalUri = internalUri;
  }

  public Uri getExternalUri() {
    return this.externalUri;
  }

  public void setExternalUri(Uri externalUri) {
    this.externalUri = externalUri;
  }

  public String getDateAdded() {
    return this.dateAdded;
  }

  public void setDateAdded(String dateAdded) {
    this.dateAdded = dateAdded;
  }
}
