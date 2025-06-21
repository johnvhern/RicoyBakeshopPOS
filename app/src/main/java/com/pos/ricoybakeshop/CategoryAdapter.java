package com.pos.ricoybakeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final List<ProductCategory> categoryList;
    private final Context context;
    public CategoryAdapter(Context context, List<ProductCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCategory category = categoryList.get(position);
        holder.categoryName.setText(category.name);

        int resId = context.getResources().getIdentifier(
                category.iconName, "drawable", context.getPackageName());
        holder.iconImageView.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView categoryName;

        ViewHolder(View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}

