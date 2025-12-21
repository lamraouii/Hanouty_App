package com.example.hanout_app.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;

public class ProfileFragment extends Fragment {

    private TextView tvUserName;
    private ImageView ivAvatar;
    private HanoutDatabase database;

    // Avatar drawables
    private final int[] avatars = {
            R.drawable.avatar_red,
            R.drawable.avatar_teal,
            R.drawable.avatar_yellow,
            R.drawable.avatar_purple,
            R.drawable.avatar_marron
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Set status bar color
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setStatusBarColor(R.color.dashboard_background);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database
        database = HanoutDatabase.getInstance(getContext());

        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName);
        ivAvatar = view.findViewById(R.id.ivAvatar);

        // Load user data from Session
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("HanoutyPrefs",
                android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            loadUserData(userId);
        }

        // User Profile Card - Open Premium Dialog
        view.findViewById(R.id.btnUserProfile).setOnClickListener(v -> openPremiumDialog());

        // Conseils Premium - Open Premium Dialog
        view.findViewById(R.id.conseils).setOnClickListener(v -> openPremiumDialog());

        // Profile & Boutique
        view.findViewById(R.id.btnProfile).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToEditProfile();
            }
        });

        // Notifications
        view.findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Paramètres de notifications", Toast.LENGTH_SHORT).show();
        });

        // Support
        view.findViewById(R.id.btnSupport).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Contacter le support", Toast.LENGTH_SHORT).show();
        });

        // Logout
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // Clear Session
            android.content.SharedPreferences prefsLogout = getActivity().getSharedPreferences("HanoutyPrefs",
                    android.content.Context.MODE_PRIVATE);
            prefsLogout.edit().clear().apply();

            Toast.makeText(getContext(), "Déconnexion...", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToSignIn();
            }
        });

        // Bottom Navigation
        view.findViewById(R.id.navHome).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToHome();
            }
        });

        view.findViewById(R.id.navVente).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToSales();
            }
        });

        view.findViewById(R.id.navProduits).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToProducts();
            }
        });

        view.findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Already on profile page
        });
    }

    private void loadUserData(int userId) {
        database.userDAO().getUserById(userId).observe(getViewLifecycleOwner(), new Observer<UserData>() {
            @Override
            public void onChanged(UserData userData) {
                if (userData != null) {
                    tvUserName.setText(userData.getName());

                    // Set random avatar based on user ID
                    int avatarIndex = userData.getId_User() % avatars.length;
                    ivAvatar.setImageResource(avatars[avatarIndex]);
                }
            }
        });
    }

    private void openPremiumDialog() {
        PremiumDialogFragment dialog = new PremiumDialogFragment();
        dialog.show(getParentFragmentManager(), "PremiumDialog");
    }
}
