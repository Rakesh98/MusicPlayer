package com.rakesh.mobile.musicmasti.view.container_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.RecentlyPlayedSongListComparator;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.HidingScrollListener;
import com.rakesh.mobile.musicmasti.view.MusicContainer;
import com.rakesh.mobile.musicmasti.view.Splash;
import com.rakesh.mobile.musicmasti.view.adapters.AllSongsAdapter;
import com.rakesh.mobile.musicmasti.view.adapters.SpacesItemDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by rakesh.jnanagari on 03/07/16.
 */
public class AllSongs extends Fragment {
  private RecyclerView mRecyclerView;
  private MusicContainer mMusicContainer;
  private List<Song> songList;
  private AllSongsAdapter adapter;
  private FloationButtonStatus mFloationButtonStatus;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_music_container, container, false);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.container);
    mMusicContainer = (MusicContainer) getActivity();
    songList = new ArrayList<>();
    songList.addAll(StaticData.songList);
    adapter = new AllSongsAdapter(songList, getActivity(), true);

    GridLayoutManager mGridLayoutManager =
        new GridLayoutManager(mMusicContainer, Configuration.gridCount);
    mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        if (0 != position) {
          return 1;
        } else {
          return Configuration.gridCount;
        }

      }
    });
    mRecyclerView.setLayoutManager(mGridLayoutManager);
    mRecyclerView.addItemDecoration(new SpacesItemDecorator(Configuration.CONTAINER_LIST_SPACE));
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.setOnScrollListener(new HidingScrollListener(getActivity()) {

      @Override
      public void onMoved(int distance) {
        mMusicContainer.mAppBarLayout.setTranslationY(-distance);
      }

      @Override
      public void onShow() {
        mMusicContainer.mAppBarLayout.animate().translationY(0)
            .setInterpolator(new DecelerateInterpolator(2)).start();
      }

      @Override
      public void onHide() {
        mMusicContainer.mAppBarLayout.animate().translationY(-Utils.mToolBarHeight)
            .setInterpolator(new AccelerateInterpolator(2)).start();
      }

    });

    mFloationButtonStatus = mMusicContainer;
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
          mFloationButtonStatus.setFolatingButtonVisibility(true);
        }
      }

      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0 ||dy<0) {
          mFloationButtonStatus.setFolatingButtonVisibility(false);
        }
      }
    });

    return view;
  }
  public void updateList() {
    if (songList != null && adapter != null) {
      Collections.sort(songList, new RecentlyPlayedSongListComparator());
      adapter.notifyDataSetChanged();
    }
  }

}
