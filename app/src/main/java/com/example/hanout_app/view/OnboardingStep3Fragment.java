package com.example.hanout_app.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.hanout_app.R;

public class OnboardingStep3Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_step_3, container, false);

        Button btnStart = view.findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) requireActivity();

            // 1. IMPORTANT : On dit au PreferenceManager que l'intro est finie
            mainActivity.getPreferenceManager().setFirstTimeLaunch(false);

            // 2. Ensuite, on navigue vers la connexion
            mainActivity.navigateToSignIn();
        });

        return view;
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