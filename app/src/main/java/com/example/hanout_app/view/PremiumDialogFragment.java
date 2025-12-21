package com.example.hanout_app.view;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.card.MaterialCardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.hanout_app.R;

public class PremiumDialogFragment extends DialogFragment {

    private MaterialCardView cardMonthly, cardYearly;
    private AppCompatButton btnSubscribe;
    private String selectedPlan = ""; // "monthly" or "yearly"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_premium, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        cardMonthly = view.findViewById(R.id.cardMonthly);
        cardYearly = view.findViewById(R.id.cardYearly);
        btnSubscribe = view.findViewById(R.id.btnSubscribe);

        // Close button
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        // Monthly option click
        cardMonthly.setOnClickListener(v -> selectPlan("monthly"));

        // Yearly option click
        cardYearly.setOnClickListener(v -> selectPlan("yearly"));

        // Subscribe button
        btnSubscribe.setOnClickListener(v -> {
            if (!selectedPlan.isEmpty()) {
                String message = selectedPlan.equals("monthly")
                        ? "Abonnement mensuel : 4,99 €/mois"
                        : "Abonnement annuel : 49,99 €/an";
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }

    private void selectPlan(String plan) {
        selectedPlan = plan;

        // Reset both cards to default state
        resetCardStyle(cardMonthly);
        resetCardStyle(cardYearly);

        // Highlight selected card
        if (plan.equals("monthly")) {
            highlightCard(cardMonthly);
        } else {
            highlightCard(cardYearly);
        }

        // Enable subscribe button
        btnSubscribe.setEnabled(true);
    }

    private void highlightCard(MaterialCardView card) {
        if (getContext() == null)
            return;
        // Green border effect with elevation
        card.setCardElevation(8f);
        card.setStrokeWidth(4);
        card.setStrokeColor(ContextCompat.getColor(getContext(), R.color.hanouty_green));
    }

    private void resetCardStyle(MaterialCardView card) {
        // Reset to default
        card.setCardElevation(2f);
        card.setStrokeWidth(0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Remove dialog background
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Make dialog full width
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
