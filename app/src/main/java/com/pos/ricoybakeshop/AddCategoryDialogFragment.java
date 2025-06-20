package com.pos.ricoybakeshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddCategoryDialogFragment extends DialogFragment {

    public interface OnCategoryAddListener {
        void onCategoryAdded(String name, String iconName);
    }

    private OnCategoryAddListener listener;
    private List<String> iconNames;
    private String selectedIcon = null;

    public AddCategoryDialogFragment(List<String> iconNames, OnCategoryAddListener listener) {
        this.iconNames = iconNames;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText editName = view.findViewById(R.id.editCategoryName);
        RecyclerView iconRecyclerView = view.findViewById(R.id.iconRecyclerView);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        iconRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        IconAdapter adapter = new IconAdapter(iconNames, iconName -> selectedIcon = iconName);
        iconRecyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            if (name.isEmpty()) {
                editName.setError("Category name is required");
                return;
            }
            if (selectedIcon == null) {
                Toast.makeText(getContext(), "Please select an icon", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onCategoryAdded(name, selectedIcon);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set the dialog width to 90% of screen width
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.7);
            // Set height to WRAP_CONTENT
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            getDialog().getWindow().setLayout(width, height);
        }
    }
}

