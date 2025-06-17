package com.pos.ricoybakeshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<MenuItem> menuList;
    private OnMenuItemClickListener listener;
    private int selectedPosition = 0; // Default first item selected

    public interface OnMenuItemClickListener {
        void onMenuItemClick(int position);
    }

    public MenuAdapter(List<MenuItem> menuList, OnMenuItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        holder.icon.setImageResource(item.getIconResId());
        holder.title.setText(item.getTitle());

        boolean isSelected = position == selectedPosition;
        holder.itemView.setSelected(isSelected);
        holder.title.setSelected(isSelected);
        holder.icon.setSelected(isSelected);

        // Optional: change background manually (if not using selector)
        holder.itemView.setBackgroundResource(isSelected ? R.drawable.menu_item_selector : android.R.color.transparent);

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);

            listener.onMenuItemClick(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.menu_icon);
            title = itemView.findViewById(R.id.menu_title);
        }
    }
}