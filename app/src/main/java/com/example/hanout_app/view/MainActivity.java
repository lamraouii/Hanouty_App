package com.example.hanout_app.view;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.hanout_app.R;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setContentView(R.layout.activity_main);


        preferenceManager = new PreferenceManager(this);

        if (savedInstanceState == null) {
            loadFragment(new SplashFragment(), false);
        }

    }


    /* ================= NAVIGATION ================= */

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void navigateToOnboarding() {
        loadFragment(new OnboardingStep1Fragment(), false);
    }

    public void navigateToSignIn() {
        loadFragment(new SignInFragment(), false);
    }

    public void navigateToSignUp() {
        loadFragment(new SignUpFragment(), false);
    }

    public void navigateToHome() {
        loadFragment(new HomeFragment(), false);
    }

    public void navigateToProducts() {
        loadFragment(new ProductsFragment(), true);
    }

    public void navigateToProfile() {
        loadFragment(new ProfileFragment(), true);
    }

    public void navigateToEditProfile() {
        loadFragment(new EditProfileFragment(), true);
    }

    public void navigateToAddProduct() {
        loadFragment(new AddProductFragment(), true);
    }

    public void navigateToProductDetails(int productId) {
        loadFragment(ProductDetailsFragment.newInstance(productId), true);
    }

    public void navigateToSales() {
        loadFragment(new SalesFragment(), true);
    }

    /* ================= GETTERS ================= */

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
}
