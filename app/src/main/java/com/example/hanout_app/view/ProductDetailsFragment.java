package com.example.hanout_app.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.HanoutDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProductDetailsFragment extends Fragment {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private EditText etProductName, etQuantity, etPrice, etBarcode;
    private ImageView ivProductImage;
    private HanoutDatabase database;
    private ProductData currentProduct;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> barcodeScannerLauncher;
    private static final String ARG_PRODUCT_ID = "product_id";

    public static ProductDetailsFragment newInstance(int productId) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_details, container, false);
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

        // Initialize Activity Result Launchers
        initializeImagePickers();

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        // Bouton Supprimer (Corrigé)
        View btnDelete = view.findViewById(R.id.btnDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> deleteProduct());
        }

        // Scan button
        view.findViewById(R.id.btnScan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        // Image selection
        view.findViewById(R.id.btnSelectImage).setOnClickListener(v -> showImageSourceDialog());

        // Save button
        view.findViewById(R.id.btnSave).setOnClickListener(v -> saveChanges());

        // Load Product Data
        if (getArguments() != null) {
            int productId = getArguments().getInt(ARG_PRODUCT_ID);
            loadProduct(productId);
        }
    }

    private void loadProduct(int productId) {
        database.productDAO().getAllProducts().observe(getViewLifecycleOwner(), products -> {
            for (ProductData p : products) {
                if (p.getId_Product() == productId) {
                    currentProduct = p;
                    populateFields();
                    break;
                }
            }
        });
    }

    // ... (Le code des Launchers reste identique) ...
    private void initializeImagePickers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && selectedImageUri != null) {
                        ivProductImage.setImageURI(selectedImageUri);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivProductImage.setImageURI(selectedImageUri);
                    }
                });

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

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera(); // Si accepté, on ouvre la caméra
                    } else {
                        Toast.makeText(getContext(), "Permission caméra refusée", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sélectionner une photo")
                .setItems(new CharSequence[] { "Prendre une photo", "Choisir de la galerie" }, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndOpen(); // On appelle la nouvelle méthode de vérification
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndOpen() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                // Créer un fichier temporaire pour la capture
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

    private void populateFields() {
        if (currentProduct == null) return;
        etProductName.setText(currentProduct.getName());
        etQuantity.setText(String.valueOf(currentProduct.getQuantity()));
        etPrice.setText(String.valueOf(currentProduct.getPrice()));
        etBarcode.setText(currentProduct.getBarcode());

        if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().isEmpty()) {
            try {
                // On utilise Uri.parse pour lire le chemin du fichier sauvegardé
                Uri imageUri = Uri.parse(currentProduct.getImageUrl());
                ivProductImage.setImageURI(imageUri);
            } catch (Exception e) {
                ivProductImage.setImageResource(R.drawable.bg_icon_circle_light); // Image par défaut si erreur
            }
        }
    }

    // METHODE DE SAUVEGARDE DE L'IMAGE DANS LE STOCKAGE INTERNE
    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Créer un fichier dans le dossier privé de l'application
            File directory = getActivity().getFilesDir();
            File file = new File(directory, "img_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream out = new FileOutputStream(file);
            // Compresser l'image pour gagner de la place (Qualité 50%)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
            inputStream.close();

            // Retourner le chemin absolu en format String
            return Uri.fromFile(file).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveChanges() {
        if (currentProduct == null) return;

        String name = etProductName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String barcode = etBarcode.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { etProductName.setError("Le nom est requis"); return; }
        if (TextUtils.isEmpty(quantityStr)) { etQuantity.setError("La quantité est requise"); return; }
        if (TextUtils.isEmpty(priceStr)) { etPrice.setError("Le prix est requis"); return; }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        // Sauvegarde de l'image DANS UN THREAD pour ne pas bloquer l'UI
        new Thread(() -> {
            // Mettre à jour les champs de base
            currentProduct.setName(name);
            currentProduct.setQuantity(quantity);
            currentProduct.setPrice(price);
            currentProduct.setBarcode(barcode);

            // GESTION DE L'IMAGE
            if (selectedImageUri != null) {
                String savedPath = saveImageToInternalStorage(selectedImageUri);
                if (savedPath != null) {
                    currentProduct.setImageUrl(savedPath);
                }
            }

            // Mise à jour BDD
            database.productDAO().modifyProduct(currentProduct);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Produit mis à jour !", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).onBackPressed();
                });
            }
        }).start();
    }

    private void deleteProduct() {
        if (currentProduct == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Supprimer le produit")
                .setMessage("Voulez-vous vraiment supprimer " + currentProduct.getName() + " ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    new Thread(() -> {
                        database.productDAO().deleteProduct(currentProduct);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Produit supprimé", Toast.LENGTH_SHORT).show();
                                ((MainActivity) getActivity()).onBackPressed();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}