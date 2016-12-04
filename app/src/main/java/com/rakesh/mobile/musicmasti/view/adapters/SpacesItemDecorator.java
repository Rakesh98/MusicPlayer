package com.rakesh.mobile.musicmasti.view.adapters;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by rakesh.jnanagari on 06/07/16.
 */
public class SpacesItemDecorator extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecorator(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top = space;
    }
}
