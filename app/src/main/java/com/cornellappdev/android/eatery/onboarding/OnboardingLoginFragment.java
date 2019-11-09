package com.cornellappdev.android.eatery.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.loginviews.AccountInfoFragment;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;

public class OnboardingLoginFragment extends Fragment {
    private EditText mNetID;
    private EditText mPassword;
    private TextView mDescriptionText;
    private TextView mPrivacy;
    private Button mLoginButton;
    private ProgressBar mProgressBar;
    private AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
    private AccountPresenter mAccountPresenter = new AccountPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_login, container, false);

        mDescriptionText = view.findViewById(R.id.onboarding_description);
        mLoginButton = view.findViewById(R.id.onboarding_login_button);
        mNetID = view.findViewById(R.id.onboarding_netid_input);
        mPassword = view.findViewById(R.id.onboarding_password_input);
        mProgressBar = view.findViewById(R.id.onboarding_progress_loader);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                android.graphics.PorterDuff.Mode.MULTIPLY);
        return view;
    }
}
