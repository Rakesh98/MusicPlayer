package com.rakesh.mobile.musicmasti.view.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.utils.Utils;
import com.rakesh.mobile.musicmasti.view.PlayerService;

/**
 * Created by rakesh.jnanagari on 19/07/16.
 */
public class PlayQueueAdapter extends RecyclerView.Adapter<PlayQueueAdapter.ViewHolder> {
  private Activity context;

  public PlayQueueAdapter(Activity context) {
    this.context = context;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_queue, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    if(null != PlayerService.getInstance()) {
      Utils.setBitMap(context, holder.thumbnail,
              PlayerService.getInstance().mQueue.get(position).getSong().getAlbumId());
      holder.title.setText(PlayerService.getInstance().mQueue.get(position).getSong().getDisplayName());

    if(PlayerService.getInstance().getPlayingSongPosition() == position) {
      holder.parentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
    }
    else {
      holder.parentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent_background));
    }
    holder.remove.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PlayerService.getInstance().deleteSongFromQueue(holder.getAdapterPosition());
        notifyDataSetChanged();
      }
    });
    holder.parentLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PlayerService.getInstance().playAtPositionFromQueue(holder.getAdapterPosition());
        notifyDataSetChanged();
      }
    });
    } else {
      context.finish();
    }
  }

  @Override
  public int getItemCount() {
    if(PlayerService.getInstance() != null) {
      return PlayerService.getInstance().mQueue.size();
    } else {
      context.finish();
      return 0;
    }
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView thumbnail;
    TextView title;
    ImageView remove;
    View parentLayout;

    public ViewHolder(View itemView) {
      super(itemView);
      thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
      title = (TextView) itemView.findViewById(R.id.title);
      remove = (ImageView) itemView.findViewById(R.id.close);
      parentLayout = itemView.findViewById(R.id.parent_layout);
    }
  }

}
