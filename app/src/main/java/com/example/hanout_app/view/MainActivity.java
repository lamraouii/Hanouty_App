package com.example.hanout_app.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.hanout_app.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activer le mode plein écran edge-to-edge
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        setContentView(R.layout.activity_main);

        // Configurer les couleurs des barres système
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Barre de statut en haut - marron
            getWindow().setStatusBarColor(getResources().getColor(R.color.maron, null));

            // Barre de navigation en bas - blanche
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white, null));
        }

        // Icônes sombres pour la barre de navigation blanche
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(
                    android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        // Démarrer avec le premier écran d'onboarding ou Home si connecté
        if (savedInstanceState == null) {
            android.content.SharedPreferences prefs = getSharedPreferences("HanoutyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                loadFragment(new HomeFragment());
            } else {
                loadFragment(new OnboardingStep1Fragment());
            }
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    public void navigateToSignIn() {
        loadFragment(new SignInFragment());
    }

    public void navigateToSignUp() {
        loadFragment(new SignUpFragment());
    }

    public void navigateToHome() {
        loadFragment(new HomeFragment());
    }

    public void navigateToProducts() {
        loadFragment(new ProductsFragment());
    }

    public void navigateToProfile() {
        loadFragment(new ProfileFragment());
    }

    public void navigateToEditProfile() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new EditProfileFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Méthode pour changer la couleur de la barre d'état dynamiquement
    public void setStatusBarColor(int colorResId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(colorResId, null));
        }
    }

    public void navigateToAddProduct() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new AddProductFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void navigateToProductDetails(int productId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, ProductDetailsFragment.newInstance(productId));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void navigateToSales() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new com.example.hanout_app.view.SalesFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
