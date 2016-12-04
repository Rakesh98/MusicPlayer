package com.rakesh.mobile.musicmasti.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Album;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.comparators.AlbumComparator;
import com.rakesh.mobile.musicmasti.utils.comparators.SongComparator;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.view.adapters.AlbumAdapter;
import com.rakesh.mobile.musicmasti.view.adapters.AllSongsAdapter;
import com.rakesh.mobile.musicmasti.view.adapters.SpacesItemDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by rakesh.jnanagari on 20/11/16.
 */

public class Search extends AppCompatActivity {

    private View albumContainer;
    private View songContainer;
    private View composerContainer;

    private EditText searchEditText;

    private RecyclerView albumRecyclerView;
    private RecyclerView songRecyclerView;
    private RecyclerView composerRecyclerView;

    private GridLayoutManager albumLayoutManager;
    private GridLayoutManager songLayoutManager;
    private GridLayoutManager composerLayoutManager;

    private AlbumAdapter mAlbumAdapter;
    private AllSongsAdapter mSongAdapter;
    private AlbumAdapter mComposerAdapter;

    private ArrayList<Album> albumList;
    private ArrayList<Song> songList;
    private ArrayList<Album> composerList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initApp();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 1) {
                    setAlbumList(s.toString());
                }
                if(s.toString().length() > 2) {
                    setComposerList(s.toString());
                }
                if(s.toString().length() > 3) {
                    setSongList(s.toString());
                }
            }
        });
        findViewById(R.id.search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }

    private void initApp() {
        albumContainer = findViewById(R.id.album_container);
        songContainer = findViewById(R.id.song_container);
        composerContainer = findViewById(R.id.composer_container);
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        albumRecyclerView = (RecyclerView) findViewById(R.id.album_recycler_view);
        songRecyclerView = (RecyclerView) findViewById(R.id.song_recycler_view);
        composerRecyclerView = (RecyclerView) findViewById(R.id.composer_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // noinspection ConstantConditions,ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        albumList = new ArrayList<>();
        songList = new ArrayList<>();
        composerList = new ArrayList<>();

        mAlbumAdapter = new AlbumAdapter(albumList, this, false, Constants.ALBUM_INTENT_VALUE);
        mSongAdapter = new AllSongsAdapter(songList, this, false);
        mComposerAdapter = new AlbumAdapter(composerList, this, false, Constants.COMPOSER_INTENT_VALUE);
        albumLayoutManager =
                new GridLayoutManager(this, Configuration.gridCount);
        songLayoutManager =
                new GridLayoutManager(this, Configuration.gridCount);
        composerLayoutManager =
                new GridLayoutManager(this, Configuration.gridCount);
        albumRecyclerView.setLayoutManager(albumLayoutManager);
        songRecyclerView.setLayoutManager(songLayoutManager);
        composerRecyclerView.setLayoutManager(composerLayoutManager);
        albumRecyclerView.addItemDecoration(new SpacesItemDecorator(Configuration.CONTAINER_LIST_SPACE));
        songRecyclerView.addItemDecoration(new SpacesItemDecorator(Configuration.CONTAINER_LIST_SPACE));
        composerRecyclerView.addItemDecoration(new SpacesItemDecorator(Configuration.CONTAINER_LIST_SPACE));

        albumRecyclerView.setAdapter(mAlbumAdapter);
        songRecyclerView.setAdapter(mSongAdapter);
        composerRecyclerView.setAdapter(mComposerAdapter);


    }

    private void setAlbumList(String searchText) {
        albumList.clear();
        Album album;
        HashMap<Integer, Album> albumMap;
        albumMap = new HashMap<>();
        Song song;
        int length = StaticData.songList.size();
        for(int i = 0; i < length; i++) {
            song = StaticData.songList.get(i);
            if(song.getAlbum() != null && (song.getAlbum().toLowerCase().startsWith(searchText.toLowerCase()) || song.getAlbum().toLowerCase().contains(" " + searchText.toLowerCase()))) {
                album = albumMap.get(song.getAlbumId());
                if (null == album) {
                    album = new Album();
                    album.setAlbumId(song.getAlbumId());
                    album.setTitle(song.getAlbum());
                    album.setSubTitle(song.getComposer());
                    album.setCount(1);
                    albumMap.put(song.getAlbumId(), album);
                } else {
                    album.setCount(album.getCount() + 1);
                }
                if (StaticData.getRecentlyPlayed().contains(song.getId())) {
                    int index = StaticData.getRecentlyPlayed().indexOf(new Integer(song.getId()));
                    if (album.getRecentlyPlayedPosition() == -1) {
                        album.setRecentlyPlayedPosition(index);
                    } else {
                        if (index < album.getRecentlyPlayedPosition()) {
                            album.setRecentlyPlayedPosition(index);
                        }
                    }
                }
            }
        }
        albumList.addAll(albumMap.values());
        Collections.sort(albumList, new AlbumComparator());
        if (albumList.size() > 0) {
            albumContainer.setVisibility(View.VISIBLE);
        } else {
            albumContainer.setVisibility(View.GONE);
        }
        mAlbumAdapter.notifyDataSetChanged();
    }

    private void setSongList(String searchText) {
        songList.clear();
        for(int i = 0; i < StaticData.songList.size(); i++) {
            if (StaticData.songList.get(i).getTitle().toLowerCase().startsWith(searchText.toLowerCase()) || StaticData.songList.get(i).getTitle().toLowerCase().contains(" " + searchText.toLowerCase())) {
                songList.add(StaticData.songList.get(i));
            }
        }
        Collections.sort(songList, new SongComparator());
        if(songList.size() > 0) {
            songContainer.setVisibility(View.VISIBLE);
            mSongAdapter.notifyDataSetChanged();
        } else {
            songContainer.setVisibility(View.GONE);
        }
    }

    private void setComposerList(String searchText) {
        composerList.clear();
        Album album;
        HashMap<String, Album> albumMap;
        albumMap = new HashMap<>();
        Song song;
        int length = StaticData.songList.size();
        for(int i = 0; i < length; i++) {
            song = StaticData.songList.get(i);
            String title = "";
            String subTitle = null;
            if (null == song.getComposer() || "<unknown>".equals(song.getComposer()))
                continue;
            if (song.getComposer().contains("Lyrics")) {
                String[] split = song.getComposer().split("Lyrics");
                title = split[0].replace("|", "");
                subTitle = getResources().getString(R.string.lyrics) + split[1];
            } else {
                title = song.getComposer();
            }
            if (title.toLowerCase().startsWith(searchText.toLowerCase()) || title.toLowerCase().contains(" " + searchText.toLowerCase())) {
                album = albumMap.get(title);
                if (null == album) {
                    album = new Album();
                    album.setAlbumId(song.getAlbumId());
                    album.setTitle(title);
                    album.setSubTitle(subTitle);
                    album.setCount(1);
                    albumMap.put(title, album);
                } else {
                    album.setCount(album.getCount() + 1);
                }
                if (StaticData.getRecentlyPlayed().contains(song.getId())) {
                    int index = StaticData.getRecentlyPlayed().indexOf(new Integer(song.getId()));
                    if (album.getRecentlyPlayedPosition() == -1) {
                        album.setRecentlyPlayedPosition(index);
                    } else {
                        if (index < album.getRecentlyPlayedPosition()) {
                            album.setRecentlyPlayedPosition(index);
                        }
                    }
                }
            }
        }
        composerList.addAll(albumMap.values());
        Collections.sort(composerList, new AlbumComparator());
        if(composerList.size() > 0) {
            composerContainer.setVisibility(View.VISIBLE);
            mComposerAdapter.notifyDataSetChanged();
        } else {
            composerContainer.setVisibility(View.GONE);
        }
    }
}
