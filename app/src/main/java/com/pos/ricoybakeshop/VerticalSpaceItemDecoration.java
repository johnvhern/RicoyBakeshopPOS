package com.pos.ricoybakeshop;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // Add top margin only for items other than the first one
        int position = parent.getChildAdapterPosition(view);
        if (position != 0) {
            outRect.top = verticalSpaceHeight;
        }
    }
}

