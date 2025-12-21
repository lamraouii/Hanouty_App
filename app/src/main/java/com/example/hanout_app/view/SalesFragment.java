package com.example.hanout_app.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanout_app.R;
import com.example.hanout_app.adapter.CartAdapter;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;
import com.example.hanout_app.utils.PdfGenerator;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SalesFragment extends Fragment {

    private RecyclerView rvCart;
    private RecyclerView rvSearchResults; // Simple list for search results
    private CartAdapter cartAdapter;
    private TextView tvTotalAmount;
    private TextInputEditText etSearchProduct;
    private View layoutEmptyCart;

    // Database
    private HanoutDatabase database;
    private UserData currentUser;

    private ActivityResultLauncher<Intent> barcodeLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sales, container, false);
    }

    private com.example.hanout_app.adapter.ProductSearchAdapter searchAdapter;
    private List<ProductData> allUserProducts = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init Database & User
        database = HanoutDatabase.getInstance(getContext());
        loadCurrentUser();

        // Init Views
        rvCart = view.findViewById(R.id.rvCart);
        rvSearchResults = view.findViewById(R.id.rvSearchResults); // Ensure this ID exists in XML
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        etSearchProduct = view.findViewById(R.id.etSearchProduct);
        layoutEmptyCart = view.findViewById(R.id.layoutEmptyCart);

        // Cart Adapter
        cartAdapter = new CartAdapter(totalPrice -> {
            tvTotalAmount.setText(String.format("%.2f DH", totalPrice));
            updateEmptyState();
        });

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(cartAdapter);
        updateEmptyState();

        // Search Adapter
        searchAdapter = new com.example.hanout_app.adapter.ProductSearchAdapter(product -> {
            cartAdapter.addProduct(product);
            etSearchProduct.setText(""); // Clear search
            rvSearchResults.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Ajouté: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(searchAdapter);

        // Barcode Launcher
        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String barcode = result.getData().getStringExtra("BARCODE");
                        if (barcode != null) {
                            addProductByBarcode(barcode);
                        }
                    }
                });

        // Buttons
        view.findViewById(R.id.btnScanBarcode).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            barcodeLauncher.launch(intent);
        });

        view.findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (cartAdapter.getItemCount() > 0) {
                checkPermissionAndGeneratePdf();
            } else {
                Toast.makeText(getContext(), "Le panier est vide", Toast.LENGTH_SHORT).show();
            }
        });

        // Header Back Arrow
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        // Bottom Navigation
        view.findViewById(R.id.navHome).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        view.findViewById(R.id.navVente).setOnClickListener(v -> {
            // Already here
        });

        view.findViewById(R.id.navProduits).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProducts();
            }
        });

        view.findViewById(R.id.navProfile).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProfile();
            }
        });

        // Search Logic
        setupSearchLogic();
    }

    private void loadCurrentUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("HanoutyPrefs", Activity.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId != -1) {
            database.userDAO().getUserById(userId).observe(getViewLifecycleOwner(), user -> {
                currentUser = user;
                loadUserProducts(userId);
            });
        }
    }

    private void loadUserProducts(int userId) {
        database.productDAO().getProductsByUserId(userId).observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                allUserProducts = products;
            }
        });
    }

    private void setupSearchLogic() {
        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Hide results when focus lost or cleared
        etSearchProduct.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Delayed hide to allow click
                rvSearchResults.postDelayed(() -> rvSearchResults.setVisibility(View.GONE), 200);
            }
        });
    }

    private void filterProducts(String query) {
        if (query.isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            return;
        }

        List<ProductData> filteredList = new ArrayList<>();
        for (ProductData p : allUserProducts) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(p);
            }
        }

        if (!filteredList.isEmpty()) {
            searchAdapter.setResults(filteredList);
            rvSearchResults.setVisibility(View.VISIBLE);
        } else {
            rvSearchResults.setVisibility(View.GONE);
        }
    }
    // The old searchProductByName method is removed as its functionality is
    // replaced by filterProducts.

    private void addProductByBarcode(String barcode) {
        // Run on background thread
        new Thread(() -> {
            ProductData product = database.productDAO().checkProductByBarcode(barcode);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (product != null) {
                        cartAdapter.addProduct(product);
                        Toast.makeText(getContext(), "Produit ajouté: " + product.getName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Produit introuvable", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void updateEmptyState() {
        if (cartAdapter.getItemCount() == 0) {
            layoutEmptyCart.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
        } else {
            layoutEmptyCart.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
        }
    }

    private void checkPermissionAndGeneratePdf() {
        // Android 10+ (Scoped Storage) doesn't need WRITE_EXTERNAL_STORAGE for
        // Downloads
        // But for compatibility or if using external public dir:
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 101);
                return;
            }
        }
        processCheckout();
    }

    private void processCheckout() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Utilisateur non chargé", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Calculate Total
        double total = 0;
        final List<CartAdapter.CartItem> items = new ArrayList<>(cartAdapter.getItems());
        for (CartAdapter.CartItem item : items) {
            total += item.getTotalPrice();
        }
        final double finalTotal = total;
        Toast.makeText(getContext(), "Validation en cours...", Toast.LENGTH_SHORT).show();

        // 2. Database Transaction (Background)
        HanoutDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // A. Insert Invoice
                com.example.hanout_app.database.Data.FactureData facture = new com.example.hanout_app.database.Data.FactureData();
                facture.setUserId(currentUser.getId_User());
                facture.setMontant_facture(finalTotal);
                facture.setDate_facture(new java.util.Date());

                long factureId = database.factureDAO().addFacture(facture);

                // B. Process Items (Insert Sale & Update Stock)
                for (CartAdapter.CartItem item : items) {
                    // Create Sale Link
                    com.example.hanout_app.database.Data.SaleData sale = new com.example.hanout_app.database.Data.SaleData();
                    sale.setFacture_id((int) factureId);
                    sale.setProduct_id(item.product.getId_Product());
                    sale.setQuantity_sold(item.quantity);
                    sale.setTotal_price(item.getTotalPrice());
                    sale.setDate_sale(new java.util.Date());

                    database.saleDAO().addSale(sale);

                    // Update Product Stock
                    ProductData product = item.product;
                    int newQuantity = product.getQuantity() - item.quantity;
                    // Prevent negative stock ? For now allow it or clamp to 0.
                    // Let's allow negative for simplicity (audit trail) or clamp if requested.
                    // User said "diminuer la quantité", didn't specify strict checks.
                    product.setQuantity(newQuantity);
                    database.productDAO().modifyProduct(product);
                }

                // C. Success - Generate PDF on Main Thread (or here?)
                // PDF generation involves IO, safe to do here but Toast needs Main.

                File pdfFile = new PdfGenerator(getContext()).generateInvoice(currentUser, items, finalTotal);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Clear Cart
                        cartAdapter.getItems().clear();
                        cartAdapter.notifyDataSetChanged();
                        updateEmptyState();
                        tvTotalAmount.setText("0.00 DH");

                        Toast.makeText(getContext(), "Vente validée et stock mis à jour !", Toast.LENGTH_LONG).show();
                        if (pdfFile != null) {
                            openPdf(pdfFile);
                        }
                    });
                }

            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast
                            .makeText(getContext(), "Erreur lors de la vente: " + e.getMessage(), Toast.LENGTH_LONG)
                            .show());
                }
                e.printStackTrace();
            }
        });
    }

    private void generatePdf() {
        // Deprecated/Refactored into processCheckout
    }

    private void openPdf(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(getContext(), "com.example.hanout_app.fileprovider", file);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Aucune application pour ouvrir le PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
