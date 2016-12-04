package com.rakesh.mobile.musicmasti.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ram on 10-02-2016.
 */
public class SharedPreference {

    public static final String PREFS_NAME = "Preferences";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreference instance = null;

    private SharedPreference(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreference getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreference(context);
        }
        return instance;
    }

    public void putSharedPref(String key, String value) {
        this.sharedPreferences.edit().putString(key, value).commit();
    }

    public void putSharedPrefInt(String key, int value) {
        this.sharedPreferences.edit().putInt(key, value).commit();
    }


    public void putSharedPrefLong(String key, long value) {
        this.sharedPreferences.edit().putLong(key, value).commit();
    }


    public void putSharedPrefBoolean(String key, Boolean value) {
        this.sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public String getSharedPref(String key) {
        return this.sharedPreferences.getString(key, null);
    }

    public int getSharedPrefInt(String key) {
        return this.sharedPreferences.getInt(key, 0);
    }

    public Long getSharedPrefLong(String key) {
        return this.sharedPreferences.getLong(key, 0);
    }

    public Boolean getSharedPrefBoolean(String key, boolean defaultValue) {
        return this.sharedPreferences.getBoolean(key, defaultValue);
    }

    public void clearSharedPrefs() {
        this.sharedPreferences.edit().clear().commit();
    }

    public boolean containsKey(String key) {
        return this.sharedPreferences.contains(key);
    }
}