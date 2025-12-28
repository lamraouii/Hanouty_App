package com.example.hanout_app.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.FactureData;
import com.example.hanout_app.database.HanoutDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private View rootView;
    private PreferenceManager preferenceManager;
    private HanoutDatabase database;

    // UI Components
    private TextView tvGreeting;
    private TextView tvCurrentDate; // Nouveau TextView pour la date
    private TextView tvSalesValue, tvStockValueText, tvStockValue, tvTotalProductsValue;
    private LinearLayout llRecentSales;

    // Format pour l'historique (ex: 20 Dec 2025, 14:30)
    private final SimpleDateFormat historyDateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    // Format pour l'en-tête (ex: Lundi, 20 Décembre 2025)
    private final SimpleDateFormat headerDateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.FRENCH);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Initialisation
        preferenceManager = new PreferenceManager(getContext());
        database = HanoutDatabase.getInstance(getContext());

        // 2. Setup UI
        initializeViews();
        setupClickListeners();

        // 3. Afficher la date du jour
        updateHeaderDate();

        // 4. Chargement des données
        if (preferenceManager.isLoggedIn()) {
            loadAuthenticatedData();
        } else {
            tvGreeting.setText("Bonjour, Invité");
            resetDashboardValues();
        }

        return rootView;
    }

    private void initializeViews() {
        tvGreeting = rootView.findViewById(R.id.tvGreeting);

        // Initialisation de la date
        tvCurrentDate = rootView.findViewById(R.id.tvCurrentDate);

        // Dashboard Cards
        tvSalesValue = rootView.findViewById(R.id.tvSalesValue);
        tvStockValueText = rootView.findViewById(R.id.tvStockValueText);
        tvStockValue = rootView.findViewById(R.id.tvStockValue);
        tvTotalProductsValue = rootView.findViewById(R.id.tvTotalProductsValue);

        // List Container
        llRecentSales = rootView.findViewById(R.id.llRecentSales);
    }

    private void updateHeaderDate() {
        if (tvCurrentDate != null) {
            String dateStr = headerDateFormat.format(new Date());
            // Mettre la première lettre en majuscule (lundi -> Lundi)
            String capitalizedDate = dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1);
            tvCurrentDate.setText(capitalizedDate);
        }
    }

    private void resetDashboardValues() {
        tvSalesValue.setText("0.00 DH");
        tvStockValueText.setText("0.00 DH");
        tvStockValue.setText("0 Articles");
        tvTotalProductsValue.setText("0");
        llRecentSales.removeAllViews();
    }

    private void loadAuthenticatedData() {
        int userId = getUserId();
        if (userId == -1) return;

        // A. Infos Utilisateur
        database.userDAO().getUserById(userId).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvGreeting.setText("Salam, " + user.getName() + " \uD83D\uDC4B");
            }
        });

        // B. Total Produits
        database.productDAO().getProductCount(userId).observe(getViewLifecycleOwner(), count -> {
            tvTotalProductsValue.setText(String.valueOf(count != null ? count : 0));
        });

        // C. Stock Faible
        database.productDAO().getLowStockCount(5, userId).observe(getViewLifecycleOwner(), count -> {
            int lowStock = count != null ? count : 0;
            tvStockValue.setText(lowStock + " Articles");
            if (lowStock > 0) {
                tvStockValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvStockValue.setTextColor(getResources().getColor(R.color.text_value));
            }
        });

        // D. Valeur Stock
        database.productDAO().getTotalStockValue(userId).observe(getViewLifecycleOwner(), value -> {
            double total = value != null ? value : 0.0;
            tvStockValueText.setText(String.format("%.2f DH", total));
        });

        // E. Ventes du Jour
        loadTodaySales(userId);

        // F. Dernières Ventes
        loadRecentInvoices(userId);
    }

    private void loadTodaySales(int userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endOfDay = calendar.getTimeInMillis();

        database.factureDAO().getDailySalesSum(startOfDay, endOfDay, userId).observe(getViewLifecycleOwner(), sum -> {
            double totalToday = sum != null ? sum : 0.0;
            tvSalesValue.setText(String.format("%.2f DH", totalToday));
        });
    }

    private void loadRecentInvoices(int userId) {
        database.factureDAO().getLastNFacturesByUserId(userId, 10).observe(getViewLifecycleOwner(), factures -> {
            llRecentSales.removeAllViews();

            if (factures != null && !factures.isEmpty()) {
                for (FactureData facture : factures) {
                    addInvoiceRow(facture);
                }
            } else {
                showEmptyStateMessage();
            }
        });
    }

    private void addInvoiceRow(FactureData facture) {
        if (getContext() == null) return;

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_history_invoice, llRecentSales, false);

        TextView tvInvoiceId = itemView.findViewById(R.id.tvInvoiceId);
        TextView tvDate = itemView.findViewById(R.id.tvDate);
        TextView tvAmount = itemView.findViewById(R.id.tvAmount);

        tvInvoiceId.setText("Facture " + facture.getId_facture());

        if (facture.getDate_facture() != null) {
            tvDate.setText(historyDateFormat.format(facture.getDate_facture()));
        } else {
            tvDate.setText("--");
        }

        tvAmount.setText(String.format("%.2f DH", facture.getMontant_facture()));
        itemView.setOnClickListener(v -> {
            openInvoicePdf(facture);
        });
        llRecentSales.addView(itemView);
    }
    private void openInvoicePdf(FactureData facture) {
        try {
            // 1. Construire le nom du fichier
            // IMPORTANT : Assurez-vous que c'est le même nom que lors de la génération (Facture_ID.pdf ou Invoice_ID.pdf)
            String fileName = "Facture_" + facture.getId_facture() + ".pdf";

            // 2. Chercher le fichier dans le dossier de l'application
            java.io.File file = new java.io.File(getContext().getExternalFilesDir(null), fileName);

            // 3. Vérifier si le fichier existe
            if (file.exists()) {
                // 4. Créer l'Intent pour ouvrir le PDF
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);

                // On utilise FileProvider pour la sécurité (comme dans SalesFragment)
                android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(
                        getContext(),
                        "com.example.hanout_app.fileprovider", // Doit correspondre à votre AndroidManifest
                        file
                );

                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Le PDF de cette facture est introuvable.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Erreur lors de l'ouverture : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showEmptyStateMessage() {
        if (getContext() == null) return;
        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.item_empty_sales, llRecentSales, false);
        llRecentSales.addView(emptyView);
    }

    private int getUserId() {
        String userIdStr = preferenceManager.getUserId();
        try {
            return Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void setupClickListeners() {
        setClickListener(R.id.cardSales, () -> navigateToSales());
        setClickListener(R.id.cardStock, () -> navigateToLowStock());
        setClickListener(R.id.cardTotalProducts, () -> navigateToProducts());
        setClickListener(R.id.cardStockValue, () -> navigateToProducts());

        // Bottom Navigation
        setClickListener(R.id.navHome, () -> showToast("Vous y êtes déjà"));
        setClickListener(R.id.navVente, () -> navigateToSales());
        setClickListener(R.id.navProduits, () -> navigateToProducts());
        setClickListener(R.id.navProfile, () -> navigateToProfile());
    }

    private void setClickListener(int viewId, Runnable action) {
        View view = rootView.findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> {
                if (getActivity() != null) action.run();
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToSales() {
        ((MainActivity) getActivity()).navigateToSales();
    }

    private void navigateToProducts() {
        ((MainActivity) getActivity()).navigateToProducts();
    }

    private void navigateToProfile() {
        ((MainActivity) getActivity()).navigateToProfile();
    }

    private void navigateToLowStock() {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putBoolean("SHOW_LOW_STOCK", true);
        fragment.setArguments(args);
        ((MainActivity) getActivity()).loadFragment(fragment, true);
    }
}