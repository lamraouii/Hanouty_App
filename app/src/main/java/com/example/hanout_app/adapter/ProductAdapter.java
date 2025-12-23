package com.example.hanout_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%.2f DH", product.getPrice()));
        holder.tvStock.setText("Stock: " + product.getQuantity());
        // Using a default value for Ref for now as it's not in the constructor/model
        // explicitly used in the grid
        holder.tvRef.setText("Réf: #" + product.getBarcode());

        // Set Image from Drawable Resource ID
        if (product.getImageResId() != 0) {
            holder.imgProduct.setImageResource(product.getImageResId());
        } else {
            // Fallback if no image ID provided (though we plan to provide them)
            holder.imgProduct.setImageResource(R.drawable.ic_box_teal); // Placeholder
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, product.getName(), Toast.LENGTH_SHORT).show();
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
