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

public class OnboardingStep2Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_step_2, container, false);

        // Changer la couleur de la barre d'état en bleu
        //((MainActivity) getActivity()).setStatusBarColor(R.color.moroccan_royal_blue);

        Button btnNext = view.findViewById(R.id.btnNext2);
        btnNext.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new OnboardingStep3Fragment(),false);
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
