package com.rakesh.mobile.musicmasti.view.notification_controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.view.PlayerService;

/**
 * Created by rakesh.jnanagari on 18/11/16.
 */

public class NotificationStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != PlayerService.getInstance()) {
            if (PlayerService.getInstance().isPlayerPlaying()) {
                PlayerService.getInstance().pause();
            } else {
                PlayerService.getInstance().resume();
            }
        }
    }
}
