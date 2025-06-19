package com.pos.ricoybakeshop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.ricoybakeshop.R;
import com.pos.ricoybakeshop.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartList;

    public CartAdapter(List<CartItem> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.txtName.setText(item.product.name);
        holder.txtQty.setText("Qty: " + item.quantity);
        holder.txtSubtotal.setText("₱ " + String.format("%.2f", item.getSubTotal()));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, txtSubtotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCartProductName);
            txtQty = itemView.findViewById(R.id.txtCartQuantity);
            txtSubtotal = itemView.findViewById(R.id.txtCartSubtotal);
        }
    }
}
