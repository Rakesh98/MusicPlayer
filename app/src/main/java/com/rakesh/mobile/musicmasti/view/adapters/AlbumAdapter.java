package com.rakesh.mobile.musicmasti.view.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Album;
import com.rakesh.mobile.musicmasti.model.Queue;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.model.ViewHolder;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.SharedPreference;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.MusicContainer;
import com.rakesh.mobile.musicmasti.view.PlayerService;
import com.rakesh.mobile.musicmasti.view.SongList;
import com.rakesh.mobile.musicmasti.view.Splash;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh.jnanagari on 09/07/16.
 */
public class AlbumAdapter extends RecyclerView.Adapter<ViewHolder> {
  private List<Album> albumList;
  private Activity context;
  private boolean isHeaderSet;
  private int intentType;

  public AlbumAdapter(List<Album> albumList, Activity context, boolean isHeaderSet,
      int intentType) {
    this.albumList = albumList;
    this.context = context;
    this.isHeaderSet = isHeaderSet;
    this.intentType = intentType;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView;
    if (0 == viewType && isHeaderSet) {
      LinearLayout linearLayout = new LinearLayout(context);
      View view = new View(context);
      linearLayout.addView(view);
      view.getLayoutParams().height = StaticData.toolBarHeight;

      itemView = linearLayout;
    } else {
      if (Configuration.gridView) {
        itemView =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
      } else {
        itemView =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
      }
    }

    return new ViewHolder(itemView, viewType, isHeaderSet);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    if (isHeaderSet) {
      if (position == 0) {
        return;
      } else {
        position = position - 1;
      }
    }

    Album album = albumList.get(position);
    holder.title.setText(album.getTitle());

    if (Configuration.gridView) {
      holder.count.setText(album.getCount() + "");
    } else {
      holder.count
          .setText(context.getResources().getString(R.string.songs) + " " + album.getCount());
      if (null != album.getSubTitle()) {
        holder.subTitle.setVisibility(View.VISIBLE);
        holder.subTitle.setText(album.getSubTitle());
      } else {
        holder.subTitle.setVisibility(View.GONE);
      }
    }
    if (Configuration.gridView) {
      holder.thumbnail.getLayoutParams().height = Configuration.musicContainerListThumbnailDimen;
      holder.thumbnail.getLayoutParams().width = Configuration.musicContainerListThumbnailDimen;
      holder.textLayout
          .setBackgroundDrawable(context.getResources().getDrawable(R.drawable.gradient_grid));
    }
    Utils.setBitMap(context, holder.thumbnail, albumList.get(position).getAlbumId());
    if (!Configuration.gridView) {
      holder.parentLayout = holder.textLayout;
    }

    holder.options.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isHeaderSet) {
          showOptionsDialog(holder.getAdapterPosition() - 1);
        } else {
          showOptionsDialog(holder.getAdapterPosition());
        }
      }
    });
    holder.parentLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        holder.thumbnail.buildDrawingCache();
        Bitmap bitmap = holder.thumbnail.getDrawingCache();
        Palette palette = Palette.from(bitmap).generate();
        Intent intent = new Intent(context, SongList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.SONG_LIST_INTETN_KEY, intentType);
        if (isHeaderSet) {
          intent.putExtra(Constants.ALBUM_ID_KEY,
              albumList.get(holder.getAdapterPosition() - 1).getAlbumId());
        } else {
          intent.putExtra(Constants.ALBUM_ID_KEY,
              albumList.get(holder.getAdapterPosition()).getAlbumId());
        }

        intent.putExtra(Constants.ITEM_COLOR_KEY,
            palette.getDarkVibrantColor(Color.parseColor("#E0E0E0")));
        intent.putExtra(Constants.FLOATING_BUTTON_COLOR,
            palette.getMutedColor(Color.parseColor("#D84315")));
        intent.putExtra(Constants.TOOLBAR_COLOR_KEY,
            palette.getMutedColor(Color.parseColor("#757575")));
        intent.putExtra(Constants.STATUSBAR_COLOR,
            palette.getDarkMutedColor(Color.parseColor("#424242")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
          ActivityOptionsCompat options =
              ActivityOptionsCompat.makeSceneTransitionAnimation(context, holder.thumbnail,
                  context.getResources().getString(R.string.transition_thumbnai));
          context.startActivity(intent, options.toBundle());
        } else {
          context.startActivity(intent);
        }
        if (!isHeaderSet) {
          context.finish();
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    if (isHeaderSet) {
      return albumList.size() + 1;
    } else {
      return albumList.size();
    }
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }

  private void showOptionsDialog(final int position) {
    final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
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
    if (intentType == Constants.PLAY_LIST_INTENT_VALUE) {
      ((TextView)dialog.findViewById(R.id.remove)).setText(R.string.remove_from_playlist);
    }
    dialog.findViewById(R.id.enque_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        List<Queue> queueList = new ArrayList<>();
        List<Song> songList = setSongList(position);
        for (int k = 0; k < songList.size(); k++) {
          Queue queue = new Queue();
          queue.setSong(songList.get(k));
          queueList.add(queue);
        }
        if (null != PlayerService.getInstance()) {
          PlayerService.getInstance().addToQueue(queueList, false, -1);
        } else {
          context.finish();
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
        Utils.shareFiles(context, getSongUriList(position));
      }
    });
    dialog.findViewById(R.id.info_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showInfoDialog(position);
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.delete_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (intentType == Constants.PLAY_LIST_INTENT_VALUE) {

        }
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.add_to_playlist).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.addToPlayList(setSongList(position), context);
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

  private List<Song> setSongList(int position) {
    List<Song> songList = new ArrayList<>();
    for (int i = 0; i < StaticData.songList.size(); i++) {
      if (StaticData.songList.get(i).getAlbumId() == albumList.get(position).getAlbumId()) {
        songList.add(StaticData.songList.get(i));
      }
    }
    return songList;
  }

  private void showInfoDialog(int position) {
    final Dialog dialog = new Dialog(context);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.setContentView(R.layout.popup_info_group);

    dialog.setCanceledOnTouchOutside(true);
    dialog.setCancelable(true);
    dialog.show();
    ArrayList<String> artistlist = new ArrayList<>();
    String artists = "", album = "", composer = "";
    long size = 0l, duration = 0l;
    for (int i = 0; i < StaticData.songList.size(); i++) {
      if (StaticData.songList.get(i).getAlbumId() == albumList.get(position).getAlbumId()) {
        if (null != StaticData.songList.get(i).getArtist()) {
          String[] tempArray = StaticData.songList.get(i).getArtist().split(",");
          for (int j = 0; j < tempArray.length; j++) {
            if (!artistlist.contains(tempArray[j])) {
              artistlist.add(tempArray[j]);
            }
          }
        }
        if (album.isEmpty() && null != StaticData.songList.get(i).getAlbum()) {
          album = StaticData.songList.get(i).getAlbum();
        }
        if (composer.isEmpty() && null != StaticData.songList.get(i).getComposer()) {
          composer = StaticData.songList.get(i).getComposer();
          if (composer.contains("Lyrics")) {
            String[] split = composer.split("Lyrics");
            composer = split[0].replace("|", "");
          }
        }
        size += Long.parseLong(StaticData.songList.get(i).getSize());
        duration += Long.parseLong(StaticData.songList.get(i).getDuration());
      }

    }
    for (int i = 0; i < artistlist.size(); i++) {
      if (artists.isEmpty()) {
        artists = artists + artistlist.get(i);
      } else {
        artists = artists + ", " + artistlist.get(i);
      }
    }
    ((TextView) dialog.findViewById(R.id.artist)).setText(artists);
    ((TextView) dialog.findViewById(R.id.composer)).setText(composer);
    ((TextView) dialog.findViewById(R.id.tracks))
        .setText(String.valueOf(albumList.get(position).getCount()));
    ((TextView) dialog.findViewById(R.id.album)).setText(album);
    ((TextView) dialog.findViewById(R.id.duration)).setText(Utils.milliSecondsToTimer(duration));
    ((TextView) dialog.findViewById(R.id.size)).setText(Utils.convertBytesToMb(size));
    dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

  }

  private ArrayList<Uri> getSongUriList(int position) {
    ArrayList<Uri> uris = new ArrayList<>();
    if (intentType == Constants.ALBUM_INTENT_VALUE) {
      for (int i = 0; i < StaticData.songList.size(); i++) {
        if (albumList.get(position).getAlbumId() == StaticData.songList.get(i).getAlbumId()) {
          File file = new File(StaticData.songList.get(i).getContentUri().toString());
          uris.add(Uri.fromFile(file));
        }
      }

    } else if (intentType == Constants.COMPOSER_INTENT_VALUE) {
      String composerName = null;
      for (int i = 0; i < StaticData.songList.size(); i++) {
        Song song = StaticData.songList.get(i);
        if (song.getAlbumId() == albumList.get(position).getAlbumId()) {
          if (song.getComposer() != null && song.getComposer().contains("Lyrics")) {
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
          if (StaticData.songList.get(i).getComposer() != null
              && StaticData.songList.get(i).getComposer().contains("Lyrics")) {
            String[] split = StaticData.songList.get(i).getComposer().split("Lyrics");
            tempComposer = split[0].replace("|", "");
          } else {
            tempComposer = StaticData.songList.get(i).getComposer();
          }

          if (null != tempComposer && tempComposer.trim().equalsIgnoreCase(composerName.trim())) {
            File file = new File(StaticData.songList.get(i).getContentUri().toString());
            uris.add(Uri.fromFile(file));
          }
        }
      } else {
        for (int i = 0; i < StaticData.songList.size(); i++) {
          if (StaticData.songList.get(i).getAlbumId() == albumList.get(position).getAlbumId()) {
            File file = new File(StaticData.songList.get(i).getContentUri().toString());
            uris.add(Uri.fromFile(file));
          }
        }
      }

    } else if (intentType == Constants.PLAY_LIST_INTENT_VALUE) {
      for (int i = 0; i < StaticData.songList.size(); i++) {
        if (StaticData.songList.get(i).getAlbumId() == albumList.get(position).getAlbumId()
            && StaticData.getPlayListSelected().contains(StaticData.songList.get(i).getId())) {
          File file = new File(StaticData.songList.get(i).getContentUri().toString());
          uris.add(Uri.fromFile(file));
        }
      }
    }
    return uris;
  }
}
