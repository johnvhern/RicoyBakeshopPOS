package com.pos.ricoybakeshop;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private List<Product> productList;
    private OnProductClickListener listener;

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    // for testing purposes only
    private String getCategoryName(int categoryId) {
        switch (categoryId) {
            case 1: return "Bread";
            case 2: return "Pastry";
            case 3: return "Drinks";
            default: return "Unknown";
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.txtName.setText(product.name);
        // remove getCategory if for real products
        holder.txtCategory.setText(getCategoryName(product.categoryId));
        holder.txtQuantity.setText(String.format(String.valueOf(product.quantity) + " pcs"));
        holder.txtPrice.setText("₱" + String.format("%.2f", product.price));

        // Load image (local drawable or URI)
        Context context = holder.itemView.getContext();
        if (product.imageUrl != null) {
            if (product.imageUrl.startsWith("file://") || product.imageUrl.startsWith("content://")) {
                Glide.with(context).load(Uri.parse(product.imageUrl)).into(holder.imageProduct);
            } else {
                int resId = context.getResources().getIdentifier(product.imageUrl, "drawable", context.getPackageName());
                if (resId != 0) {
                    holder.imageProduct.setImageResource(resId);
                } else {
                    holder.imageProduct.setImageResource(R.drawable.cookies);
                }
            }
        } else {
            holder.imageProduct.setImageResource(R.drawable.cookies);
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView txtName, txtCategory, txtPrice, txtQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtQuantity = itemView.findViewById(R.id.txtProductQuantity);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }
}
