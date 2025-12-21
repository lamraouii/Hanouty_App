package com.example.hanout_app.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.HanoutDatabase;

import java.io.File;
import java.io.IOException;

public class AddProductFragment extends Fragment {

    private EditText etProductName, etQuantity, etPrice, etBarcode;
    private ImageView ivProductImage, ivCameraIcon;
    private HanoutDatabase database;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> barcodeScannerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database
        database = HanoutDatabase.getInstance(getContext());

        // Initialize views
        etProductName = view.findViewById(R.id.etProductName);
        etQuantity = view.findViewById(R.id.etQuantity);
        etPrice = view.findViewById(R.id.etPrice);
        etBarcode = view.findViewById(R.id.etBarcode);
        ivProductImage = view.findViewById(R.id.ivProductImage);
        ivCameraIcon = view.findViewById(R.id.ivCameraIcon);

        // Initialize Activity Result Launchers
        initializeImagePickers();

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        // Scan button - Launch barcode scanner
        view.findViewById(R.id.btnScan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        // Image selection - show dialog to choose camera or gallery
        view.findViewById(R.id.btnSelectImage).setOnClickListener(v -> showImageSourceDialog());

        // Save button
        view.findViewById(R.id.btnSave).setOnClickListener(v -> saveProduct());
    }

    private void initializeImagePickers() {
        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && selectedImageUri != null) {
                        ivProductImage.setImageURI(selectedImageUri);
                        ivCameraIcon.setVisibility(View.GONE);
                    }
                });

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivProductImage.setImageURI(selectedImageUri);
                        ivCameraIcon.setVisibility(View.GONE);
                    }
                });

        // Barcode scanner launcher
        barcodeScannerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        String barcode = result.getData().getStringExtra("BARCODE");
                        if (barcode != null) {
                            etBarcode.setText(barcode);
                            Toast.makeText(getContext(), "Code-barres scanné: " + barcode, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sélectionner une photo")
                .setItems(new CharSequence[] { "Prendre une photo", "Choisir de la galerie" }, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create temporary file for the photo
            File photoFile = null;
            try {
                photoFile = File.createTempFile("product_", ".jpg", getActivity().getCacheDir());
                selectedImageUri = FileProvider.getUriForFile(getContext(),
                        "com.example.hanout_app.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                cameraLauncher.launch(intent);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Erreur lors de la création du fichier", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String barcode = etBarcode.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Le nom est requis");
            return;
        }
        if (TextUtils.isEmpty(quantityStr)) {
            etQuantity.setError("La quantité est requise");
            return;
        }
        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Le prix est requis");
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        new Thread(() -> {
            // Check if barcode exists (only if barcode is provided)
            ProductData existingProduct = null;
            if (!TextUtils.isEmpty(barcode)) {
                existingProduct = database.productDAO().checkProductByBarcode(barcode);
            }

            if (existingProduct != null) {
                // Product exists, ask to update quantity on UI thread
                ProductData finalExistingProduct = existingProduct;
            } else {
                // New product
                insertNewProduct(name, quantity, price, barcode);
            }
        }).start();
    }

    private void showUpdateQuantityDialog(ProductData existingProduct, int newQuantity) {
        if (getContext() == null)
            return;

        new AlertDialog.Builder(getContext())
                .setTitle("Produit existant")
                .setMessage("Ce produit existe déjà (" + existingProduct.getName() + "). Voulez-vous ajouter "
                        + newQuantity + " au stock actuel ?")
                .setPositiveButton("Ajouter au stock", (dialog, which) -> {
                    // Update quantity
                    new Thread(() -> {
                        existingProduct.setQuantity(existingProduct.getQuantity() + newQuantity);
                        database.productDAO().modifyProduct(existingProduct);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Stock mis à jour !", Toast.LENGTH_SHORT).show();
                                ((MainActivity) getActivity()).onBackPressed();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void insertNewProduct(String name, int quantity, double price, String barcode) {
        // Get Current User ID
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("HanoutyPrefs",
                android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast
                        .makeText(getContext(), "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show());
            }
            return;
        }

        ProductData newProduct = new ProductData(name, quantity, price, userId);
        newProduct.setBarcode(barcode);
        // Save the selected image URI if available
        if (selectedImageUri != null) {
            newProduct.setImageUrl(selectedImageUri.toString());
        } else {
            newProduct.setImageUrl("");
        }

        database.productDAO().addProduct(newProduct);

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Produit ajouté avec succès !", Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).onBackPressed();
            });
        }
    }
}
