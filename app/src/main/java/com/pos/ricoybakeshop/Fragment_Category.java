package com.pos.ricoybakeshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fragment_Category extends Fragment {

    RecyclerView categoryRecyclerView;
    CategoryAdapter adapter;
    List<ProductCategory> categoryList = new ArrayList<>();

    ImageButton btnAdd;

    AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment__category, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            List<String> iconNames = Arrays.asList("apple", "banana", "bean", "beef", "beer", "bread", "cake", "coffee", "cookies", "croissant", "cup_soda", "dessert",
                    "donut", "grocery_bag", "ice_cream_cone", "pizza", "sandwich", "soup", "utensils", "vegan", "wheat", "beverages");

            AddCategoryDialogFragment dialog = new AddCategoryDialogFragment(iconNames, (name, icon) -> {
                ProductCategory category = new ProductCategory(name, icon);
                new Thread(() -> {
                    db.categoryDao().insert(category);
                    requireActivity().runOnUiThread(this::loadCategories);
                }).start();
            });
            dialog.show(getParentFragmentManager(), "AddCategoryDialogFragment");

        });

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new CategoryAdapter(requireContext(), categoryList);
        categoryRecyclerView.setAdapter(adapter);

        loadCategories();

    }

    private void loadCategories() {
        new Thread(() -> {
            try {
                List<ProductCategory> categories = db.categoryDao().getAll();
                requireActivity().runOnUiThread(() -> {
                    categoryList.clear();
                    categoryList.addAll(categories);
                    adapter.notifyDataSetChanged();
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