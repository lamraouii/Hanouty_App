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

public class SignUpFragment extends Fragment {

    private TextInputEditText registerName;
    private TextInputEditText registerPhone;
    private TextInputEditText registerPassword;
    private TextInputEditText registerConfirmPassword;
    private Button registerButton;
    private TextView alreadyHaveAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Changer la couleur de la barre d'état en violet/pourpre
        ((MainActivity) getActivity()).setStatusBarColor(R.color.moroccan_green_button);

        // Initialiser les vues
        registerName = view.findViewById(R.id.registerName);
        registerPhone = view.findViewById(R.id.registerPhone);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerConfirmPassword = view.findViewById(R.id.registerConfirmPassword);
        registerButton = view.findViewById(R.id.registerButton);
        alreadyHaveAccount = view.findViewById(R.id.alreadyHaveAccount);

        // Bouton Créer un compte
        registerButton.setOnClickListener(v -> {
            String name = registerName.getText().toString().trim();
            String phone = registerPhone.getText().toString().trim();
            String password = registerPassword.getText().toString().trim();
            String confirmPassword = registerConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Implémenter la logique d'inscription
                Toast.makeText(getContext(), "Inscription en cours...", Toast.LENGTH_SHORT).show();
            }
        });

        // Lien vers connexion
        alreadyHaveAccount.setOnClickListener(v -> {
            ((MainActivity) getActivity()).navigateToSignIn();
        });

        return view;
    }
}
