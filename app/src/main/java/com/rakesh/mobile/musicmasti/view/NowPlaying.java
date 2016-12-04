package com.rakesh.mobile.musicmasti.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.adapters.PlayQueueAdapter;

import java.util.concurrent.ExecutionException;

/**
 * Created by rakesh.jnanagari on 05/09/16.
 */
public class NowPlaying extends AppCompatActivity implements PlayerService.NowPlayingInterface{

  private TextView titleTextView;
  private TextView songTextView;
  private TextView artistTextView;
  private TextView startTime;
  private TextView endTime;
  private SeekBar mSeekBar;
  private ImageView songImage;
  private ImageView songImageMain;
  private ImageView playStatus;
  private ImageView skipNext;
  private ImageView skipPrevious;
  private ImageView repeat;
  private ImageView shuffle;
  private Song songPlaying;
  private PlayQueueAdapter mPlayQueueAdapter;
  public static boolean isActivityDestroyed;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_now_playing);
    isActivityDestroyed = false;
    if(null != PlayerService.getInstance()) {
      titleTextView = (TextView) findViewById(R.id.title);
      songTextView = (TextView) findViewById(R.id.song);
      artistTextView = (TextView) findViewById(R.id.artist);
      startTime = (TextView) findViewById(R.id.start_time);
      endTime = (TextView) findViewById(R.id.end_time);
      mSeekBar = (SeekBar) findViewById(R.id.song_progress);
      songImage = (ImageView) findViewById(R.id.song_image);
      songImageMain = (ImageView) findViewById(R.id.song_image_main);
      playStatus = (ImageView) findViewById(R.id.play_status_icon);
      skipNext = (ImageView) findViewById(R.id.skip_next);
      skipPrevious = (ImageView) findViewById(R.id.skip_previous);
      repeat = (ImageView) findViewById(R.id.repeat);
      shuffle = (ImageView) findViewById(R.id.shuffle);
      Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(mToolBar);
//    setStatusBarColor(getIntent().getIntExtra(Constants.STATUSBAR_COLOR, 0));
      Utils.setStatusBarTranslucent(this, true);
      // noinspection ConstantConditions,ConstantConditions
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      songTextView.setSelected(true);
      DisplayMetrics metrics = new DisplayMetrics();
      WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
      wm.getDefaultDisplay().getMetrics(metrics);
      int width = metrics.widthPixels * 3 / 4;
      FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(width, width);
      songImageMain.setLayoutParams(parms);

      if (PlayerService.getInstance().isRepeatOn) {
        repeat.setImageResource(R.drawable.icon_repeat_gray);
      } else {
        repeat.setImageResource(R.drawable.icon_repeat);
      }

      repeat.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PlayerService.getInstance().repeat();
          if (PlayerService.getInstance().isRepeatOn) {
            repeat.setImageResource(R.drawable.icon_repeat_gray);
          } else {
            repeat.setImageResource(R.drawable.icon_repeat);
          }
        }
      });

      shuffle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PlayerService.getInstance().shuffle();
        }
      });
      playStatus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(null != PlayerService.getInstance()) {
            if (PlayerService.getInstance().isPlayerPlaying()) {
              PlayerService.getInstance().pause();
              playStatus.setImageResource(R.drawable.icon_play);
            } else {
              PlayerService.getInstance().resume();
              playStatus.setImageResource(
                      R.drawable.icon_pause_circle_outline_white);
            }
          } else {
            finish();
          }
        }
      });

      skipPrevious.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PlayerService.getInstance().skipSong(false);
          updateActivity();
        }
      });

      skipNext.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PlayerService.getInstance().skipSong(true);
          updateActivity();
        }
      });

      PlayerService.getInstance().setmNowPlayingInterface(this);
      mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
          int totalDuration = PlayerService.getInstance().getSeekPosition();
          int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

          // forward or backward to certain seconds
          PlayerService.getInstance().setSeekPosition(currentPosition);
          startTime.setText(Utils.milliSecondsToTimer(currentPosition));
        }
      });
    } else {
      finish();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.song_list_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      case R.id.enque:
        showDialogQueue();
        return true;
      case R.id.search:
        startActivity(new Intent(this, Search.class));
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void updateActivity() {
    if (null != PlayerService.getInstance()) {
      if (PlayerService.getInstance().isPlayerPlaying()) {
        playStatus.setImageResource(
                R.drawable.icon_pause_circle_outline_white);

      } else {
        playStatus.setImageResource(R.drawable.icon_play);

      }
      if (-1 != PlayerService.getInstance().getPlayingSongPosition()) {
        songPlaying = PlayerService.getInstance().mQueue.get(PlayerService.getInstance().getPlayingSongPosition()).getSong();
      }
      if (null != songPlaying) {
        try {
          Utils.setBlurImage(this, songImage, songImageMain, findViewById(R.id.image_layout), songPlaying.getAlbumId());
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }
        Utils.setCircularImage(this, songImageMain, songPlaying.getAlbumId());
        songTextView.setText(songPlaying.getDisplayName());
        artistTextView.setText(songPlaying.getArtist());
        titleTextView.setText(songPlaying.getTitle());
      }
      startTime.setText(Utils.milliSecondsToTimer(0));
      endTime.setText(Utils.milliSecondsToTimer(Long.parseLong(songPlaying.getDuration())));
    } else {
      finish();
    }
  }

  @Override
  public void onBackPressed() {
    if(MusicContainer.isActivityDestroyed) {
      startActivity(new Intent(this, MusicContainer.class));
      finish();
    } else {
      supportFinishAfterTransition();
    }
  }

  @Override
  public void updateSong(Song song, boolean enableNext, boolean enablePrevious) {
    songPlaying = song;
    if(enableNext) {
      skipNext.setVisibility(View.VISIBLE);
    } else {
      skipNext.setVisibility(View.GONE);
    }
    if(enablePrevious) {
      skipPrevious.setVisibility(View.VISIBLE);
    } else {
      skipPrevious.setVisibility(View.GONE);
    }
    updateActivity();
    if (null != mPlayQueueAdapter) {
      mPlayQueueAdapter.notifyDataSetChanged();
    }
    startTime.setText(Utils.milliSecondsToTimer(0));
    endTime.setText(Utils.milliSecondsToTimer(Long.parseLong(songPlaying.getDuration())));
  }

  @Override
  public void updateSongProgress(long currentTime, long endTime) {
    startTime.setText(Utils.milliSecondsToTimer(currentTime));
    // Updating progress bar
    int progress = (int)(Utils.getProgressPercentage(currentTime, endTime));
    //Log.d("Progress", ""+progress);
    mSeekBar.setProgress(progress);

  }

  private void showDialogQueue() {
    Dialog dialog = new Dialog(new ContextThemeWrapper(this, R.style.DialogSlideAnim));
    dialog.getWindow().setGravity(Gravity.BOTTOM);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setContentView(R.layout.dialog_queue);

    dialog.setCanceledOnTouchOutside(true);
    dialog.setCancelable(true);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    dialog.getWindow().setAttributes(lp);
    // dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
    // ViewGroup.LayoutParams.MATCH_PARENT);
    dialog.show();
    RecyclerView mRecyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mPlayQueueAdapter = new PlayQueueAdapter(this);
    mRecyclerView.setAdapter(mPlayQueueAdapter);
    setUpItemTouchHelper(mRecyclerView);
  }

  private void setUpItemTouchHelper(RecyclerView mRecyclerView) {
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
              @Override
              public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    RecyclerView.ViewHolder target) {
                return false;
              }

              @Override
              public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
              }

              @Override
              public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                if (null != PlayerService.getInstance()) {
                  PlayerService.getInstance().deleteSongFromQueue(swipedPosition);
                } else {
                  finish();
                }
              }

            };
    ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
    mItemTouchHelper.attachToRecyclerView(mRecyclerView);
  }

  private void setStatusBarColor(int colorCode) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(colorCode);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    isActivityDestroyed = false;
    if(null != PlayerService.getInstance()) {
      PlayerService.getInstance().unSetNowPlayingInterface();
    }
  }
}
