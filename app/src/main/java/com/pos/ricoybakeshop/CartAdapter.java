package com.pos.ricoybakeshop;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.ricoybakeshop.R;
import com.pos.ricoybakeshop.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private OnCartChangeListener cartChangeListener;

    public CartAdapter(List<CartItem> cartList, OnCartChangeListener cartChangeListener) {
        this.cartList = cartList;
        this.cartChangeListener = cartChangeListener;
    }

    public interface OnCartChangeListener{
        void onCartChanged(int position);
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
        holder.txtSubtotal.setText("₱ " + String.format("%.2f", item.getSubTotal()));

        // Remove old watcher
        if (holder.qtyWatcher != null) {
            holder.txtQty.removeTextChangedListener(holder.qtyWatcher);
        }

        // Safely update quantity text
        String qtyText = String.valueOf(item.quantity);
        if (!holder.txtQty.getText().toString().equals(qtyText)) {
            holder.txtQty.setText(qtyText);
            holder.txtQty.setSelection(qtyText.length());
        }

        // Plus button
        holder.btnPlus.setOnClickListener(v -> {
            item.quantity++;
            holder.txtQty.setText(String.valueOf(item.quantity));
            holder.txtSubtotal.setText("₱ " + String.format("%.2f", item.getSubTotal()));

            if (cartChangeListener != null) {
                cartChangeListener.onCartChanged(holder.getAdapterPosition());
            }
        });

        // Minus button
        holder.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                holder.txtQty.setText(String.valueOf(item.quantity));
                holder.txtSubtotal.setText("₱ " + String.format("%.2f", item.getSubTotal()));

                if (cartChangeListener != null) {
                    cartChangeListener.onCartChanged(holder.getAdapterPosition());
                }
            }
        });

        // Manual edit watcher
        holder.qtyWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (input.isEmpty()) return;

                try {
                    int qty = Integer.parseInt(input);
                    if (qty != item.quantity) {
                        item.quantity = qty;
                        holder.txtSubtotal.setText("₱ " + String.format("%.2f", item.getSubTotal()));
                        if (cartChangeListener != null) {
                            cartChangeListener.onCartChanged(holder.getAdapterPosition());
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        };

        holder.txtQty.addTextChangedListener(holder.qtyWatcher);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSubtotal;
        EditText txtQty;

        ImageButton btnPlus, btnMinus;

        TextWatcher qtyWatcher;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCartProductName);
            txtQty = itemView.findViewById(R.id.txtCartQuantity);
            txtSubtotal = itemView.findViewById(R.id.txtCartSubtotal);
            btnPlus = itemView.findViewById(R.id.btnAddQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinusQuantity);
        }
    }
}
