package com.example.hanout_app.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.ProductData;

import java.util.ArrayList;
import java.util.List;

public class ProductDataAdapter extends RecyclerView.Adapter<ProductDataAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductData> productList;
    private List<ProductData> productListFull; // For search functionality
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(ProductData product);
    }

    public ProductDataAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.productList = new ArrayList<>();
        this.productListFull = new ArrayList<>();
        this.listener = listener;
    }

    public void setProducts(List<ProductData> products) {
        this.productList = products;
        this.productListFull = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        productList.clear();
        if (query.isEmpty()) {
            productList.addAll(productListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ProductData product : productListFull) {
                if ((product.getName() != null && product.getName().toLowerCase().contains(lowerCaseQuery)) ||
                        (product.getBarcode() != null && product.getBarcode().toLowerCase().contains(lowerCaseQuery))) {
                    productList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductData product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%.2f DH", product.getPrice()));
        holder.tvStock.setText("Stock: " + product.getQuantity());
        holder.tvRef.setText("Réf: #" + product.getBarcode());

        // Load image from URI if available
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                holder.imgProduct.setImageURI(Uri.parse(product.getImageUrl()));
            } catch (Exception e) {
                holder.imgProduct.setImageResource(R.drawable.ic_box_teal);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_box_teal);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvStock, tvRef;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvProductStock);
            tvRef = itemView.findViewById(R.id.tvProductRef);
        }
    }
}
