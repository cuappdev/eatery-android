package com.cornellappdev.android.eatery.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.enums.OnboardingPageType;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class OnboardingInfoFragment extends Fragment {
    private TextView mTitle;
    private TextView mDescription;
    private OnboardingLoginFragment mOnboardingLoginFragment;
    private Button mButton;
    private Button mSecondaryButton;
    private OnboardingPageType onboardingPageType;
    private AccountPresenter mAccountPresenter;

    protected OnboardingInfoFragment(OnboardingPageType onboardingPageType) {
        this.onboardingPageType = onboardingPageType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_info, container, false);
        mTitle = view.findViewById(R.id.onboarding_info_title);
        mDescription = view.findViewById(R.id.onboarding_info_description);
        mButton = view.findViewById(R.id.onboarding_info_button);
        mSecondaryButton = view.findViewById(R.id.onboarding_secondary_button);

        mTitle.setText(onboardingPageType.getTitle());
        mDescription.setText(onboardingPageType.getDescription());

        mAccountPresenter = new AccountPresenter();

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

    protected void disableInteraction() {
        mButton.setEnabled(false);
        //mSecondaryButton.setEnabled(false);
    }

    protected void enableInteraction() {
        mButton.setEnabled(true);
        //mSecondaryButton.setEnabled(true);
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
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Moves to next onboarding item when "NEXT" button clicked.
                ((OnboardingActivity) getActivity()).getNextOnboardingPagerItem();
            }
        });
    }

    private void setupLoginButton() {
        mButton.setText(R.string.onboarding_button_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnboardingLoginFragment.login();
            }
        });
    }

    protected void endOnboarding() {
        ((OnboardingActivity) getActivity()).endOnboarding();
    }

    private void setupSkipButton() {
        mSecondaryButton.setText(R.string.onboarding_button_skip);
        mSecondaryButton.setVisibility(View.VISIBLE);
        mSecondaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endOnboarding();
            }
        });
    }
}
