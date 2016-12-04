package com.rakesh.mobile.musicmasti.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Queue;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.adapters.PlayQueueAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh.jnanagari on 16/07/16.
 */
public class SongList extends AppCompatActivity implements PlayerService.PlayerStatusUpdate {
  private List<Song> songList;
  private long albumId;
  private RelativeLayout musicRunningBar;
  private TextView bottomBarTitle;
  private TextView bottomBarSubTitle;
  private ImageView bottomBarImage;
  private ImageView bottomBarStatus;
  private ImageView bottomBarSkipNext;
  private ImageView bottomBarSkipPrevious;

  private PlayQueueAdapter mPlayQueueAdapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_music_play_container);
    if(null != PlayerService.getInstance()) {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      musicRunningBar = (RelativeLayout) findViewById(R.id.music_running_bar);
      bottomBarTitle = (TextView) findViewById(R.id.playing_song_title);
      bottomBarSubTitle = (TextView) findViewById(R.id.playing_song_sub_title);
      bottomBarImage = (ImageView) findViewById(R.id.bottom_bar_image);
      bottomBarStatus = (ImageView) findViewById(R.id.bottom_bar_status_icon);
      bottomBarSkipNext = (ImageView) findViewById(R.id.bottom_bar_next);
      bottomBarSkipPrevious = (ImageView) findViewById(R.id.bottom_bar_before);
      bottomBarSubTitle.setSelected(true);
      bottomBarStatus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(null != PlayerService.getInstance())
          if (PlayerService.getInstance().isPlayerPlaying()) {
            PlayerService.getInstance().pause();
            bottomBarStatus.setImageResource(R.drawable.icon_play);
          } else {
            PlayerService.getInstance().resume();
            bottomBarStatus.setImageResource(
                    R.drawable.icon_pause_circle_outline_white);
          }
        }
      });
      bottomBarSkipNext.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (null != PlayerService.getInstance()) {
            PlayerService.getInstance().skipSong(true);
          }
        }
      });
      bottomBarSkipPrevious.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (null != PlayerService.getInstance()) {
            PlayerService.getInstance().skipSong(false);
          }

        }
      });
      musicRunningBar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(SongList.this, NowPlaying.class);
          bottomBarImage.buildDrawingCache();
          Bitmap bitmap = bottomBarImage.getDrawingCache();
          Palette palette = Palette.from(bitmap).generate();
          intent.putExtra(Constants.STATUSBAR_COLOR, palette.getDarkMutedColor(Color.parseColor("#424242")));
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(SongList.this, bottomBarImage,
                            getResources().getString(R.string.transition_thumbnai_two));
            startActivity(intent, options.toBundle());
          } else {
            startActivity(intent);
          }
        }
      });

      songList = new ArrayList<>();
      albumId = getIntent().getLongExtra(Constants.ALBUM_ID_KEY, 0);
      AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
      Display display = getWindowManager().getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      CollapsingToolbarLayout mCollapsingToolBar =
              (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
      mCollapsingToolBar.setExpandedTitleColor(Color.TRANSPARENT);
      mCollapsingToolBar
              .setContentScrimColor(getIntent().getIntExtra(Constants.TOOLBAR_COLOR_KEY, 0));
      mCollapsingToolBar
              .setStatusBarScrimColor(getIntent().getIntExtra(Constants.STATUSBAR_COLOR, 0));
//      FloatingActionButton mFloatingActionButton =
//              (FloatingActionButton) findViewById(R.id.floating_button);
//      mFloatingActionButton.setBackgroundTintList(
//              ColorStateList.valueOf(getIntent().getIntExtra(Constants.FLOATING_BUTTON_COLOR, 0)));

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getIntent().getIntExtra(Constants.STATUSBAR_COLOR, 0));
      }
      appBarLayout.getLayoutParams().height = size.x;
      ImageView toolBarImage = (ImageView) findViewById(R.id.thumbnail);
      toolBarImage.getLayoutParams().height = size.x;
      toolBarImage.getLayoutParams().width = size.x;
      Utils.setBitMap(this, toolBarImage, albumId);

      setSongList();
//      findViewById(R.id.floating_button).setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//          showOptionDialog(-1, false);
//        }
//      });

      LinearLayout songListLayout = (LinearLayout) findViewById(R.id.song_list_layout);
      LayoutInflater mLayoutInflator =
              (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      for (int i = 0; i < songList.size(); i++) {
        View view =
                mLayoutInflator.inflate(R.layout.item_music_play_container, songListLayout, false);
        ((TextView) view.findViewById(R.id.title)).setText(songList.get(i).getDisplayName());

        ((TextView) view.findViewById(R.id.index)).setText((i + 1) + ".");
        if (null != songList.get(i).getComposer() && !songList.get(i).getComposer().isEmpty()) {
          ((TextView) view.findViewById(R.id.sub_title)).setText(songList.get(i).getComposer());
        } else {
          view.findViewById(R.id.sub_title).setVisibility(View.GONE);
        }
        ((TextView) view.findViewById(R.id.time))
                .setText(getString(R.string.duration) + " " + Utils.milliSecondsToTimer(Long.parseLong(songList.get(i).getDuration())));
        view.findViewById(R.id.item_play_list_container)
                .setBackgroundColor(getIntent().getIntExtra(Constants.ITEM_COLOR_KEY, 0));
        final int selectedSong = i;
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            List<Queue> songQueue = new ArrayList<>();
            for (int k = 0; k < songList.size(); k++) {
              Queue queue = new Queue();
              queue.setSong(songList.get(k));
              songQueue.add(queue);

            }
            PlayerService.getInstance().addToQueue(songQueue, true, selectedSong);
          }
        });
        view.findViewById(R.id.drop_down).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showOptionDialog(selectedSong, true);
          }
        });
        songListLayout.addView(view);
      }
      PlayerService.getInstance().setSongListUpdateLisitner(this);
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
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        supportFinishAfterTransition();
        return true;
      case R.id.enque:
        showDialogQueue();
        return true;
      case R.id.search:
        Intent intent = new Intent(SongList.this, Search.class);
        startActivity(intent);
        return true;
      case R.id.share_app:
        intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_app_message));
        startActivity(intent);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }


  private void setSongList() {
    switch (getIntent().getIntExtra(Constants.SONG_LIST_INTETN_KEY, Constants.ALBUM_INTENT_VALUE)) {
      case Constants.ALBUM_INTENT_VALUE:
        for (int i = 0; i < StaticData.songList.size(); i++) {
          if (StaticData.songList.get(i).getAlbumId() == albumId) {
            songList.add(StaticData.songList.get(i));
          }
        }
        break;
      case Constants.COMPOSER_INTENT_VALUE:
        String composerName = null;
        for (int i = 0; i < StaticData.songList.size(); i++) {
          Song song = StaticData.songList.get(i);
          if (song.getAlbumId() == albumId) {
            if(song.getComposer() != null && song.getComposer().contains("Lyrics")) {
              String[] split = song.getComposer().split("Lyrics");
              composerName = split[0].replace("|", "");
            } else {
              composerName = song.getComposer();
            }
            break;
          }
        }
        if (null != composerName) {
          for (int i = 0; i < StaticData.songList.size(); i++) {
            String tempComposer;
            if (StaticData.songList.get(i).getComposer() != null && StaticData.songList.get(i).getComposer().contains("Lyrics")) {
              String[] split = StaticData.songList.get(i).getComposer().split("Lyrics");
              tempComposer = split[0].replace("|", "");
            } else {
              tempComposer = StaticData.songList.get(i).getComposer();
            }

            if (null != tempComposer && tempComposer.trim().equalsIgnoreCase(composerName.trim())) {
              songList.add(StaticData.songList.get(i));
            }
          }
        } else {
          for (int i = 0; i < StaticData.songList.size(); i++) {
            if (StaticData.songList.get(i).getAlbumId() == albumId) {
              songList.add(StaticData.songList.get(i));
            }
          }
        }

        break;
      case Constants.PLAY_LIST_INTENT_VALUE:
        for (int i = 0; i < StaticData.songList.size(); i++) {
          if (StaticData.songList.get(i).getAlbumId() == albumId && StaticData.getPlayListSelected().contains(StaticData.songList.get(i).getId())) {
            songList.add(StaticData.songList.get(i));
          }
        }
    }

  }

  private void showOptionDialog(final int position, final boolean isParticularSong) {
    final Dialog dialog = new Dialog(new ContextThemeWrapper(this, R.style.DialogSlideAnim));
    dialog.getWindow().setGravity(Gravity.BOTTOM);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setContentView(R.layout.dialog_options_song);

    dialog.setCanceledOnTouchOutside(true);
    dialog.setCancelable(true);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    dialog.getWindow().setAttributes(lp);
    dialog.show();
    dialog.findViewById(R.id.enque_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        List<Queue> queueList = new ArrayList<>();
        if (isParticularSong) {
          Queue queue = new Queue();
          queue.setSong(songList.get(position));
          queueList.add(queue);
        } else {
          for (int k = 0; k < songList.size(); k++) {
            Queue queue = new Queue();
            queue.setSong(songList.get(k));
            queueList.add(queue);
          }
        }
        if(null != PlayerService.getInstance()) {
          PlayerService.getInstance().addToQueue(queueList, false, -1);
        }
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.set_as_ringtone_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
    dialog.findViewById(R.id.share_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
    dialog.findViewById(R.id.info_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(isParticularSong) {
          showInfoDialog(songList.get(position));
        }
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.delete_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });
    dialog.findViewById(R.id.add_to_playlist).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isParticularSong) {
          Utils.addToPlayList(songList.get(position), SongList.this);
        } else {
          Utils.addToPlayList(songList, SongList.this);
        }
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });


  }

  @Override
  public void playingSongStatus(Queue song, boolean enableNext, boolean enablePrevious) {
    if (null == PlayerService.getInstance()) {
      finish();
      return;
    }
    if (null != song) {
      musicRunningBar.setVisibility(View.VISIBLE);
      bottomBarSubTitle.setText(song.getSong().getTitle());
      bottomBarTitle.setText(song.getSong().getAlbum());
      Utils.setBitMap(this, bottomBarImage, song.getSong().getAlbumId());
      if (!PlayerService.getInstance().isPlayerPlaying()) {
        bottomBarStatus.setImageResource(R.drawable.icon_play);
      } else {
        bottomBarStatus.setImageResource(
            R.drawable.icon_pause_circle_outline_white);
      }
      if (enableNext) {
        bottomBarSkipNext.setVisibility(View.VISIBLE);
      } else {
        bottomBarSkipNext.setVisibility(View.GONE);
      }
      if (enablePrevious) {
        bottomBarSkipPrevious.setVisibility(View.VISIBLE);
      } else {
        bottomBarSkipPrevious.setVisibility(View.GONE);
      }
    } else {
      musicRunningBar.setVisibility(View.GONE);
    }
    if (null != mPlayQueueAdapter) {
      mPlayQueueAdapter.notifyDataSetChanged();
    }
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
            if(null != PlayerService.getInstance()) {
              PlayerService.getInstance().deleteSongFromQueue(swipedPosition);
            } else {
              finish();
            }
          }

        };
    ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
    mItemTouchHelper.attachToRecyclerView(mRecyclerView);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(null != PlayerService.getInstance()) {
      PlayerService.getInstance().setSongListUnregister();
    }
  }

  private void showInfoDialog(Song song) {
    final Dialog dialog = new Dialog(this);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setContentView(R.layout.popup_info);

    dialog.setCanceledOnTouchOutside(true);
    dialog.setCancelable(true);
    dialog.show();
    ((TextView)dialog.findViewById(R.id.title)).setText(song.getTitle());
    ((TextView)dialog.findViewById(R.id.artist)).setText(song.getArtist());
    ((TextView)dialog.findViewById(R.id.composer)).setText(song.getAlbum());
    ((TextView)dialog.findViewById(R.id.path)).setText(song.getContentUri().toString());
    ((TextView)dialog.findViewById(R.id.album)).setText(song.getAlbum());
    ((TextView)dialog.findViewById(R.id.duration)).setText(Utils.milliSecondsToTimer(Long.parseLong(song.getDuration())));
    ((TextView)dialog.findViewById(R.id.size)).setText(Utils.convertBytesToMb(Long.parseLong(song.getSize())));
    dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

  }


}
