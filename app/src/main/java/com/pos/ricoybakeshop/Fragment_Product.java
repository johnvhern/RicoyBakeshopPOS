package com.pos.ricoybakeshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_Product extends Fragment {

    ImageButton btnAdd;
    AppDatabase db;

    List<Product> productList = new ArrayList<>();
    ProductListAdapter productAdapter;
    RecyclerView productRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());
        btnAdd = view.findViewById(R.id.btnAdd);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load categories and setup adapter
        new Thread(() -> {
            List<ProductCategory> categories = db.categoryDao().getAll();
            Map<Integer, String> categoryMap = new HashMap<>();
            for (ProductCategory category : categories) {
                categoryMap.put(category.id, category.name);
            }

            requireActivity().runOnUiThread(() -> {
                productAdapter = new ProductListAdapter(requireContext(), productList, categoryMap);
                productRecyclerView.setAdapter(productAdapter);
                loadProducts(); // Load products after adapter is ready

                btnAdd.setOnClickListener(v -> {
                    AddProductDialogFragment dialog = new AddProductDialogFragment(categories, (name, categoryId, price, quantity, imageUrl) -> {
                        Product product = new Product(name, categoryId, price, quantity, imageUrl);
                        new Thread(() -> {
                            db.productDao().insert(product);
                            requireActivity().runOnUiThread(this::loadProducts);
                        }).start();
                    });
                    dialog.show(getParentFragmentManager(), "AddProductDialogFragment");
                });
            });
        }).start();
    }

    private void loadProducts() {
        new Thread(() -> {
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
                        Toast.makeText(getContext(), "DB Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}