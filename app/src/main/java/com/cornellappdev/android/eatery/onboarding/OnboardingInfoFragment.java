package com.cornellappdev.android.eatery.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.enums.OnboardingPageType;

public class OnboardingInfoFragment extends Fragment {
    private OnboardingLoginFragment mOnboardingLoginFragment;
    private Button mButton;
    private Button mSecondaryButton;
    private OnboardingPageType onboardingPageType;
    private ProgressBar mProgressBar;

    OnboardingInfoFragment(OnboardingPageType onboardingPageType) {
        this.onboardingPageType = onboardingPageType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_info, container, false);
        TextView title = view.findViewById(R.id.onboarding_info_title);
        TextView description = view.findViewById(R.id.onboarding_info_description);
        mButton = view.findViewById(R.id.onboarding_info_button);
        mSecondaryButton = view.findViewById(R.id.onboarding_secondary_button);

        title.setText(onboardingPageType.getTitle());
        description.setText(onboardingPageType.getDescription());
        mProgressBar = view.findViewById(R.id.progress_loader);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                android.graphics.PorterDuff.Mode.MULTIPLY);

        setupContent();
        if (onboardingPageType == OnboardingPageType.LOGIN) {
            setupSkipButton();
            setupLoginButton();
        } else {
            mSecondaryButton.setVisibility(View.GONE);
            setupNextButton();
        }

        return view;
    }

    void loggingIn() {
        if (getActivity() != null && getContext() != null) {
            ((OnboardingActivity) getActivity()).setPagerEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            mButton.setEnabled(false);
            mSecondaryButton.setEnabled(false);
            mButton.setText("");
            mButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.fadedBlue));
        }
    }

    void resumeLoginGUI() {
        if (getActivity() != null && getContext() != null) {
            ((OnboardingActivity) getActivity()).setPagerEnabled(true);
            mButton.setEnabled(true);
            mButton.setText(R.string.login_label);
            mButton.setBackground(
                    ContextCompat.getDrawable(getContext(), R.drawable.bordered_button));
            mSecondaryButton.setEnabled(true);
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    private void setupContent() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (onboardingPageType) {
            // TODO (yanlam): Add dynamic rendering for animation.
            case MENUS:
                break;
            case COLLEGETOWN:
                break;
            case TRANSACTIONS:
                break;
            case LOGIN:
                // Set up login inputs.
                mOnboardingLoginFragment = new OnboardingLoginFragment();
                transaction.replace(R.id.onboarding_frame_layout,
                        mOnboardingLoginFragment).commit();
                break;
        }
    }

    private void setupNextButton() {
        mButton.setText(R.string.onboarding_button_next);
        mButton.setOnClickListener((View v) -> {
            if (getActivity() != null) {
                // Moves to next onboarding item when "NEXT" button clicked.
                ((OnboardingActivity) getActivity()).getNextOnboardingPagerItem();
            }
        });
    }

    private void setupLoginButton() {
        mButton.setText(R.string.onboarding_button_login);
        mButton.setOnClickListener((View v) -> mOnboardingLoginFragment.login());
    }

    void endOnboarding() {
        if (getActivity() != null) {
            ((OnboardingActivity) getActivity()).endOnboarding();
        }
    }

    private void setupSkipButton() {
        mSecondaryButton.setText(R.string.onboarding_button_skip);
        mSecondaryButton.setVisibility(View.VISIBLE);
        mSecondaryButton.setOnClickListener((View v) -> endOnboarding());
    }
}
