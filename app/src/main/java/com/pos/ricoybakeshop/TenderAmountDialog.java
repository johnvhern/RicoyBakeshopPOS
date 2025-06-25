package com.pos.ricoybakeshop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TenderAmountDialog extends DialogFragment {

    private EditText etTendered;
    private Button btnConfirm, btnCancel;
    private double total;
    private OnTenderConfirmedListener listener;

    public interface OnTenderConfirmedListener {
        void onTenderConfirmed(double tendered);
    }

    public TenderAmountDialog(OnTenderConfirmedListener listener, double total) {
        this.listener = listener;
        this.total = total;
    }

    @Override
    public int getTheme() {
        return R.style.NoBackgroundDialogTheme;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), getTheme());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tender_amount, null);

        etTendered = view.findViewById(R.id.etTendered);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            String input = etTendered.getText().toString().trim();

            if (input.isEmpty()) {
                etTendered.setError("Please enter an amount");
                return;
            }

            try {
                double tendered = Double.parseDouble(input);

                if (tendered < total) {
                    etTendered.setError("Amount must be at least ₱" + String.format("%.2f", total));
                    return;
                }

                if (listener != null) {
                    listener.onTenderConfirmed(tendered);
                }
                dismiss();
            } catch (NumberFormatException e) {
                etTendered.setError("Invalid amount");
            }
        });

        builder.setView(view);
        return builder.create();
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
