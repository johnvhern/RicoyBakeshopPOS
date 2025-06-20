package com.pos.ricoybakeshop;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

    private final List<String> iconNames;
    private final OnIconClickListener listener;
    private String selectedIcon = "";

    public interface OnIconClickListener {
        void onIconClick(String iconName);
    }

    public IconAdapter(List<String> iconNames, OnIconClickListener listener) {
        this.iconNames = iconNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, parent.getResources().getDisplayMetrics());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        imageView.setPadding(8, 8, 8, 8);
        return new IconViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        String iconName = iconNames.get(position);
        Context context = holder.itemView.getContext();
        int resId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
        holder.imageView.setImageResource(resId);

        holder.itemView.setOnClickListener(v -> {
            selectedIcon = iconName;
            listener.onIconClick(iconName);
            notifyDataSetChanged();
        });

        // Optionally highlight selected icon
        holder.imageView.setAlpha(iconName.equals(selectedIcon) ? 1.0f : 0.5f);
    }

    @Override
    public int getItemCount() {
        return iconNames.size();
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        IconViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}

