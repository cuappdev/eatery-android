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

import com.airbnb.lottie.LottieAnimationView;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.enums.OnboardingPageType;

public class OnboardingInfoFragment extends Fragment {
    private OnboardingLoginFragment mOnboardingLoginFragment;
    private LottieAnimationView mLottieAnimationView;
    private Button mButton;
    private Button mSecondaryButton;
    private static final String ARG_PARAM1 = "OnboardingPageType";
    private ProgressBar mProgressBar;
    private OnboardingPageType onboardingPageType = OnboardingPageType.MENUS;

    public OnboardingInfoFragment() {

    }

    public static OnboardingInfoFragment newInstance(OnboardingPageType onboardingPageType) {
        OnboardingInfoFragment fragment = new OnboardingInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, onboardingPageType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            onboardingPageType = (OnboardingPageType) getArguments().get(ARG_PARAM1);
        }
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
//        mProgressBar = view.findViewById(R.id.progress_loader);
//        mProgressBar.setVisibility(View.INVISIBLE);
//        mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
//                android.graphics.PorterDuff.Mode.MULTIPLY);

        mLottieAnimationView = view.findViewById(R.id.onboarding_animation);
        mLottieAnimationView.setAnimation(onboardingPageType.getAnimationRaw());

        setupContent();
        setupNextButton();
//        if (onboardingPageType == OnboardingPageType.MENUS) {
//            setupSkipButton();
//            setupLoginButton();
//            if (getActivity() != null) {
//                if (((OnboardingActivity) getActivity()).getLoggingIn()) {
//                    this.loggingIn();
//                }
//            }
//        } else {
//            mSecondaryButton.setVisibility(View.GONE);
//            setupNextButton();
//        }

        return view;
    }

    void reloadAnimation() {
        if (mLottieAnimationView != null) {
            mLottieAnimationView.playAnimation();
        }
    }

    void loggingIn() {
        if (getActivity() != null && getContext() != null) {
            ((OnboardingActivity) getActivity()).setLoggingIn(true);
//            mProgressBar.setVisibility(View.VISIBLE);
            mButton.setEnabled(false);
            mButton.setText("");
            mButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.fadedBlue));
            //mSecondaryButton.setEnabled(false);
        }
    }

    void resumeLoginGUI() {
        if (getActivity() != null && getContext() != null) {
            ((OnboardingActivity) getActivity()).setLoggingIn(false);
            mButton.setEnabled(true);
            mButton.setText(R.string.login_label);
            mButton.setBackground(
                    ContextCompat.getDrawable(getContext(), R.drawable.bordered_button));
            mSecondaryButton.setEnabled(true);
//            mProgressBar.setVisibility(View.INVISIBLE);
//            mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
//                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    private void setupContent() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (onboardingPageType) {
            case MENUS:
                mLottieAnimationView.setScale(0.25f);
                mLottieAnimationView.setVisibility(View.VISIBLE);
                break;
            case TRANSACTIONS:
                mLottieAnimationView.setScale(0.232f);
                mLottieAnimationView.setVisibility(View.VISIBLE);
                break;
            case LOGIN:
                // Set up login inputs.
                mLottieAnimationView.setVisibility(View.GONE);
                mOnboardingLoginFragment = new OnboardingLoginFragment();
                transaction.replace(R.id.onboarding_frame_layout,
                        mOnboardingLoginFragment).commit();
                break;
        }
    }

    private void setupNextButton() {
        mButton.setText("LET'S GO");
        mButton.setOnClickListener((View v) -> endOnboarding());
//        mProgressBar.setVisibility(View.INVISIBLE);

//        mButton.setOnClickListener((View v) -> {
//
//            if (getActivity() != null) {
//                // Moves to next onboarding item when "NEXT" button clicked.
//                ((OnboardingActivity) getActivity()).getNextOnboardingPagerItem();
//            }
//        });
    }

    private void setupLoginButton() {
        mButton.setText(R.string.onboarding_button_login);
        mButton.setOnClickListener((View v) -> mOnboardingLoginFragment.login());
    }

    void endOnboarding() {
//        if (getActivity() != null) {
//            ((OnboardingActivity) getActivity()).endOnboarding();
//        }
        ((OnboardingActivity) getActivity()).endOnboarding();

    }

    private void setupSkipButton() {
        mSecondaryButton.setText(R.string.onboarding_button_skip);
        mSecondaryButton.setVisibility(View.VISIBLE);
        mSecondaryButton.setOnClickListener((View v) -> endOnboarding());
    }
}
