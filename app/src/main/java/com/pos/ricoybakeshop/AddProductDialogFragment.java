package com.pos.ricoybakeshop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddProductDialogFragment extends DialogFragment {

    public interface OnProductAddListener {
        void onProductAdded(String name, int categoryId, double productPrice, int productQuantity, String imageUrl);
    }

    private OnProductAddListener listener;
    private List<ProductCategory> categoryList;
    private Uri selectedImageUri;
    private ImageView productImage;

    public AddProductDialogFragment(List<ProductCategory> categoryList, OnProductAddListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setBackgroundResource(R.drawable.rounded_background);

        EditText productName = view.findViewById(R.id.productName);
        TextInputEditText productPrice = view.findViewById(R.id.productPrice);
        TextInputEditText productQuantity = view.findViewById(R.id.productQuantity);
        MaterialButton btnAdd = view.findViewById(R.id.btnAddProduct);
        AutoCompleteTextView selectedCategory = view.findViewById(R.id.productCategorySpinner);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelProduct);
        productImage = view.findViewById(R.id.productImageView);

        productImage.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(0)
                    .start();
        });

        List<String> categoryNames = new ArrayList<>();
        for (ProductCategory category : categoryList) {
            categoryNames.add(category.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
        );
        selectedCategory.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String name = productName.getText().toString().trim();
            String priceStr = productPrice.getText().toString().trim();
            String quantityStr = productQuantity.getText().toString().trim();
            String categoryStr = selectedCategory.getText().toString().trim();

            if (name.isEmpty()) {
                productName.setError("Required");
                return;
            }

            if (priceStr.isEmpty()) {
                productPrice.setError("Required");
                return;
            }

            if (quantityStr.isEmpty()) {
                productQuantity.setError("Required");
                return;
            }

            if (categoryStr.isEmpty()) {
                selectedCategory.setError("Required");
                return;
            }

            if (selectedImageUri == null) {
                Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            int categoryId = -1;
            for (ProductCategory category : categoryList) {
                if (category.name.equals(categoryStr)) {
                    categoryId = category.id;
                    break;
                }
            }

            if (categoryId == -1) {
                selectedCategory.setError("Invalid category");
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                int quantity = Integer.parseInt(quantityStr);

                listener.onProductAdded(name, categoryId, price, quantity, selectedImageUri.toString());
                dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            productImage.setImageURI(selectedImageUri);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getTheme() {
        return R.style.NoBackgroundDialogTheme;
    }
}