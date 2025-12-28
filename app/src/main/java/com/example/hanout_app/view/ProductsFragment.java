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
import com.example.hanout_app.database.HanoutDatabase;

// IMPORT DU PREFERENCE MANAGER
import com.example.hanout_app.view.PreferenceManager;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductDataAdapter adapter;
    private HanoutDatabase database;
    private EditText etSearch;

    // Déclaration du manager
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products, container, false);

        // Initialisation de la BDD et du PreferenceManager
        database = HanoutDatabase.getInstance(getContext());
        preferenceManager = new PreferenceManager(getContext());

        // Initialisation RecyclerView
        recyclerView = rootView.findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductDataAdapter(getContext(), product -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProductDetails(product.getId_Product());
            }
        });
        recyclerView.setAdapter(adapter);

        // Initialisation Recherche
        etSearch = rootView.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Chargement des produits
        loadProducts();

        // Boutons et Navigation
        rootView.findViewById(R.id.btnAddProduct).setOnClickListener(v -> {
            if (getActivity() != null) ((MainActivity) getActivity()).navigateToAddProduct();
        });

        rootView.findViewById(R.id.navHome).setOnClickListener(v -> {
            if (getActivity() != null) ((MainActivity) getActivity()).navigateToHome();
        });

        rootView.findViewById(R.id.navVente).setOnClickListener(v -> {
            if (getActivity() != null) ((MainActivity) getActivity()).navigateToSales();
        });

        rootView.findViewById(R.id.navProduits).setOnClickListener(v -> {
            // Déjà sur la page produits
        });

        rootView.findViewById(R.id.navProfile).setOnClickListener(v -> {
            if (getActivity() != null) ((MainActivity) getActivity()).navigateToProfile();
        });

        return rootView;
    }

    private void loadProducts() {
        // UTILISATION DU PREFERENCE MANAGER
        // On vérifie d'abord si l'utilisateur est connecté
        if (!preferenceManager.isLoggedIn()) {
            Toast.makeText(getContext(), "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Récupération de l'ID (String -> int)
        String userIdStr = preferenceManager.getUserId();
        int userId = -1;
        try {
            if (!userIdStr.isEmpty()) {
                userId = Integer.parseInt(userIdStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Erreur ID utilisateur", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId != -1) {
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
                        Toast.makeText(getContext(), "Aucun article en stock faible !", Toast.LENGTH_SHORT).show();
                        adapter.setProducts(new java.util.ArrayList<>());
                    }
                });
            } else {
                database.productDAO().getProductsByUserId(userId).observe(getViewLifecycleOwner(), products -> {
                    if (products != null && !products.isEmpty()) {
                        adapter.setProducts(products);
                    } else {
                        Toast.makeText(getContext(), "Aucun produit trouvé. Ajoutez-en un !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}