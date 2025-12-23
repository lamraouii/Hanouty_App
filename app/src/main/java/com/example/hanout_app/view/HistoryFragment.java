package com.example.hanout_app.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.adapter.HistoryAdapter;
import com.example.hanout_app.database.HanoutDatabase;

import java.util.Collections;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private HanoutDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = HanoutDatabase.getInstance(getContext());
        rvHistory = view.findViewById(R.id.rvHistory);

        setupRecyclerView();
        loadHistory();

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter(invoice -> {
            // Optional: Show details of invoice (Items)
            // For now just Toast
            Toast.makeText(getContext(), "Facture #" + invoice.getId_facture(), Toast.LENGTH_SHORT).show();
        });
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(historyAdapter);
    }

    private void loadHistory() {
        SharedPreferences prefs = getActivity().getSharedPreferences("HanoutyPrefs", Activity.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            database.factureDAO().getAllFactureByUserId(userId).observe(getViewLifecycleOwner(), invoices -> {
                if (invoices != null) {
                    // Reverse list to show newest first
                    Collections.reverse(invoices);
                    historyAdapter.setInvoices(invoices);
                }
            });
        }
    }
}
