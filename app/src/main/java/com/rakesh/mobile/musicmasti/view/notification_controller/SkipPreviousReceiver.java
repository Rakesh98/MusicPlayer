package com.rakesh.mobile.musicmasti.view.notification_controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.view.PlayerService;
import com.rakesh.mobile.musicmasti.view.Splash;

/**
 * Created by rakesh.jnanagari on 18/11/16.
 */

public class SkipPreviousReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != PlayerService.getInstance() && StaticData.songList != null && !StaticData.songList.isEmpty()) {
            PlayerService.getInstance().skipSong(false);
        } else {
            Intent intentSplash = new Intent(context, Splash.class);
            intentSplash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentSplash);
            PlayerService.getInstance().cancelNotification();
        }
    }
}
