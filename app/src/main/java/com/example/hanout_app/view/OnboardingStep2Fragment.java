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

public class OnboardingStep2Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_step_2, container, false);

        // Changer la couleur de la barre d'état en bleu
        ((MainActivity) getActivity()).setStatusBarColor(R.color.moroccan_royal_blue);

        Button btnNext = view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new OnboardingStep3Fragment());
        });

        return view;
    }
}
