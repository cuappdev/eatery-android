package com.cornellappdev.android.eatery.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cornellappdev.android.eatery.R;

public class OnboardingInfoFragment extends Fragment {
    private TextView mTitle;
    private TextView mDescription;
    private ImageView mImageView;
    private Button mButton;
    private String title;
    private String description;

    public OnboardingInfoFragment(String title, String description) {
       this.title = title;
       this.description = description;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context currContext = getContext();
        View view = inflater.inflate(R.layout.fragment_onboarding_info, container, false);
        mTitle = view.findViewById(R.id.onboarding_info_title);
        mDescription = view.findViewById(R.id.onboarding_info_description);
        mImageView = view.findViewById(R.id.onboarding_info_image_view);
        mButton = view.findViewById(R.id.onboarding_info_button);

        mTitle.setText(title);
        mDescription.setText(description);
        return view;
    }
}
