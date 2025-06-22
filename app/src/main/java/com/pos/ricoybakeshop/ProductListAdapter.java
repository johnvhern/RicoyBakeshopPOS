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
import java.util.Map;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private Map<Integer, String> categoryMap;

    AppDatabase db;

    public ProductListAdapter(Context context, List<Product> productList, Map<Integer, String> categoryMap) {
        this.context = context;
        this.productList = productList;
        this.categoryMap = categoryMap;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.txtName.setText(product.name);
        holder.txtCategory.setText(categoryMap.get(product.categoryId));
        holder.txtQuantity.setText(String.format(String.valueOf(product.quantity) + " pcs"));
        holder.txtPrice.setText("₱" + String.format("%.2f", product.price));

        if (product.imageUrl != null) {
            Uri imageUri = Uri.parse(product.imageUrl);
            Glide.with(context).load(imageUri).into(holder.imageProduct);

        } else {
            holder.imageProduct.setImageResource(R.drawable.cookie);
        }
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
            imageProduct = itemView.findViewById(R.id.productImage);
            txtName = itemView.findViewById(R.id.productName);
            txtCategory = itemView.findViewById(R.id.productCategory);
            txtQuantity = itemView.findViewById(R.id.productQuantity);
            txtPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}
