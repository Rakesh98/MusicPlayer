package com.rakesh.mobile.musicmasti.view.notification_controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.view.MusicContainer;
import com.rakesh.mobile.musicmasti.view.NowPlaying;
import com.rakesh.mobile.musicmasti.view.PlayerService;
import com.rakesh.mobile.musicmasti.view.Splash;

/**
 * Created by rakesh.jnanagari on 18/11/16.
 */

public class CloseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != PlayerService.getInstance() && StaticData.songList != null && !StaticData.songList.isEmpty()) {
            PlayerService.getInstance().cancelNotification();
            if (MusicContainer.isActivityDestroyed && NowPlaying.isActivityDestroyed) {
                PlayerService.getInstance().stopSelf();
            }
        } else {
            PlayerService.getInstance().cancelNotification();
        }
    }
}
