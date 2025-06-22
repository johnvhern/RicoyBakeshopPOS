package com.pos.ricoybakeshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddProductDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setBackgroundResource(R.drawable.rounded_background);

        Button btnCancel = view.findViewById(R.id.btnCancelProduct);

        btnCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public int getTheme() {
        return R.style.NoBackgroundDialogTheme;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set the dialog width to 90% of screen width
            int width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.4);
            // Set height to WRAP_CONTENT
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            getDialog().getWindow().setLayout(width, height);
        }
    }
}
