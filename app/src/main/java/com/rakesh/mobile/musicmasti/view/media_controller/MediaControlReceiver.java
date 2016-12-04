package com.rakesh.mobile.musicmasti.view.media_controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.rakesh.mobile.musicmasti.view.PlayerService;

/**
 * Created by rakesh.jnanagari on 20/11/16.
 */

public class MediaControlReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == PlayerService.getInstance()) {
            return;
        }
        final KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event.getAction() != KeyEvent.ACTION_DOWN) return;

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_STOP:
                PlayerService.getInstance().pause();
                break;
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (PlayerService.getInstance().isPlayerPlaying()) {
                    PlayerService.getInstance().pause();
                } else {
                    if (PlayerService.getInstance().isSongPaused) {
                        PlayerService.getInstance().resume();
                    } else {
                        PlayerService.getInstance().playSong();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (PlayerService.getInstance().isPlayerPlaying()) {
                    PlayerService.getInstance().pause();
                } else {
                    if (PlayerService.getInstance().isSongPaused) {
                        PlayerService.getInstance().resume();
                    } else {
                        PlayerService.getInstance().playSong();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (PlayerService.getInstance().isPlayerPlaying()) {
                    PlayerService.getInstance().pause();
                } else {
                    if (PlayerService.getInstance().isSongPaused) {
                        PlayerService.getInstance().resume();
                    } else {
                        PlayerService.getInstance().playSong();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                PlayerService.getInstance().skipSong(true);
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                PlayerService.getInstance().skipSong(false);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                PlayerService.getInstance().changeVolume(true);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                PlayerService.getInstance().changeVolume(false);
                break;
        }
    }
}
