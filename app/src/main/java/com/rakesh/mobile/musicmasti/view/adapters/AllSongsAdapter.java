package com.rakesh.mobile.musicmasti.view.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.model.Queue;
import com.rakesh.mobile.musicmasti.model.Song;
import com.rakesh.mobile.musicmasti.model.ViewHolder;
import com.rakesh.mobile.musicmasti.utils.Configuration;
import com.rakesh.mobile.musicmasti.utils.Constants;
import com.rakesh.mobile.musicmasti.utils.SharedPreference;
import com.rakesh.mobile.musicmasti.utils.StaticData;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.MusicContainer;
import com.rakesh.mobile.musicmasti.view.NowPlaying;
import com.rakesh.mobile.musicmasti.view.PlayerService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh.jnanagari on 04/07/16.
 */
public class AllSongsAdapter extends RecyclerView.Adapter<ViewHolder> {
  private List<Song> songList;
  private Activity context;
  private List<Queue> queueList;
  private boolean isHeaderSet;

  public AllSongsAdapter(List<Song> songList, Activity context, boolean isHeaderSet) {
    this.songList = songList;
    this.context = context;
    queueList = new ArrayList<>();
    this.isHeaderSet = isHeaderSet;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView;
    if(0 == viewType && isHeaderSet) {
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

      final Song song = songList.get(position);
      holder.title.setText(song.getTitle());
      holder.subTitle.setText(song.getDisplayName());
      holder.count.setVisibility(View.GONE);
      if(Configuration.gridView) {
        holder.thumbnail.getLayoutParams().height = Configuration.musicContainerListThumbnailDimen;
        holder.thumbnail.getLayoutParams().width = Configuration.musicContainerListThumbnailDimen;
        holder.textLayout
                .setBackgroundDrawable(context.getResources().getDrawable(R.drawable.gradient_grid));
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
          queueList.clear();
          Queue queue;
          for (int i = 0; i < songList.size(); i++) {
            queue = new Queue();
            queue.setSong(songList.get(i));
            queueList.add(queue);
          }

          if(null != PlayerService.getInstance()) {
            if (isHeaderSet) {
              PlayerService.getInstance().addToQueue(queueList, true, holder.getAdapterPosition()-1);
            } else {
              PlayerService.getInstance().addToQueue(queueList, true, holder.getAdapterPosition());
            }
          } else {
            context.finish();
          }
          Intent intent = new Intent(context, NowPlaying.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          holder.thumbnail.buildDrawingCache();
          Bitmap bitmap = holder.thumbnail.getDrawingCache();
          Palette palette = Palette.from(bitmap).generate();
          intent.putExtra(Constants.STATUSBAR_COLOR, palette.getDarkMutedColor(Color.parseColor("#424242")));
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(context, holder.thumbnail,
                            context.getResources().getString(R.string.transition_thumbnai_two));
            context.startActivity(intent, options.toBundle());
          } else {
            context.startActivity(intent);
          }
          if(!isHeaderSet) {
            context.finish();
          }
        }
      });

      Utils.setBitMap(context, holder.thumbnail,
              songList.get(position).getAlbumId());

  }

  @Override
  public int getItemCount() {
    if(isHeaderSet) {
      return songList.size() + 1;
    } else {
      return songList.size();
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
    dialog.findViewById(R.id.enque_layout).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        queueList.clear();
        Queue queue = new Queue();
        queue.setSong(songList.get(position));
        queueList.add(queue);
        if(null != PlayerService.getInstance()) {
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
        setRingTone(position);
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
        showInfoDialog(songList.get(position));
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
        Utils.addToPlayList(songList.get(position), context);
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

  private void setRingTone(int position) {
    File songFile = new File("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + songList.get(position).getContentUri().getPath());
    ContentValues values = new ContentValues();
    values.put(MediaStore.MediaColumns.DATA, songList.get(position).getContentUri().toString());
    values.put(MediaStore.MediaColumns.TITLE, "ring");
    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
    values.put(MediaStore.MediaColumns.SIZE, songList.get(position).getDuration());
    values.put(MediaStore.Audio.Media.ARTIST, songList.get(position).getArtist());
    values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
    values.put(MediaStore.Audio.Media.IS_ALARM, true);
    values.put(MediaStore.Audio.Media.IS_MUSIC, false);

    Uri uri = MediaStore.Audio.Media.getContentUriForPath(songList.get(position).getContentUri().toString());
    Uri songUri = context.getContentResolver().insert(uri, values);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if(Settings.System.canWrite(context)) {
        try {
          RingtoneManager.setActualDefaultRingtoneUri(context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE, songUri);
        } catch (Throwable t) {
          Log.d("debug", t.getStackTrace().toString());
        }
      } else {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);
      }
    } else {
      try {
        RingtoneManager.setActualDefaultRingtoneUri(context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE, songUri);
      } catch (Throwable t) {
        Log.d("debug", t.getStackTrace().toString());
      }
    }

  }

  private void showInfoDialog(Song song) {
    final Dialog dialog = new Dialog(context);
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
