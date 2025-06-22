package com.pos.ricoybakeshop;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class POS_Category_Tab extends RecyclerView.Adapter<POS_Category_Tab.ViewHolder> {

    private final List<ProductCategory> categoryList;
    private final Context context;
    private int selectedPosition = 0;

    private OnCategoryClickListener listener;

    public POS_Category_Tab(Context context, List<ProductCategory> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(ProductCategory category);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pos_category_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCategory category = categoryList.get(position);
        holder.categoryName.setText(category.name);

        String item;
        if (category.productCount >= 2){
            item = "Items";
        }else{
            item = "Item";
        }
        holder.totalProducts.setText(String.format(String.valueOf(category.productCount) + " " + item));

        int resId = context.getResources().getIdentifier(
                category.iconName, "drawable", context.getPackageName());
        holder.iconImageView.setImageResource(resId);

        boolean isSelected = position == selectedPosition;

        // Set outline if selected
        if (isSelected) {
            holder.cardView.setStrokeWidth(2);
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.brown));
        } else {
            holder.cardView.setStrokeWidth(0);
            holder.cardView.setStrokeColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();

            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(categoryList.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView iconImageView;
        TextView categoryName;
        TextView totalProducts;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            categoryName = itemView.findViewById(R.id.categoryName);
            totalProducts = itemView.findViewById(R.id.totalProducts);
        }
    }
}
