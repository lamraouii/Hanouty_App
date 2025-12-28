/*package com.example.hanout_app.view;

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
// Imports corrects selon votre arborescence
import com.example.hanout_app.database.Dao.UserDAO;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;
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

        // 1. Gestion du scroll avec le clavier (Essentiel pour Edge-to-Edge)
        setupKeyboardHandling(view);

        // 2. Initialisation des vues
        initViews(view);

        // 3. Configuration des actions
        setupListeners();

        return view;
    }

    // --- Gestion Clavier ---
    private void setupKeyboardHandling(View view) {
        ScrollView scrollView = view.findViewById(R.id.scrollContent);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(0, 0, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    // --- Initialisation ---
    private void initViews(View view) {
        registerName = view.findViewById(R.id.registerName);
        registerPhone = view.findViewById(R.id.registerPhone);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerConfirmPassword = view.findViewById(R.id.registerConfirmPassword);
        registerButton = view.findViewById(R.id.registerButton);
        alreadyHaveAccount = view.findViewById(R.id.alreadyHaveAccount);
    }

    // --- Listeners ---
    private void setupListeners() {
        // Bouton Créer compte
        registerButton.setOnClickListener(v -> handleSignUp());

        // Lien "J'ai déjà un compte" -> Aller vers SignIn
        alreadyHaveAccount.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToSignIn();
            }
        });
    }

    // --- Logique d'Inscription ---
    private void handleSignUp() {
        String name = registerName.getText().toString().trim();
        String phone = registerPhone.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String confirmPassword = registerConfirmPassword.getText().toString().trim();

        // Validations
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 10) {
            Toast.makeText(getContext(), "Le numéro de téléphone doit contenir 10 chiffres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Traitement en arrière-plan
        HanoutDatabase.databaseWriteExecutor.execute(() -> {
            UserDAO userDao = HanoutDatabase.getInstance(getContext()).userDAO();

            // 1. Vérifier si l'utilisateur existe déjà
            if (userDao.getUserByPhoneSync(phone) != null) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ce numéro existe déjà", Toast.LENGTH_SHORT).show()
                    );
                }
            } else {
                // 2. Création de l'objet User
                // Note : Assurez-vous que le constructeur UserData(String, String, String) existe
                UserData newUser = new UserData(name, phone, password);

                // Email fictif pour combler le champ (optionnel selon votre logique)
                newUser.setEmail(phone + "@hanouty.local");

                // 3. Insertion dans la BDD
                userDao.addUser(newUser);

                // 4. Récupérer l'utilisateur créé (pour avoir son ID généré automatiquement)
                UserData createdUser = userDao.getUserByPhoneSync(phone);

                // 5. Finaliser sur le Thread Principal
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> finalizeRegistration(createdUser));
                }
            }
        });
    }

    private void finalizeRegistration(UserData user) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            Toast.makeText(getContext(), "Compte créé avec succès !", Toast.LENGTH_SHORT).show();

            // --- AMÉLIORATION UX ---
            // Au lieu d'aller vers SignIn, on connecte directement l'utilisateur
            mainActivity.getPreferenceManager().createLoginSession(
                    String.valueOf(user.getId_User()), // Conversion ID -> String
                    user.getName(),
                    user.getPhone()
            );

            // Redirection immédiate vers l'accueil
            mainActivity.navigateToHome();
        }
    }
}

 */

package com.example.hanout_app.view;

import android.os.Bundle;
import android.util.Log;
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
import com.example.hanout_app.database.Dao.UserDAO;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;
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

        // J'ai gardé votre méthode de gestion du clavier comme demandé
        setupKeyboardHandling(view);

        initViews(view);
        setupListeners();

        return view;
    }

    // --- Votre méthode originale pour le clavier ---
    private void setupKeyboardHandling(View view) {
        ScrollView scrollView = view.findViewById(R.id.scrollContent);
        if (scrollView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
                v.setPadding(0, 0, 0, insets.bottom);
                return WindowInsetsCompat.CONSUMED;
            });
        }
    }

    private void initViews(View view) {
        registerName = view.findViewById(R.id.registerName);
        registerPhone = view.findViewById(R.id.registerPhone);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerConfirmPassword = view.findViewById(R.id.registerConfirmPassword);
        registerButton = view.findViewById(R.id.registerButton);
        alreadyHaveAccount = view.findViewById(R.id.alreadyHaveAccount);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleSignUp());

        alreadyHaveAccount.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToSignIn();
            }
        });
    }

    private void handleSignUp() {
        String name = registerName.getText().toString().trim();
        String phone = registerPhone.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String confirmPassword = registerConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 10) {
            Toast.makeText(getContext(), "Le numéro de téléphone doit contenir 10 chiffres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sécurité pour éviter le double clic qui fait planter
        registerButton.setEnabled(false);

        HanoutDatabase.databaseWriteExecutor.execute(() -> {
            try {
                UserDAO userDao = HanoutDatabase.getInstance(getContext()).userDAO();

                if (userDao.getUserByPhoneSync(phone) != null) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Ce numéro existe déjà", Toast.LENGTH_SHORT).show();
                            registerButton.setEnabled(true);
                        });
                    }
                } else {
                    UserData newUser = new UserData(name, phone, password);
                    newUser.setEmail(phone + "@hanouty.com");

                    userDao.addUser(newUser);

                    UserData createdUser = userDao.getUserByPhoneSync(phone);

                    // --- CORRECTION DU CRASH ---
                    // On vérifie que createdUser n'est pas NULL avant de l'utiliser
                    if (createdUser != null && getActivity() != null) {
                        getActivity().runOnUiThread(() -> finalizeRegistration(createdUser));
                    } else {
                        // Gestion d'erreur silencieuse si l'activité est fermée
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> registerButton.setEnabled(true));
                        }
                    }
                }
            } catch (Exception e) {
                // Empêche l'application de quitter en cas d'erreur imprévue
                Log.e("SignUpError", "Erreur: " + e.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Une erreur est survenue", Toast.LENGTH_SHORT).show();
                        registerButton.setEnabled(true);
                    });
                }
            }
        });
    }

    /*private void finalizeRegistration(UserData user) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            Toast.makeText(getContext(), "Compte créé avec succès !", Toast.LENGTH_SHORT).show();

            try {
                mainActivity.getPreferenceManager().createLoginSession(
                        String.valueOf(user.getId_User()),
                        user.getName(),
                        user.getPhone()
                );
                mainActivity.navigateToHome();
            } catch (Exception e) {
                // Sécurité supplémentaire
                mainActivity.navigateToSignIn();
            }
        }
    }*/

    private void finalizeRegistration(UserData user) {
        // 1. Vérification de sécurité : si l'activité est déjà fermée, on arrête tout
        if (getActivity() == null) return;

        // 2. On force l'exécution sur le Thread Principal (UI)
        getActivity().runOnUiThread(() -> {

            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();

                // Vérification que l'user n'est pas null pour éviter le crash
                if (user == null) {
                    Toast.makeText(getContext(), "Erreur à la création du compte", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(), "Compte créé avec succès !", Toast.LENGTH_SHORT).show();

                try {
                    // On s'assure que le PreferenceManager est accessible
                    if (mainActivity.getPreferenceManager() != null) {
                        mainActivity.getPreferenceManager().createLoginSession(
                                String.valueOf(user.getId_User()),
                                user.getName(),
                                user.getPhone()
                        );

                        // Navigation
                        mainActivity.navigateToHome();
                    } else {
                        // Si le manager n'est pas prêt, on instancie localement pour sauver la mise
                        PreferenceManager localPref = new PreferenceManager(requireContext());
                        localPref.createLoginSession(
                                String.valueOf(user.getId_User()),
                                user.getName(),
                                user.getPhone()
                        );
                        mainActivity.navigateToHome();
                    }

                } catch (Exception e) {
                    // 3. IMPORTANT : Afficher l'erreur dans les logs (Logcat) pour comprendre
                    e.printStackTrace();

                    Toast.makeText(getContext(), "Erreur connexion auto : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    mainActivity.navigateToSignIn();
                }
            }
        });
    }
}