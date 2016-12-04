package com.rakesh.mobile.musicmasti.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rakesh.mobile.musicmasti.R;

/**
 * Created by rakesh.jnanagari on 04/07/16.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView thumbnail;
    public TextView count;
    public TextView title;
    public TextView subTitle;
    public View options;
    public RelativeLayout textLayout;
    public View parentLayout;

    public ViewHolder(View view, int position, boolean isHeaderset) {
        super(view);
        if(0 != position || !isHeaderset){
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            thumbnail.getLayoutParams().height = thumbnail.getLayoutParams().width;
            count = (TextView) view.findViewById(R.id.count);
            title = (TextView) view.findViewById(R.id.title);
            subTitle = (TextView) view.findViewById(R.id.sub_title);
            options = view.findViewById(R.id.options);
            textLayout = (RelativeLayout) view.findViewById(R.id.text_layout);
            parentLayout = view.findViewById(R.id.parent_layout);
        }
    }
}
