package com.example.hanout_app.view;

import android.content.Intent;
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
    private PreferenceManager preferenceManager; // Ajout du gestionnaire

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

        // Set status bar color if needed
        if (getActivity() != null) {
            //((MainActivity) getActivity()).setStatusBarColor(R.color.dashboard_background);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialiser la Base de données et le PreferenceManager
        database = HanoutDatabase.getInstance(getContext());
        preferenceManager = new PreferenceManager(requireContext());

        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName);
        ivAvatar = view.findViewById(R.id.ivAvatar);

        // 2. Charger les données utilisateur via PreferenceManager
        String userIdStr = preferenceManager.getUserId();

        // Comme PreferenceManager stocke un String mais la DB attend peut-être un int,
        // on convertit et on vérifie que ce n'est pas vide.
        if (!userIdStr.isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdStr);
                loadUserData(userId);
            } catch (NumberFormatException e) {
                // Gérer le cas où l'ID n'est pas un nombre valide
                e.printStackTrace();
                Toast.makeText(getContext(), "Erreur de chargement profil", Toast.LENGTH_SHORT).show();
            }
        }

        // --- Listeners ---

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

        // 3. Logout refactorisé avec PreferenceManager
        /*view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // Utilisation de la méthode dédiée du manager
            preferenceManager.logoutUser();

            Toast.makeText(getContext(), "Déconnexion...", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateToSignIn();
            }
        });*/
// ... dans onViewCreated ...

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // 1. Nettoyer les préférences (Session)
            preferenceManager.logoutUser();
            Toast.makeText(getContext(), "Déconnexion...", Toast.LENGTH_SHORT).show();

            // 2. Créer une intention pour redémarrer MainActivity
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);

                // CES FLAGS SONT LA CLÉ :
                // CLEAR_TASK : Vide toute la pile d'activités/fragments
                // NEW_TASK : Démarre une nouvelle tâche vierge
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                // 3. Fermer l'instance actuelle (juste par sécurité)
                getActivity().finish();
            }
        });

        // Bottom Navigation
        setupBottomNavigation(view);
    }

    private void setupBottomNavigation(View view) {
        view.findViewById(R.id.navHome).setOnClickListener(v -> {
            if (getActivity() != null) ((MainActivity) getActivity()).navigateToHome();
        });

        view.findViewById(R.id.navVente).setOnClickListener(v -> {
            if (getActivity() != null) ((MainActivity) getActivity()).navigateToSales();
        });

        view.findViewById(R.id.navProduits).setOnClickListener(v -> {
            if (getActivity() != null) ((MainActivity) getActivity()).navigateToProducts();
        });

        view.findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Already on profile page
        });
    }

    private void loadUserData(int userId) {
        // On observe la DB pour avoir les mises à jour en temps réel (ex: modification du nom)
        database.userDAO().getUserById(userId).observe(getViewLifecycleOwner(), new Observer<UserData>() {
            @Override
            public void onChanged(UserData userData) {
                if (userData != null) {
                    tvUserName.setText(userData.getName());

                    // Gestion de l'avatar aléatoire basé sur l'ID
                    // Assurez-vous que getId_User() retourne bien un int
                    int avatarIndex = userData.getId_User() % avatars.length;

                    // Protection contre index hors limites (juste au cas où)
                    if (avatarIndex >= 0 && avatarIndex < avatars.length) {
                        ivAvatar.setImageResource(avatars[avatarIndex]);
                    }
                }
            }
        });
    }

    private void openPremiumDialog() {
        PremiumDialogFragment dialog = new PremiumDialogFragment();
        dialog.show(getParentFragmentManager(), "PremiumDialog");
    }
}