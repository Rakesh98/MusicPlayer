package com.rakesh.mobile.musicmasti;

import android.app.Application;

import com.rakesh.mobile.musicmasti.utils.DBManager;

/**
 * Created by rakesh.jnanagari on 24/06/16.
 */
public class AppController extends Application{

    public DBManager mDBManager;
    private static AppController mAppController;

    public static AppController getInstance () {
        return mAppController;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mAppController = this;
        mDBManager = new DBManager(getApplicationContext());
    }


    private void initApp() {

    }
}
