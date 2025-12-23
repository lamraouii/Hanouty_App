package com.example.hanout_app.view;

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

        // Changer la couleur de la barre d'état en vert
        ((MainActivity) getActivity()).setStatusBarColor(R.color.moroccan_green_button);

        Button btnStart = view.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
            ((MainActivity) getActivity()).navigateToSignIn();
        });

        return view;
    }
}
