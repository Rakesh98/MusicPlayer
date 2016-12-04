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
import android.widget.TextView;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Album;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.comparators.AlbumComparator;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.HidingScrollListener;
import com.rakesh.mobile.musicmasti.view.MusicContainer;
import com.rakesh.mobile.musicmasti.view.adapters.AlbumAdapter;
import com.rakesh.mobile.musicmasti.view.adapters.SpacesItemDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rakesh.jnanagari on 03/07/16.
 */
public class Playlists extends Fragment {
    private RecyclerView mRecyclerView;
    private AlbumAdapter adapter;
    private MusicContainer mMusicContainer;
    private List<Album> albumList;
    private FloationButtonStatus mFloationButtonStatus;
    private TextView noDataText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_container, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.container);
        mMusicContainer = (MusicContainer) getActivity();
        albumList = new ArrayList<>();
        noDataText = (TextView) (view.findViewById(R.id.no_data));
        setAlbumList();
        if(albumList.isEmpty()) {
            noDataText.setVisibility(View.VISIBLE);
        }
        adapter = new AlbumAdapter(albumList, getActivity(), true, Constants.PLAY_LIST_INTENT_VALUE);

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

    private void setAlbumList() {
        Album album;
        HashMap<Integer, Album> albumMap;
        albumMap = new HashMap<>();
        Song song;
        List<Song> songList = setSongList();
        int length = songList.size();
        for(int i = 0; i < length; i++) {
            song = songList.get(i);
            album = albumMap.get(song.getAlbumId());
            if(null == album) {
                album = new Album();
                album.setAlbumId(song.getAlbumId());
                album.setTitle(song.getAlbum());
                album.setSubTitle(song.getComposer());
                album.setCount(1);
                album.setDateAdded(song.getDateAdded());
                albumMap.put(song.getAlbumId(), album);
            } else {
                album.setCount(album.getCount() + 1);
            }
        }
        albumList = new ArrayList<>(albumMap.values());
        Collections.sort(albumList, new AlbumComparator());
    }

    private List<Song> setSongList() {
        ArrayList<Song> returnList = new ArrayList<>();
        for(int i = 0; i < StaticData.getPlayListSelected().size(); i++) {
            for (int j = 0; j < StaticData.songList.size(); j++) {
                if (StaticData.getPlayListSelected().get(i) == StaticData.songList.get(j).getId()) {
                    returnList.add(StaticData.songList.get(j));
                }
            }
        }
        return returnList;
    }

    public void updateList () {
        if (null != albumList && null != adapter) {
            Collections.sort(albumList, new AlbumComparator());
            adapter.notifyDataSetChanged();
        }

    }
}
