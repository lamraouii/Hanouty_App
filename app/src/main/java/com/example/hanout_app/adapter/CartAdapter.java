package com.example.hanout_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.ProductData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    // Helper class to store product + quantity
    public static class CartItem {
        public ProductData product;
        public int quantity;

        public CartItem(ProductData product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return product.getPrice() * quantity;
        }
    }

    private List<CartItem> cartItems;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated(double totalAmount);
    }

    public CartAdapter(CartUpdateListener listener) {
        this.cartItems = new ArrayList<>();
        this.listener = listener;
    }

    public void addProduct(ProductData product) {
        // Check if exists
        for (CartItem item : cartItems) {
            if (item.product.getId_Product() == product.getId_Product()) {
                item.quantity++;
                notifyDataSetChanged();
                notifyTotalUpdate();
                return;
            }
        }
        // New item
        cartItems.add(new CartItem(product, 1));
        notifyDataSetChanged();
        notifyTotalUpdate();
    }

    private void notifyTotalUpdate() {
        if (listener != null) {
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getTotalPrice();
            }
            listener.onCartUpdated(total);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvName.setText(item.product.getName());
        holder.tvPrice.setText(String.format("%.2f DH", item.product.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.quantity));
        holder.tvTotal.setText(String.format("%.2f DH", item.getTotalPrice()));

        holder.btnPlus.setOnClickListener(v -> {
            item.quantity++;
            notifyItemChanged(position);
            notifyTotalUpdate();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                notifyItemChanged(position);
                notifyTotalUpdate();
            } else {
                // Remove item?
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                notifyTotalUpdate();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getItems() {
        return cartItems;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvTotal;
        ImageButton btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvTotal = itemView.findViewById(R.id.tvCartItemTotal);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
