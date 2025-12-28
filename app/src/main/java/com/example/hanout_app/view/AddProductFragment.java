package com.example.hanout_app.view;

import android.app.AlertDialog;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.ProductData;
import com.example.hanout_app.database.HanoutDatabase;
// IMPORT PREFERENCE MANAGER
import com.example.hanout_app.view.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddProductFragment extends Fragment {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private EditText etProductName, etQuantity, etPrice, etBarcode;
    private ImageView ivProductImage, ivCameraIcon;
    private HanoutDatabase database;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> barcodeScannerLauncher;

    // Déclaration Manager
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database & PreferenceManager
        database = HanoutDatabase.getInstance(getContext());
        preferenceManager = new PreferenceManager(getContext());

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

        // Scan button
        view.findViewById(R.id.btnScan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            barcodeScannerLauncher.launch(intent);
        });

        // Image selection
        view.findViewById(R.id.btnSelectImage).setOnClickListener(v -> showImageSourceDialog());

        // Save button
        view.findViewById(R.id.btnSave).setOnClickListener(v -> saveProduct());
    }

    private void initializeImagePickers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && selectedImageUri != null) {
                        ivProductImage.setImageURI(selectedImageUri);
                        ivCameraIcon.setVisibility(View.GONE);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivProductImage.setImageURI(selectedImageUri);
                        ivCameraIcon.setVisibility(View.GONE);
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

        if (TextUtils.isEmpty(name)) { etProductName.setError("Le nom est requis"); return; }
        if (TextUtils.isEmpty(quantityStr)) { etQuantity.setError("La quantité est requise"); return; }
        if (TextUtils.isEmpty(priceStr)) { etPrice.setError("Le prix est requis"); return; }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        new Thread(() -> {
            ProductData existingProduct = null;
            if (!TextUtils.isEmpty(barcode)) {
                existingProduct = database.productDAO().checkProductByBarcode(barcode);
            }

            if (existingProduct != null) {
                final ProductData finalExistingProduct = existingProduct;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> showUpdateQuantityDialog(finalExistingProduct, quantity));
                }
            } else {
                insertNewProduct(name, quantity, price, barcode);
            }
        }).start();
    }

    private void showUpdateQuantityDialog(ProductData existingProduct, int newQuantity) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Produit existant")
                .setMessage("Ce produit existe déjà (" + existingProduct.getName() + "). Voulez-vous ajouter "
                        + newQuantity + " au stock actuel ?")
                .setPositiveButton("Ajouter au stock", (dialog, which) -> {
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

    private void insertNewProduct(String name, int quantity, double price, String barcode) {
        // 1. Vérification connexion
        if (!preferenceManager.isLoggedIn()) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show());
            }
            return;
        }

        // 2. Récupération ID
        String userIdStr = preferenceManager.getUserId();
        int userId = -1;
        try {
            if (!userIdStr.isEmpty()) {
                userId = Integer.parseInt(userIdStr);
            }
        } catch (NumberFormatException e) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur ID Utilisateur", Toast.LENGTH_SHORT).show());
            }
            return;
        }

        if (userId == -1) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur ID invalide", Toast.LENGTH_SHORT).show());
            }
            return;
        }

        // --- CORRECTION DE L'ERREUR ---
        // On crée une variable finale pour l'utiliser dans le Thread
        final int finalUserId = userId;

        new Thread(() -> {
            // Utilisation de finalUserId ici (et non userId)
            ProductData newProduct = new ProductData(name, quantity, price, finalUserId);
            newProduct.setBarcode(barcode);

            // GESTION IMAGE PERSISTANTE (Correction image)
            if (selectedImageUri != null) {
                // On utilise la fonction de sauvegarde qu'on a ajoutée plus tôt
                String savedPath = saveImageToInternalStorage(selectedImageUri);
                if (savedPath != null) {
                    newProduct.setImageUrl(savedPath);
                } else {
                    newProduct.setImageUrl("");
                }
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
        }).start();
    }
}