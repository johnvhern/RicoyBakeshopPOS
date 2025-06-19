package com.pos.ricoybakeshop;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pos.ricoybakeshop.CartAdapter;
import com.pos.ricoybakeshop.ProductAdapter;
import com.pos.ricoybakeshop.CartItem;
import com.pos.ricoybakeshop.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class Fragment_Pos extends Fragment {

    private TextView txtTotal, txtTendered, txtChange, txtDateTime;
    private Spinner spinnerPaymentMethod;
    private RecyclerView recyclerProducts, recyclerCart;
    private ProductAdapter productAdapter;
    private CartAdapter cartAdapter;

    private List<Product> productList = new ArrayList<>();
    private List<CartItem> cartList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__pos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTotal = view.findViewById(R.id.txtTotal);
        txtTendered = view.findViewById(R.id.txtTendered);
        txtChange = view.findViewById(R.id.txtChange);
        txtDateTime = view.findViewById(R.id.txtDateTime);
        spinnerPaymentMethod = view.findViewById(R.id.spinnerPaymentMethod);

        recyclerProducts = view.findViewById(R.id.recyclerProducts);
        recyclerCart = view.findViewById(R.id.recyclerCart);

        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));

        productAdapter = new ProductAdapter(productList, this::onProductClick);
        cartAdapter = new CartAdapter(cartList);

        recyclerProducts.setAdapter(productAdapter);
        recyclerCart.setAdapter(cartAdapter);

//        loadProductsFromRoom();
        populateTestProducts();
        setDateTime();

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerProducts.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true));

        view.findViewById(R.id.btnConfirmPayment).setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            showTenderDialog();
        });
    }

    private void populateTestProducts() {
        try{
            productList.clear();

            Product p1 = new Product();
            p1.id = 1;
            p1.name = "Pandesal";
            p1.categoryId = 1;
            p1.price = 5.0;
            p1.imageUrl = "pandesal";

            Product p2 = new Product();
            p2.id = 2;
            p2.name = "Elorde";
            p2.categoryId = 2;
            p2.price = 5.0;
            p2.imageUrl = "elorde";

            Product p3 = new Product();
            p3.id = 3;
            p3.name = "Monay";
            p3.categoryId = 1;
            p3.price = 5.0;
            p3.imageUrl = "monay";

            productList.add(p1);
            productList.add(p2);
            productList.add(p3);

            productAdapter.notifyDataSetChanged();
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getContext(), "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setDateTime() {
        String dateTime = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm a", new Locale("en", "PH"))
                .format(new Date());
        txtDateTime.setText(dateTime);
    }

    private void loadProductsFromRoom() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Product> products = AppDatabase.getInstance(requireContext()).productDao().getAllProducts();
                requireActivity().runOnUiThread(() -> {
                    productList.clear();
                    productList.addAll(products);
                    productAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void onProductClick(Product product) {
        for (CartItem item : cartList) {
            if (item.product.id == product.id) {
                item.quantity++;
                updateCart();
                return;
            }
        }
        CartItem newItem = new CartItem();
        newItem.product = product;
        newItem.quantity = 1;
        cartList.add(newItem);
        updateCart();
    }

    private void updateCart() {
        cartAdapter.notifyDataSetChanged();
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getSubTotal();
        }
        txtTotal.setText("Total: ₱ " + String.format("%.2f", total));
    }

    private void showTenderDialog() {
        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(getContext())
                .setTitle("Enter Amount Tendered")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    try {
                        double tendered = Double.parseDouble(input.getText().toString());
                        double total = calculateTotal();
                        double change = tendered - total;

                        txtTendered.setText("Amount Tendered: ₱ " + String.format("%.2f", tendered));
                        txtChange.setText("Change: ₱ " + String.format("%.2f", change));

                        cartList.clear();
                        updateCart();
                        Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private double calculateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getSubTotal();
        }
        return total;
    }
}
