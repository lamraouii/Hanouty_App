package com.example.hanout_app.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.hanout_app.R;
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

        // Changer la couleur de la barre d'état en violet/pourpre
        ((MainActivity) getActivity()).setStatusBarColor(R.color.maron);

        // Initialiser les vues
        phoneEditText = view.findViewById(R.id.phoneEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        inscriptionButton = view.findViewById(R.id.inscription);
        forgotPassword = view.findViewById(R.id.forgotPassword);

        // Bouton Connexion
        loginButton.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                // Verify login in background
                com.example.hanout_app.database.HanoutDatabase.databaseWriteExecutor.execute(() -> {
                    com.example.hanout_app.database.Data.UserData user = com.example.hanout_app.database.HanoutDatabase
                            .getInstance(getContext())
                            .userDAO().getUserByPhoneSync(phone);

                    // Back to Main Thread for UI updates
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (user == null) {
                                Toast.makeText(getContext(), "Ce numéro de téléphone n'existe pas", Toast.LENGTH_SHORT)
                                        .show();
                            } else if (!user.getPassword().equals(password)) {
                                Toast.makeText(getContext(), "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                            } else {
                                // Login Success
                                Toast.makeText(getContext(), "Connexion réussie!", Toast.LENGTH_SHORT).show();

                                // Save Session
                                android.content.SharedPreferences prefs = getActivity()
                                        .getSharedPreferences("HanoutyPrefs", android.content.Context.MODE_PRIVATE);
                                android.content.SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putInt("userId", user.getId_User());
                                editor.apply();

                                ((MainActivity) getActivity()).navigateToHome();
                            }
                        });
                    }
                });
            }
        });

        // Bouton Inscription - Navigation vers SignUp
        inscriptionButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).navigateToSignUp();
        });

        // Lien mot de passe oublié
        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Fonctionnalité à venir", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
