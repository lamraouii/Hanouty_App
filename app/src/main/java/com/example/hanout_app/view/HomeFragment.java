package com.example.hanout_app.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanout_app.R;

public class HomeFragment extends Fragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Changer la couleur de la barre d'état en terracotta/marron
        ((MainActivity) getActivity()).setStatusBarColor(R.color.maron);

        initializeViews();
        setupClickListeners();

        return rootView;
    }

    private void initializeViews() {
        // Fetch User Name
        com.example.hanout_app.database.HanoutDatabase database = com.example.hanout_app.database.HanoutDatabase
                .getInstance(getContext());

        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("HanoutyPrefs",
                android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            database.userDAO().getUserById(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    android.widget.TextView tvGreeting = rootView.findViewById(R.id.tvGreeting);
                    tvGreeting.setText("Salam, " + user.getName() + " 👋");
                }
            });

            // Fetch Real Dashboard Metrics
            long todayStart = getStartOfDay();
            long todayEnd = getEndOfDay();

            // 1. Daily Sales
            // 1. Daily Sales
            database.factureDAO().getDailySalesSum(todayStart, todayEnd, userId).observe(getViewLifecycleOwner(),
                    sum -> {
                        android.widget.TextView tvSales = rootView.findViewById(R.id.tvSalesValue);
                        if (sum != null) {
                            tvSales.setText(String.format("%.2f DH", sum));
                        } else {
                            tvSales.setText("0.00 DH");
                        }
                    });

            // 2. Low Stock (Threshold < 5)
            database.productDAO().getLowStockCount(5, userId).observe(getViewLifecycleOwner(), count -> {
                android.widget.TextView tvStock = rootView.findViewById(R.id.tvStockValue);
                if (count != null) {
                    tvStock.setText(count + " Articles");
                } else {
                    tvStock.setText("0 Articles");
                }
            });

            // 3. Stock Value
            database.productDAO().getTotalStockValue(userId).observe(getViewLifecycleOwner(), value -> {
                android.widget.TextView tvStockVal = rootView.findViewById(R.id.tvStockValueText);
                if (value != null) {
                    tvStockVal.setText(String.format("%.2f DH", value));
                } else {
                    tvStockVal.setText("0.00 DH");
                }
            });

            // 4. Total Products
            database.productDAO().getProductCount(userId).observe(getViewLifecycleOwner(), count -> {
                android.widget.TextView tvTotalProd = rootView.findViewById(R.id.tvTotalProductsValue);
                if (count != null) {
                    tvTotalProd.setText(String.valueOf(count));
                } else {
                    tvTotalProd.setText("0");
                }
            });

            // 5. Last Sales (Recent 3)
            android.widget.LinearLayout llRecentSales = rootView.findViewById(R.id.llRecentSales);
            database.factureDAO().getLastNFacturesByUserId(userId, 3).observe(getViewLifecycleOwner(), sales -> {
                if (llRecentSales != null) {
                    llRecentSales.removeAllViews();
                    if (sales != null && !sales.isEmpty()) {
                        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd MMM, HH:mm",
                                java.util.Locale.getDefault());
                        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(getContext());

                        for (com.example.hanout_app.database.Data.FactureData sale : sales) {
                            android.view.View view = inflater.inflate(R.layout.item_dashboard_sale, llRecentSales,
                                    false);

                            android.widget.TextView tvTitle = view.findViewById(R.id.tvSaleTitle);
                            android.widget.TextView tvTime = view.findViewById(R.id.tvSaleTime);
                            android.widget.TextView tvAmount = view.findViewById(R.id.tvSaleAmount);

                            tvTitle.setText("Facture #" + sale.getId_facture());
                            if (sale.getDate_facture() != null) {
                                tvTime.setText(dateFormat.format(sale.getDate_facture()));
                            }
                            tvAmount.setText(String.format("%.2f DH", sale.getMontant_facture()));

                            llRecentSales.addView(view);
                        }
                    } else {
                        // Optional: Add a "No sales yet" view
                    }
                }
            });
        }
    }

    // Helper for timestamps
    private long getStartOfDay() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getEndOfDay() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.set(java.util.Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    private void setupClickListeners() {
        // ... Click listeners ...
        // Keeping existing click listeners but metrics logic is above.
        // Wait, I cannot insert efficiently without knowing IDs.
        // I will do a quick check of fragment_home.xml for IDs inside cards.

        // Stock Value (previously Credit) - No action for now
        rootView.findViewById(R.id.cardStockValue).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Valeur totale de votre stock", Toast.LENGTH_SHORT).show();
        });

        rootView.findViewById(R.id.cardStock).setOnClickListener(v -> {
            if (getActivity() != null) {
                ProductsFragment fragment = new ProductsFragment();
                Bundle args = new Bundle();
                args.putBoolean("SHOW_LOW_STOCK", true);
                fragment.setArguments(args);
                ((MainActivity) getActivity()).loadFragment(fragment);
            }
        });

        // Total Products (previously Profit) - Maybe go to Products?
        rootView.findViewById(R.id.cardTotalProducts).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProducts();
            }
        });

        // Bottom navigation
        rootView.findViewById(R.id.navHome).setOnClickListener(v -> {
            // Already on home, do nothing or refresh
            Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
        });

        rootView.findViewById(R.id.navVente).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToSales();
            }
        });

        rootView.findViewById(R.id.navProduits).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProducts();
            }
        });

        rootView.findViewById(R.id.navProfile).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProfile();
            }
        });
    }
}
