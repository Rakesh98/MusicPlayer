package com.rakesh.mobile.musicmasti.view.adapters;

import com.rakesh.mobile.musicmasti.R;
import com.rakesh.mobile.musicmasti.utils.StaticData;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by rakesh.jnanagari on 27/12/16.
 */

public class SelectPlayListAdapter extends RecyclerView.Adapter<SelectPlayListAdapter.ViewHolder> {
  public int selectedPosition = -1;

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_playlist, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.nameTextView.setText(StaticData.getPlayListNames().get(position));
    if (selectedPosition == position) {
      holder.radioButton.setChecked(true);
    } else {
      holder.radioButton.setChecked(false);
    }
    holder.radioButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectedPosition = holder.getAdapterPosition();
        notifyDataSetChanged();
      }
    });
    holder.parent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectedPosition = holder.getAdapterPosition();
        notifyDataSetChanged();
      }
    });
  }

  @Override
  public int getItemCount() {
    return StaticData.getPlayListLibrary().size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    TextView nameTextView;
    RadioButton radioButton;
    View parent;

    public ViewHolder(View itemView) {
      super(itemView);
      parent = itemView;
      radioButton = (RadioButton) itemView.findViewById(R.id.select_playlist);
      nameTextView = (TextView) itemView.findViewById(R.id.playlist_name);

    }
  }
}
