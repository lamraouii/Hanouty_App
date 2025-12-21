package com.example.hanout_app.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.hanout_app.R;
import com.example.hanout_app.database.Data.UserData;
import com.example.hanout_app.database.HanoutDatabase;

public class EditProfileFragment extends Fragment {

    private EditText editName, editPhone, editNewPassword, editCurrentPassword;
    private HanoutDatabase database;
    private UserData currentUser;
    private int currentUserId = 1; // Assuming user ID 1 for now

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        // Changer la couleur de la barre d'état en terracotta/marron
        ((MainActivity) getActivity()).setStatusBarColor(R.color.maron);

        // Set status bar color to match the header background if needed,
        // but the layout has a header image, so maybe keep it transparent or default.
        // For now, let's keep it consistent.

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database
        database = HanoutDatabase.getInstance(getContext());

        // Initialize views
        editName = view.findViewById(R.id.editName);
        editPhone = view.findViewById(R.id.editPhone);
        editNewPassword = view.findViewById(R.id.editNewPassword);
        editCurrentPassword = view.findViewById(R.id.editCurrentPassword);

        // Load user data from Session
        android.content.SharedPreferences prefs = getActivity().getSharedPreferences("HanoutyPrefs",
                android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            loadUserData(userId);
        } else {
            Toast.makeText(getContext(), "Erreur session. Veuillez vous reconnecter.", Toast.LENGTH_SHORT).show();
            ((MainActivity) getActivity()).navigateToSignIn();
        }

        // Back button
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        // Save button
        view.findViewById(R.id.btnSave).setOnClickListener(v -> saveChanges());
    }

    private void loadUserData(int userId) {
        database.userDAO().getUserById(userId).observe(getViewLifecycleOwner(), new Observer<UserData>() {
            @Override
            public void onChanged(UserData userData) {
                if (userData != null) {
                    currentUser = userData;
                    editName.setText(userData.getName());
                    editPhone.setText(userData.getPhone());
                    // Don't pre-fill password fields
                }
            }
        });
    }

    private void saveChanges() {
        if (currentUser == null)
            return;

        String newName = editName.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        String newPassword = editNewPassword.getText().toString().trim();
        String currentPasswordInput = editCurrentPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(newName)) {
            editName.setError("Le nom est requis");
            return;
        }

        if (TextUtils.isEmpty(newPhone)) {
            editPhone.setError("Le téléphone est requis");
            return;
        }

        if (TextUtils.isEmpty(currentPasswordInput)) {
            editCurrentPassword.setError("Mot de passe actuel requis pour modifier");
            return;
        }

        if (!currentPasswordInput.equals(currentUser.getPassword())) {
            editCurrentPassword.setError("Mot de passe actuel incorrect");
            return;
        }

        // Update data
        currentUser.setName(newName);
        currentUser.setPhone(newPhone);

        if (!TextUtils.isEmpty(newPassword)) {
            currentUser.setPassword(newPassword);
        }

        // Perform update in background
        new Thread(() -> {
            database.userDAO().modifyUser(currentUser);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).onBackPressed();
                    }
                });
            }
        }).start();
    }
}
