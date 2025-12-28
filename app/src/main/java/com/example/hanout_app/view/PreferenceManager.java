package com.example.hanout_app.view;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gestionnaire des préférences partagées
 * Gère la persistance des données locales (première ouverture, connexion, user)
 */
public class PreferenceManager {

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    // Clés constantes
    private static final String PREF_NAME = "HanoutPref";
    private static final String KEY_FIRST_TIME = "isFirstTime";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    // Données utilisateur
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PHONE = "userPhone"; // Remplace l'email car vous utilisez le téléphone
    private static final String KEY_USER_PASSWORD = "userPassword"; // (Optionnel, attention à la sécurité)

    /**
     * Constructeur
     * @param context Context de l'application
     */
    public PreferenceManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /* ================= FIRST LAUNCH (ONBOARDING) ================= */

    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(KEY_FIRST_TIME, isFirstTime);
        editor.apply();
    }

    /* ================= SESSION & AUTH ================= */

    /**
     * Crée la session utilisateur après une connexion ou inscription réussie
     */
    public void createLoginSession(String userId, String name, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logoutUser() {
        // On garde KEY_FIRST_TIME à false, mais on efface les données utilisateur
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_PHONE);
        editor.apply();
    }

    /* ================= GETTERS (Pour récupérer les infos) ================= */

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "Utilisateur");
    }

    public String getUserPhone() {
        return preferences.getString(KEY_USER_PHONE, "");
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, "");
    }
}