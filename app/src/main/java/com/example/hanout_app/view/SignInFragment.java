package com.example.hanout_app.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.hanout_app.R;
// Imports basés sur ton arborescence (Tree)
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;
import com.google.android.material.textfield.TextInputEditText;

public class SignInFragment extends Fragment {

    private TextInputEditText phoneEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private Button inscriptionButton;
    private TextView forgotPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // 1. Gestion du clavier (Scroll automatique)
        setupKeyboardHandling(view);

        // 2. Initialisation des vues
        initViews(view);

        // 3. Configuration des actions (Listeners)
        setupListeners();

        return view;
    }

    private void setupKeyboardHandling(View view) {
        ScrollView scrollView = view.findViewById(R.id.scrollContent);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void initViews(View view) {
        phoneEditText = view.findViewById(R.id.phoneEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        inscriptionButton = view.findViewById(R.id.inscription);
        forgotPassword = view.findViewById(R.id.forgotPassword);
    }

    private void setupListeners() {
        // Bouton Connexion
        loginButton.setOnClickListener(v -> handleLogin());

        // Bouton Inscription -> Redirection vers SignUp
        inscriptionButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToSignUp();
            }
        });

        // Mot de passe oublié
        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fonctionnalité à venir", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Accès Base de données (Background Thread) ---
        HanoutDatabase.databaseWriteExecutor.execute(() -> {

            // On utilise UserDAO via l'instance de la base
            UserData user = HanoutDatabase
                    .getInstance(getContext())
                    .userDAO()
                    .getUserByPhoneSync(phone);

            // --- Retour sur le Thread Principal (UI) ---
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user == null) {
                        Toast.makeText(getContext(), "Ce numéro n'existe pas", Toast.LENGTH_SHORT).show();
                    } else if (!user.getPassword().equals(password)) {
                        Toast.makeText(getContext(), "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    } else {
                        // --- SUCCÈS ---
                        loginSuccess(user);
                    }
                });
            }
        });
    }

    private void loginSuccess(UserData user) {
        Toast.makeText(getContext(), "Marhba " + user.getName() + " !", Toast.LENGTH_SHORT).show();

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            // Sauvegarde via PreferenceManager (Mis à jour précédemment)
            // On convertit l'ID int en String car PreferenceManager attend des String pour la session
            mainActivity.getPreferenceManager().createLoginSession(
                    String.valueOf(user.getId_User()), // Conversion int -> String
                    user.getName(),
                    user.getPhone()
            );

            // Navigation vers l'accueil
            mainActivity.navigateToHome();
        }
    }
}