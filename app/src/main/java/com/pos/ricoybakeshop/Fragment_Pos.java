package com.pos.ricoybakeshop;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

public class Fragment_Pos extends Fragment {

    private TextView txtTotal, txtTendered, txtChange, txtDateTime;
    private MaterialAutoCompleteTextView spinnerPaymentMethod;
    private RecyclerView recyclerProducts, recyclerCart, recycleCategoryCard;
    private ProductAdapter productAdapter;
    private CartAdapter cartAdapter;

    AppDatabase db;
    private POS_Category_Tab categoryAdapter;

    private List<Product> productList = new ArrayList<>();
    private List<CartItem> cartList = new ArrayList<>();

    List<ProductCategory> categoryList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__pos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init views...
        txtTotal = view.findViewById(R.id.txtTotal);
        txtTendered = view.findViewById(R.id.txtTendered);
        txtChange = view.findViewById(R.id.txtChange);
        txtDateTime = view.findViewById(R.id.txtDateTime);

        recyclerProducts = view.findViewById(R.id.recyclerProducts);
        recyclerCart = view.findViewById(R.id.recyclerCart);
        recycleCategoryCard = view.findViewById(R.id.categoryRecyclerView);

        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartList);
        recyclerCart.setAdapter(cartAdapter);

        recycleCategoryCard.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Initialize DB
        db = AppDatabase.getInstance(requireContext());

        // Load categories and setup productAdapter safely
        new Thread(() -> {
            List<ProductCategory> categories = db.categoryDao().getAll();
            List<ProductDao.CategoryCount> counts = db.productDao().getProductCount();

            Map<Integer, String> categoryIdToName = new HashMap<>();
            for (ProductCategory category : categories) {
                categoryIdToName.put(category.id, category.name);
            }

            Map<Integer, Integer> countMap = new HashMap<>();
            for (ProductDao.CategoryCount count : counts) {
                countMap.put(count.categoryId, count.count);
            }

            for (ProductCategory category : categories) {
                category.productCount = countMap.getOrDefault(category.id, 0);
            }

            requireActivity().runOnUiThread(() -> {
                // Initialize adapters
                productAdapter = new ProductAdapter(requireContext(), productList, categoryIdToName, this::onProductClick);
                recyclerProducts.setAdapter(productAdapter);

                categoryList.clear();
                categoryList.addAll(categories);
                categoryAdapter = new POS_Category_Tab(getContext(), categoryList, category -> {
                    Log.d("POS", "Selected category: " + category.name);
                });
                recycleCategoryCard.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();

                // load products
                loadProductsFromRoom();
            });
        }).start();

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

    private void setDateTime() {
        String dateTime = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm a", new Locale("en", "PH"))
                .format(new Date());
        txtDateTime.setText(dateTime);
    }

    private void loadProductsFromRoom() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Product> products = db.productDao().getAllProducts();
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
        TenderAmountDialog dialog = new TenderAmountDialog(tendered -> {
            double total = calculateTotal();
            double change = tendered - total;

            txtTendered.setText("Amount Tendered: ₱ " + String.format("%.2f", tendered));
            txtChange.setText("Change: ₱ " + String.format("%.2f", change));

            cartList.clear();
            updateCart();
            Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
        });
        dialog.show(getChildFragmentManager(), "TenderDialog");

    }

    private double calculateTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.getSubTotal();
        }
        return total;
    }
}
