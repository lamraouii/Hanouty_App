package com.example.hanout_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.FactureData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<FactureData> invoiceList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    private final OnInvoiceClickListener listener;

    public interface OnInvoiceClickListener {
        void onInvoiceClick(FactureData invoice);
    }

    public HistoryAdapter(OnInvoiceClickListener listener) {
        this.listener = listener;
    }

    public void setInvoices(List<FactureData> invoices) {
        this.invoiceList = invoices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_invoice, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        FactureData invoice = invoiceList.get(position);
        holder.bind(invoice);
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceId, tvDate, tvAmount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceId = itemView.findViewById(R.id.tvInvoiceId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        void bind(FactureData invoice) {
            tvInvoiceId.setText("Facture #" + invoice.getId_facture());
            if (invoice.getDate_facture() != null) {
                tvDate.setText(dateFormat.format(invoice.getDate_facture()));
            }
            tvAmount.setText(String.format(Locale.getDefault(), "%.2f DH", invoice.getMontant_facture()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInvoiceClick(invoice);
                }
            });
        }
    }
}
