package com.rakesh.mobile.musicmasti.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Queue;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.ShakeDetector;
import com.rakesh.mobile.musicmasti.utils.SharedPreference;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.media_controller.MediaControlReceiver;
import com.rakesh.mobile.musicmasti.view.notification_controller.CloseReceiver;
import com.rakesh.mobile.musicmasti.view.notification_controller.NotificationStatusReceiver;
import com.rakesh.mobile.musicmasti.view.notification_controller.SkipNextReceiver;
import com.rakesh.mobile.musicmasti.view.notification_controller.SkipPreviousReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by rakesh.jnanagari on 17/07/16.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {

  public List<Queue> mQueue;
  private int playingSongPosition;
  private MediaPlayer mMediaPlayer;
  public int songProgress;
  private Handler mHandler;
  public static boolean isServiceRunning;
  public boolean isRepeatOn = false;
  public boolean isSongPaused = false;
  private PlayerStatusUpdate mPlayerStatusUpdate, mPlayerStatusUpdateSongList;

  private final int NOTIFICATION_ID = 903761647;
  private NotificationTarget notificationTarget;
  private RemoteViews remoteViews;
  private RemoteViews expandedView;
  private NotificationCompat.Builder mBuilder;
  private Notification notification;
  private NotificationManager mNotificationManager;
  private Context context;

  private AudioManager mAudioManager;
  private boolean audioManagerFlag = false;
  private Runnable mUpdateTimeTask;
  private NowPlayingInterface mNowPlayingInterface;

  private NotificationStatusReceiver mNotificationClickReceiver;
  private SkipPreviousReceiver mSkipPreviousReceiver;
  private SkipNextReceiver mSkipNextReceiver;
  private CloseReceiver mCloseReceiver;
  private static PlayerService selfReference;

  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private ShakeDetector mShakeDetector;

  @Override
  public void onCreate() {
    super.onCreate();
    isServiceRunning = true;
    context = this;
    selfReference = this;
    initService();
    initNotification();
    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
      @Override
      public void onAudioFocusChange(int focusChange) {
        if(focusChange<=0) {
          if(isPlayerPlaying()) {
            audioManagerFlag = true;
          }
          pause();
        } else {
          if(audioManagerFlag) {
            resume();
            audioManagerFlag = false;
          }
        }
      }
    }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    if (Configuration.isHeadSetControlEnabled) {
      registerHeadSetController();
    }
    registerReceivers();

    if (Configuration.isShakeSongSkipEnabled) {
      registerShakeLisitner();
    }
  }

  public static PlayerService getInstance() {
    return selfReference;
  }

  public void registerHeadSetController() {
    if (null != context) {
      AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
      mAudioManager.registerMediaButtonEventReceiver(new ComponentName(context, MediaControlReceiver.class));
    }
  }

  public void unRegisterHeadSetController() {
    if (null != context) {
      AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
      mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(context, MediaControlReceiver.class));
    }
  }


  private void registerReceivers() {
    mNotificationClickReceiver = new NotificationStatusReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_PLAY_STATUS);
    registerReceiver(mNotificationClickReceiver, filter);

    mSkipPreviousReceiver = new SkipPreviousReceiver();
    filter = new IntentFilter();
    filter.addAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_PREVIOUS);
    registerReceiver(mSkipPreviousReceiver, filter);

    mSkipNextReceiver = new SkipNextReceiver();
    filter = new IntentFilter();
    filter.addAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_NEXT);
    registerReceiver(mSkipNextReceiver, filter);

    mCloseReceiver = new CloseReceiver();
    filter = new IntentFilter();
    filter.addAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_CLOSE);
    registerReceiver(mCloseReceiver, filter);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  public void unregisterReceivers() {
    unregisterReceiver(mNotificationClickReceiver);
    unregisterReceiver(mSkipNextReceiver);
    unregisterReceiver(mSkipPreviousReceiver);
    unregisterReceiver(mCloseReceiver);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mMediaPlayer.release();
    unregisterReceivers();
    if (Configuration.isShakeSongSkipEnabled) {
      unRegisterShakeLisitner();
    }
    if (Configuration.isHeadSetControlEnabled) {
      unRegisterHeadSetController();
    }
    cancelNotification();
    mHandler.removeCallbacksAndMessages(null);
    isServiceRunning = false;
    StaticData.songList = null;
    selfReference = null;
  }

  private void initService() {
    mQueue = new ArrayList<>();
    playingSongPosition = -1;
    mMediaPlayer = new MediaPlayer();
    mMediaPlayer.setOnCompletionListener(this);
    mHandler = new Handler();

  }

  public void addToQueue(List<Queue> queueList, boolean emptyPreviousQueue, int position) {
    if (queueList.size() == 0)
      return;
    if (mQueue.size() == 0) {
      mQueue.addAll(queueList);
      if (-1 == playingSongPosition)
        playingSongPosition = 0;
      else
        playingSongPosition = position;
      playSong();
    } else {
      if (emptyPreviousQueue) {
        mQueue.clear();
        if (position != -1)
          playingSongPosition = position;
        else
          playingSongPosition = 0;
      }
      mQueue.addAll(queueList);
      if (emptyPreviousQueue) {
        playSong();
      } else {
        updateBottomLayout();
        updateNotification();
      }
    }

  }

  public void playSong() {
    isSongPaused = false;
    if (-1 != playingSongPosition && playingSongPosition < mQueue.size()) {
      try {
        mMediaPlayer.reset();
        mMediaPlayer
            .setDataSource(mQueue.get(playingSongPosition).getSong().getContentUri().getPath());
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        mQueue.get(playingSongPosition).setPlayed(true);
        songProgress = 0;
        if (StaticData.getRecentlyPlayed().contains(mQueue.get(playingSongPosition).getSong().getId())) {
          StaticData.getRecentlyPlayed().remove(new Integer(mQueue.get(playingSongPosition).getSong().getId()));
        }
        StaticData.getRecentlyPlayed().add(0, mQueue.get(playingSongPosition).getSong().getId());
        if (StaticData.getRecentlyPlayed().size() >= Configuration.RECENTLY_PLAYED_STORE_LIMIT) {
          StaticData.getRecentlyPlayed().remove(StaticData.getRecentlyPlayed().size() - 1);
        }
        SharedPreference.getInstance(context).putSharedPref(Constants.RECENTLY_PLAYED_KEY, Utils.listToString(StaticData.getRecentlyPlayed()));

      } catch (IOException e) {
        e.printStackTrace();
      }
      updateBottomLayout();
      updateNotification();
    }

  }

  public void updateBottomLayout() {
    boolean next = false;
    boolean previous = false;
    if(-1 != playingSongPosition) {
      next = playingSongPosition < mQueue.size() - 1;
      previous = playingSongPosition > 0;
    }
    if(null != mPlayerStatusUpdate) {
      if (-1 != playingSongPosition) {
        mPlayerStatusUpdate.playingSongStatus(mQueue.get(playingSongPosition), next, previous);
      } else {
        mPlayerStatusUpdate.playingSongStatus(null, false, false);
      }
    }
    if(null != mPlayerStatusUpdateSongList) {
      if (-1 != playingSongPosition) {
        mPlayerStatusUpdateSongList.playingSongStatus(mQueue.get(playingSongPosition), next, previous);
      } else {
        mPlayerStatusUpdateSongList.playingSongStatus(null, false, false);
      }
    }
    if(null != mNowPlayingInterface && -1 != playingSongPosition) {
      mNowPlayingInterface.updateSong(mQueue.get(playingSongPosition).getSong(), next, previous);
      if(null != mUpdateTimeTask) {
        mHandler.postDelayed(mUpdateTimeTask, 100);
      }
    }
  }

  public void pause() {
    isSongPaused = true;
    if (isPlayerPlaying() && null != mMediaPlayer) {
      mMediaPlayer.pause();
      updateBottomLayout();
      updateNotification();
    }
    if(null != mNowPlayingInterface) {
      if(null != mUpdateTimeTask) {
        mHandler.postDelayed(mUpdateTimeTask, 100);
      }
    }
  }

  public void resume() {
    isSongPaused = false;
    if (!isPlayerPlaying()) {
      mMediaPlayer.start();
      updateBottomLayout();
      updateNotification();
    } else {
      playSong();
    }
    if(null != mNowPlayingInterface) {
      if(null != mUpdateTimeTask) {
        mHandler.postDelayed(mUpdateTimeTask, 100);
      }
    }
  }

  public void skipSong(boolean isNext) {
    if (isNext) {
      if (playingSongPosition < mQueue.size() - 1) {
        playingSongPosition++;
        playSong();
      }
    } else {
      if (playingSongPosition > 0) {
        playingSongPosition--;
        playSong();
      }
    }

  }

  public boolean isPlayerPlaying() {
    if (null != mMediaPlayer) {
      return mMediaPlayer.isPlaying();
    }
    return false;
  }

  public boolean isServiceInRunning() {
    return isServiceRunning;
  }

  @Override
  public void onCompletion(MediaPlayer mediaPlayer) {
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if(-1 != playingSongPosition) {
          mQueue.get(playingSongPosition).setPlayed(true);
          if (playingSongPosition < mQueue.size() - 1) {
            playingSongPosition++;
            playSong();
          } else {
            playingSongPosition = 0;
            if (!mQueue.get(playingSongPosition).isPlayed() || isRepeatOn) {
              playSong();
            }
          }
        }
      }
    }, 3000);
  }

  public void setSeekPosition(int position) {
    mMediaPlayer.seekTo(position);
  }

  public int getSeekPosition() {
   return  mMediaPlayer.getDuration();
  }

  public void setPlayerStatusUpdateLisitner(PlayerStatusUpdate playerStatus) {
    mPlayerStatusUpdate = playerStatus;

    if (-1 != playingSongPosition) {
      updateBottomLayout();
      updateNotification();
    } else {
      mPlayerStatusUpdate.playingSongStatus(null, false, false);
    }
  }

  public void setPlayerStatusUpdateUnregister() {
    mPlayerStatusUpdate = null;
  }

  public void setSongListUpdateLisitner(PlayerStatusUpdate playerStatusUpdate) {
    mPlayerStatusUpdateSongList = playerStatusUpdate;

    if (-1 != playingSongPosition) {
      updateBottomLayout();
      updateNotification();
    } else {
      mPlayerStatusUpdateSongList.playingSongStatus(null, false, false);
    }
  }

  public void setSongListUnregister() {
    mPlayerStatusUpdateSongList = null;
  }

  public void playAtPositionFromQueue(int position) {
    if (position >= 0 && position < mQueue.size()) {
      playingSongPosition = position;
      playSong();
    }
  }

  public void setmNowPlayingInterface(NowPlayingInterface nowPlayingInterface) {
    mNowPlayingInterface = nowPlayingInterface;
    if(-1 != playingSongPosition) {
      updateBottomLayout();
      updateNotification();
    }
    mUpdateTimeTask = new Runnable() {
      public void run() {
        long totalDuration = mMediaPlayer.getDuration();
        long currentDuration = mMediaPlayer.getCurrentPosition();
        if(null != mNowPlayingInterface) {
          mNowPlayingInterface.updateSongProgress(currentDuration, totalDuration);
          if(isPlayerPlaying()) {
            mHandler.postDelayed(this, 1000);
          }
        }
      }
    };
    if(isPlayerPlaying()) {
      mHandler.postDelayed(mUpdateTimeTask, 1000);
    }
  }

  public void unSetNowPlayingInterface() {
    mNowPlayingInterface = null;
  }

  public void deleteSongFromQueue(int position) {
    if (position < mQueue.size() && position >= 0) {
      boolean currentState = mMediaPlayer.isPlaying();
      boolean shouldPlay = (position == playingSongPosition);
      if (playingSongPosition == position && currentState) {
        mMediaPlayer.reset();
      }
      mQueue.remove(position);
      if (mQueue.size() == 0) {
        playingSongPosition = -1;
      } else if (playingSongPosition >= mQueue.size()) {
        playingSongPosition--;
      }
      if (currentState && -1 != playingSongPosition && shouldPlay) {
        playSong();
      } else {
        updateBottomLayout();
        updateNotification();
      }
    }

  }

  public int getPlayingSongPosition() {
    return playingSongPosition;
  }

  public interface PlayerStatusUpdate {
    void playingSongStatus(Queue song, boolean enableNext, boolean enablePrevious);
  }

  public interface NowPlayingInterface {
    void updateSong(Song song, boolean enableNext, boolean enablePrevious);
    void updateSongProgress(long currentTime, long endTime);
  }

  private void initNotification() {
    remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    expandedView =
            new RemoteViews(context.getPackageName(), R.layout.notification_layout_expanded);

  }

  private void updateNotification() {
    if (-1 != playingSongPosition) {
      remoteViews.setTextViewText(R.id.title,
              mQueue.get(playingSongPosition).getSong().getDisplayName());
      Intent resultIntent = new Intent(context, NowPlaying.class);
      resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent resultPendingIntent =
              PendingIntent.getActivity(context, 0, resultIntent,
                      PendingIntent.FLAG_UPDATE_CURRENT);
      Intent playIntent = new Intent();
      playIntent.setAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_PLAY_STATUS);
      PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      Intent nextIntent = new Intent();
      nextIntent.setAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_NEXT);
      PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      Intent previousIntent = new Intent();
      previousIntent.setAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_PREVIOUS);
      PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 3, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      Intent closeIntent = new Intent();
      closeIntent.setAction(Constants.PACKAGE_NAME + Constants.NOTIFICATION_CLOSE);
      PendingIntent closePendingIntent = PendingIntent.getBroadcast(context, 4, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      remoteViews.setOnClickPendingIntent(R.id.skip_previous_song, previousPendingIntent);
      remoteViews.setOnClickPendingIntent(R.id.skip_next_song, nextPendingIntent);
      remoteViews.setOnClickPendingIntent(R.id.status_song, playPendingIntent);
      remoteViews.setOnClickPendingIntent(R.id.close_notification, closePendingIntent);


      if (isPlayerPlaying()) {
        remoteViews.setImageViewResource(R.id.status_song, R.drawable.notification_pause);
        remoteViews.setViewVisibility(R.id.close_notification, View.INVISIBLE);
      } else {
        remoteViews.setImageViewResource(R.id.status_song, R.drawable.notification_play);
        remoteViews.setViewVisibility(R.id.close_notification, View.VISIBLE);
      }

      if(playingSongPosition == 0) {
        remoteViews.setViewVisibility(R.id.skip_previous_song, View.INVISIBLE);
      } else {
        remoteViews.setViewVisibility(R.id.skip_previous_song, View.VISIBLE);
      }
      if(playingSongPosition == mQueue.size() - 1) {
        remoteViews.setViewVisibility(R.id.skip_next_song, View.INVISIBLE);
      } else {
        remoteViews.setViewVisibility(R.id.skip_next_song, View.VISIBLE);
      }
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
      builder.setContentIntent(resultPendingIntent);
      builder.setSmallIcon(R.drawable.music_icon);
      builder.setAutoCancel(false);
      builder.setPriority(Notification.PRIORITY_MAX);
      if(isPlayerPlaying()) {
        builder.setOngoing(true);
      }
      notification = builder.build();
      notification.contentView = remoteViews;
      remoteViews.setImageViewResource(R.id.notification_image, R.drawable.music_icon);
      Uri uriPath = ContentUris.withAppendedId(Utils.ART_WORK_URI,
              mQueue.get(playingSongPosition).getSong().getAlbumId());

      if (Build.VERSION.SDK_INT >= 16) {
        // Inflate and set the layout for the expanded notification view
        expandedView.setImageViewResource(R.id.notification_image, R.drawable.music_icon);
        expandedView.setTextViewText(R.id.title,
                mQueue.get(playingSongPosition).getSong().getAlbum());
        expandedView.setTextViewText(R.id.sub_title,
                mQueue.get(playingSongPosition).getSong().getDisplayName());
        if (isPlayerPlaying()) {
          expandedView.setImageViewResource(R.id.status_song, R.drawable.notification_pause);
          expandedView.setViewVisibility(R.id.close_notification, View.INVISIBLE);
        } else {
          expandedView.setImageViewResource(R.id.status_song, R.drawable.notification_play);
          expandedView.setViewVisibility(R.id.close_notification, View.VISIBLE);
        }

        if(playingSongPosition == 0) {
          expandedView.setViewVisibility(R.id.skip_previous_song, View.INVISIBLE);
        } else {
          expandedView.setViewVisibility(R.id.skip_previous_song, View.VISIBLE);
        }
        if(playingSongPosition == mQueue.size() - 1) {
          expandedView.setViewVisibility(R.id.skip_next_song, View.INVISIBLE);
        } else {
          expandedView.setViewVisibility(R.id.skip_next_song, View.VISIBLE);
        }
        expandedView.setOnClickPendingIntent(R.id.skip_previous_song, previousPendingIntent);
        expandedView.setOnClickPendingIntent(R.id.skip_next_song, nextPendingIntent);
        expandedView.setOnClickPendingIntent(R.id.status_song, playPendingIntent);
        expandedView.setOnClickPendingIntent(R.id.close_notification, closePendingIntent);
        notification.bigContentView = expandedView;
        notificationTarget = new NotificationTarget(context, expandedView, R.id.notification_image,
                notification, NOTIFICATION_ID);
        Glide.with(context.getApplicationContext()).load(uriPath).asBitmap().into(notificationTarget);
      }
      notificationTarget = new NotificationTarget(context, remoteViews, R.id.notification_image,
              notification, NOTIFICATION_ID);
      mNotificationManager.notify(NOTIFICATION_ID, notification);
      Glide.with(context.getApplicationContext()).load(uriPath).asBitmap().into(notificationTarget);
    } else {
      if (null != mNotificationManager) {
        mNotificationManager.cancel(NOTIFICATION_ID);
      }
    }
  }

  public void shuffle() {
    long seed = System.nanoTime();
    Collections.shuffle(mQueue, new Random(seed));
    for (int i = 0; i < mQueue.size(); i++) {
      mQueue.get(i).setPlayed(false);
    }
    pause();
    playAtPositionFromQueue(0);
  }

  public void repeat() {
    isRepeatOn = !isRepeatOn;
  }

  public void cancelNotification() {
    if (!isPlayerPlaying()) {
      if (mNotificationManager != null) {
        mNotificationManager.cancel(NOTIFICATION_ID);
      }
    }
  }

  public void registerShakeLisitner() {
    if(null != context) {
      // ShakeDetector initialization
      mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
      mAccelerometer = mSensorManager
              .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      mShakeDetector = new ShakeDetector();
      mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

        @Override
        public void onShake(int count) {
          if(isPlayerPlaying()) {
            skipSong(true);
          }
        }
      });
      mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }
  }

  public void unRegisterShakeLisitner() {
    if (null != mSensorManager && null != mShakeDetector) {
      mSensorManager.unregisterListener(mShakeDetector);
    }
    mSensorManager = null;
    mAccelerometer = null;
    mShakeDetector = null;
  }

  public void changeVolume(boolean increase) {
    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    if (increase) {
      audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
              AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    } else {
      audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
              AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }
  }
}
