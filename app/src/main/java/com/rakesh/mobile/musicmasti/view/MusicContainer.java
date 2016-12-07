package com.rakesh.mobile.musicmasti.view;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rakesh.mobile.musicmasti.R;

import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.SharedPreference;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.adapters.PlayQueueAdapter;
import com.rakesh.mobile.musicmasti.view.adapters.ViewPagerAdapter;
import com.rakesh.mobile.musicmasti.view.adapters.ViewPagerTransformer;
import com.rakesh.mobile.musicmasti.view.container_fragments.Albums;
import com.rakesh.mobile.musicmasti.view.container_fragments.AllSongs;
import com.rakesh.mobile.musicmasti.view.container_fragments.FloationButtonStatus;
import com.rakesh.mobile.musicmasti.view.container_fragments.Composer;
import com.rakesh.mobile.musicmasti.view.container_fragments.Playlists;

import java.util.List;

/**
 * Created by rakesh.jnanagari on 24/06/16.
 */
public class MusicContainer extends AppCompatActivity
    implements PlayerService.PlayerStatusUpdate, FloationButtonStatus {
  private Toolbar toolbar;
  private TabLayout tabLayout;
  private ViewPager viewPager;
  public AppBarLayout mAppBarLayout;
  private ViewPagerAdapter adapter;
  private SharedPreference mSharedPreference;
  private RelativeLayout musicRunningBar;
  private TextView bottomBarTitle;
  private TextView bottomBarSubTitle;
  private ImageView bottomBarImage;
  private ImageView bottomBarStatus;
  private ImageView bottomBarSkipNext;
  private ImageView bottomBarSkipPrevious;
  private FloatingActionButton mFloatingActionButton;
  private PlayQueueAdapter mPlayQueueAdapter;
  public static boolean isActivityDestroyed;
  private DrawerLayout mDrawerLayout;
  private NavigationView mNavigationView;
  private Albums mAlbums;
  private AllSongs mAllSongs;
  private Composer mComposer;
  private Playlists mPlayLists;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.music_container);
    isActivityDestroyed = false;
    if (PlayerService.getInstance() != null) {
      toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      toolbar.setNavigationIcon(R.drawable.icon_menu);
      mSharedPreference = SharedPreference.getInstance(this);
      mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
      setToolbarHeight();
      bottomBarTitle = (TextView) findViewById(R.id.playing_song_title);
      bottomBarSubTitle = (TextView) findViewById(R.id.playing_song_sub_title);
      bottomBarSubTitle.setSelected(true);
      bottomBarImage = (ImageView) findViewById(R.id.bottom_bar_image);
      bottomBarStatus = (ImageView) findViewById(R.id.bottom_bar_status_icon);
      bottomBarSkipNext = (ImageView) findViewById(R.id.bottom_bar_next);
      bottomBarSkipPrevious = (ImageView) findViewById(R.id.bottom_bar_before);
      mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_button);
      mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
      mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
      Utils.setStatusBarTranslucent(this, true);
//    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mDrawerLayout.openDrawer(Gravity.LEFT);
        }
      });

      viewPager = (ViewPager) findViewById(R.id.viewpager);
      tabLayout = (TabLayout) findViewById(R.id.tabs);
      musicRunningBar = (RelativeLayout) findViewById(R.id.music_running_bar);
      mFloatingActionButton
              .setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E64A19")));
      bottomBarStatus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (null != PlayerService.getInstance()) {
            if (PlayerService.getInstance().isPlayerPlaying()) {
              PlayerService.getInstance().pause();
              bottomBarStatus.setImageResource(R.drawable.icon_play);
            } else {
              PlayerService.getInstance().resume();
              bottomBarStatus.setImageResource(
                      R.drawable.icon_pause_circle_outline_white);
            }
          } else {
            finish();
          }
        }
      });

      bottomBarSkipNext.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PlayerService.getInstance().skipSong(true);
        }
      });
      bottomBarSkipPrevious.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PlayerService.getInstance().skipSong(false);
        }
      });

      mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          showDialogQueue();
        }
      });
      musicRunningBar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(MusicContainer.this, NowPlaying.class);
          bottomBarImage.buildDrawingCache();
          Bitmap bitmap = bottomBarImage.getDrawingCache();
          Palette palette = Palette.from(bitmap).generate();
          intent.putExtra(Constants.STATUSBAR_COLOR, palette.getDarkMutedColor(Color.parseColor("#424242")));
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(MusicContainer.this, bottomBarImage,
                            getResources().getString(R.string.transition_thumbnai_two));
            startActivity(intent, options.toBundle());
          } else {
            startActivity(intent);
          }
        }
      });

      setupViewPager(viewPager);
      tabLayout.setupWithViewPager(viewPager);
        PlayerService.getInstance().setPlayerStatusUpdateLisitner(this);
      mNavigationView.setItemIconTintList(null);
      mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
          Intent intent;
          switch (item.getItemId()) {
            case R.id.equalizer:
              intent = new Intent();
              intent.setAction("android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL");
              if ((intent.resolveActivity(getPackageManager()) != null)) {
                startActivity(intent);
              } else {
                Toast.makeText(getApplicationContext(), getString(R.string.equalizer_not_supported), Toast.LENGTH_LONG).show();
              }
              return true;
            case R.id.settings:
              intent = new Intent(MusicContainer.this, Settings.class);
              startActivity(intent);
              return true;
            case R.id.share_app:
              intent = new Intent(android.content.Intent.ACTION_SEND);
              intent.setType("text/plain");
              intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_app_message));
              startActivity(intent);
              return true;
            case R.id.feedback:
              intent=new Intent(Intent.ACTION_SEND);
              String[] recipients={"rakeshjnanagari@gmail.com"};
              intent.putExtra(Intent.EXTRA_EMAIL, recipients);
              intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback and Suggestions");
              intent.setType("text/html");
              final PackageManager pm = getPackageManager();
              final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
              ResolveInfo best = null;
              for(final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                  best = info;
              if (best != null) {
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
              }
              startActivity(intent);
              return true;
          }
          return false;
        }
      });
    } else {
      finish();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    MenuItem viewType = menu.findItem(R.id.view_type);
    if (Configuration.gridView) {
      viewType.setIcon(R.drawable.icon_head_line_view);
      viewType.setTitle(R.string.list_view);
    } else {
      viewType.setIcon(R.drawable.icon_module_view);
      viewType.setTitle(R.string.grid_view);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.view_type:
        Configuration.gridView = !Configuration.gridView;
        mSharedPreference.putSharedPrefBoolean(Constants.GRID_VIEW_KEY, Configuration.gridView);
        if (Configuration.gridView) {
          item.setIcon(R.drawable.icon_head_line_view);
          item.setTitle(R.string.list_view);
        } else {
          item.setIcon(R.drawable.icon_module_view);
          item.setTitle(R.string.grid_view);
        }
        playListItemViewChange();
        return true;
      case R.id.settings:
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        return true;
      case R.id.search:
        startActivity(new Intent(this, Search.class));
        return true;
      case R.id.sort:
        showSortDialog();
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

  @Override
  public void onConfigurationChanged(android.content.res.Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    playListItemViewChange();
  }

  private void setupViewPager(ViewPager viewPager) {
    adapter = new ViewPagerAdapter(getSupportFragmentManager());
    if (Configuration.albums) {
      if (mAlbums == null) {
        mAlbums = new Albums();
      }
      adapter.addFragment(mAlbums, getResources().getString(R.string.albums));
    }
    if (Configuration.allSongs) {
      if (mAllSongs == null) {
        mAllSongs = new AllSongs();
      }
      adapter.addFragment(mAllSongs, getResources().getString(R.string.all_songs));
    }
    if (Configuration.composers) {
      if (mComposer == null) {
        mComposer = new Composer();
      }
      adapter.addFragment(mComposer, getResources().getString(R.string.artists));
    }
    if (Configuration.playlists) {
      if (mPlayLists == null) {
        mPlayLists = new Playlists();
      }
      adapter.addFragment(mPlayLists, getResources().getString(R.string.playlists));
    }
    viewPager.setPageTransformer(true, new ViewPagerTransformer());
    viewPager.setAdapter(adapter);
  }

  private void setToolbarHeight() {
    TypedValue tv = new TypedValue();
    if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      Utils.mToolBarHeight =
          TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
    }
  }

  private void playListItemViewChange() {
    Utils.initGridItemSize(this);
    int currentPage = viewPager.getCurrentItem();
    viewPager.setAdapter(adapter);
    viewPager.setCurrentItem(currentPage);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    isActivityDestroyed = true;
    if(PlayerService.getInstance() != null) {
      PlayerService.getInstance().setPlayerStatusUpdateUnregister();
    }
    Log.d("debug", "destroy");
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
  public void playingSongStatus(com.rakesh.mobile.musicmasti.model.Queue song, boolean enableNext,
      boolean enablePrevious) {
    if(null == PlayerService.getInstance()) {
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
    mFloatingActionButton.invalidate();
    RelativeLayout.LayoutParams params =
        (RelativeLayout.LayoutParams) mFloatingActionButton.getLayoutParams();
    if (View.VISIBLE == musicRunningBar.getVisibility()) {
      params.setMargins(0, 0, 25, 190);
    } else {
      params.setMargins(0, 0, 25, 25);
    }
    mFloatingActionButton.setLayoutParams(params);
  }

  @Override
  public void setFolatingButtonVisibility(boolean visibility) {
    if (visibility) {
      mFloatingActionButton.show();
    } else {
      mFloatingActionButton.hide();
    }
  }

  private void showSortDialog() {
    final Dialog dialog = new Dialog(this);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setContentView(R.layout.popup_sort);

    dialog.setCanceledOnTouchOutside(true);
    dialog.setCancelable(true);
    dialog.show();
    final RadioButton title = (RadioButton) dialog.findViewById(R.id.title);
    final RadioButton artists = (RadioButton) dialog.findViewById(R.id.artists);
    final RadioButton recents = (RadioButton) dialog.findViewById(R.id.recently_played);
    final RadioButton date = (RadioButton) dialog.findViewById(R.id.date);
    RadioButton none = (RadioButton) dialog.findViewById(R.id.none);
    switch (Configuration.sortType) {
      case Constants.TITLE_SORT:
        title.setChecked(true);
        break;
      case Constants.ARTISTS_SORT:
        artists.setChecked(true);
        break;
      case Constants.RECENT_SORT:
        recents.setChecked(true);
        break;
      case Constants.DATE_SORT:
        date.setChecked(true);
        break;
      case Constants.NONE_SORT:
        none.setChecked(true);
        break;
    }

    dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (title.isChecked()) {
          Configuration.sortType = Constants.TITLE_SORT;
        } else if (artists.isChecked()) {
          Configuration.sortType = Constants.ARTISTS_SORT;
        } else if (recents.isChecked()) {
          Configuration.sortType = Constants.RECENT_SORT;
        } else if (date.isChecked()) {
          Configuration.sortType = Constants.DATE_SORT;
        } else {
          Configuration.sortType = Constants.NONE_SORT;
        }

        mSharedPreference.putSharedPrefInt(Constants.SORT_KEY, Configuration.sortType);
        if (null != mAlbums) {
          mAlbums.updateList();
        }
        if (null != mAllSongs) {
          mAllSongs.updateList();
        }
        if (null != mComposer) {
          mComposer.updateList();
        }
        if (null != mPlayLists) {
          mPlayLists.updateList();
        }
        dialog.dismiss();
      }
    });

  }
}
