package com.example.hanout_app.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanout_app.R;

public class SplashFragment extends Fragment {

    private Handler handler;
    private PreferenceManager preferenceManager;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le PreferenceManager
        preferenceManager = ((MainActivity) requireActivity()).getPreferenceManager();

        handler = new Handler(Looper.getMainLooper());

        startAnimations(view);
        scheduleNavigation();
    }

    /* ================= ANIMATIONS ================= */

    private void startAnimations(View view) {
        View logo = view.findViewById(R.id.logo);
        TextView appName = view.findViewById(R.id.app_name);
        ProgressBar loading = view.findViewById(R.id.loading);

        // --- Logo: fade + scale + overshoot ---
        if (logo != null) {
            logo.setAlpha(0f);
            logo.setScaleX(0.5f);
            logo.setScaleY(0.5f);
            logo.animate()
                    .alpha(1f)
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(600)
                    .withEndAction(() -> logo.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(300)
                            .start())
                    .start();
        }

        // --- App Name: fade + translation ---
        if (appName != null) {
            appName.setAlpha(0f);
            appName.setTranslationY(30f);
            appName.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(300)
                    .setDuration(800)
                    .start();
        }


        // --- ProgressBar: fade-in ---
        if (loading != null) {
            loading.setAlpha(0f);
            loading.animate()
                    .alpha(1f)
                    .setStartDelay(500)
                    .setDuration(500)
                    .start();
        }
    }

    /* ================= NAVIGATION ================= */

    private void scheduleNavigation() {
        handler.postDelayed(this::navigateNext, Constants.SPLASH_DURATION);
    }

    private void navigateNext() {
        MainActivity activity = (MainActivity) requireActivity();

        if (preferenceManager.isFirstTimeLaunch()) {
            activity.navigateToOnboarding();
        } else if (preferenceManager.isLoggedIn()) {
            activity.navigateToHome();
        } else {
            activity.navigateToSignIn();
        }
    }

    /* ================= CLEANUP ================= */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Forcer le portrait uniquement quand ce fragment est visible
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
