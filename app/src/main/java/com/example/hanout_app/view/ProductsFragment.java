package com.example.hanout_app.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.adapter.ProductDataAdapter;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.HanoutDatabase;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductDataAdapter adapter;
    private HanoutDatabase database;
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products, container, false);

        // Initialize database
        database = HanoutDatabase.getInstance(getContext());

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize adapter with click listener
        adapter = new ProductDataAdapter(getContext(), product -> {
            // Navigate to product details when clicked
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProductDetails(product.getId_Product());
            }
        });
        recyclerView.setAdapter(adapter);

        // Initialize search
        etSearch = rootView.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Load products from database
        loadProducts();

        // Add Product Button
        rootView.findViewById(R.id.btnAddProduct).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToAddProduct();
            }
        });

        // Bottom Navigation
        rootView.findViewById(R.id.navHome).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        rootView.findViewById(R.id.navVente).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToSales();
            }
        });

        rootView.findViewById(R.id.navProduits).setOnClickListener(v -> {
            // Already on products page
        });

        rootView.findViewById(R.id.navProfile).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProfile();
            }
        });

        return rootView;
    }

    private void loadProducts() {
        // Get User ID
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("HanoutyPrefs",
                android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            // Check for Low Stock Argument
            Bundle args = getArguments();
            boolean showLowStockOnly = false;
            if (args != null) {
                showLowStockOnly = args.getBoolean("SHOW_LOW_STOCK", false);
            }

            if (showLowStockOnly) {
                database.productDAO().getLowStockProducts(5, userId).observe(getViewLifecycleOwner(), products -> {
                    if (products != null && !products.isEmpty()) {
                        adapter.setProducts(products);
                        Toast.makeText(getContext(), "Affichage du stock faible (< 5)", Toast.LENGTH_SHORT).show();
                    } else {
                        // Empty state
                        Toast.makeText(getContext(), "Aucun article en stock faible !", Toast.LENGTH_SHORT).show();
                        // Clear adapter or show helpful message
                        adapter.setProducts(new java.util.ArrayList<>());
                    }
                });
            } else {
                // Default: Observe all products for this user
                database.productDAO().getProductsByUserId(userId).observe(getViewLifecycleOwner(), products -> {
                    if (products != null && !products.isEmpty()) {
                        adapter.setProducts(products);
                    } else {
                        // Empty state
                        Toast.makeText(getContext(), "Aucun produit trouvé. Ajoutez-en un !", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        }
    }
}
