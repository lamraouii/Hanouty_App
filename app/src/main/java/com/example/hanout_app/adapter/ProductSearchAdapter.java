package com.example.hanout_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.ProductData;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.SearchViewHolder> {

    private List<ProductData> searchResults = new ArrayList<>();
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(ProductData product);
    }

    public ProductSearchAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setResults(List<ProductData> results) {
        this.searchResults = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        ProductData product = searchResults.get(position);
        holder.text1.setText(product.getName());
        holder.text2.setText(String.format("%.2f DH", product.getPrice()));

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
