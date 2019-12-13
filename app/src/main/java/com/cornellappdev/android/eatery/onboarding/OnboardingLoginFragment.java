package com.cornellappdev.android.eatery.onboarding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.enums.CacheType;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.network.QueryUtilities;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;
import com.cornellappdev.android.eatery.util.InternalStorage;

import java.io.IOException;

@SuppressLint("SetJavaScriptEnabled")
public class OnboardingLoginFragment extends Fragment {
    private EditText mNetID;
    private EditText mPassword;
    private TextView mDescriptionText;
    private WebView mWebView;
    private OnboardingInfoFragment mOnboardingInfoFragment;

    private AccountPresenter mAccountPresenter = new AccountPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_login, container, false);
        Context currContext = getContext();

        mNetID = view.findViewById(R.id.onboarding_netid_input);
        mPassword = view.findViewById(R.id.onboarding_password_input);
        mDescriptionText = view.findViewById(R.id.onboarding_login_description);
        mOnboardingInfoFragment = (OnboardingInfoFragment) getParentFragment();
        mWebView = view.findViewById(R.id.login_webview);

        GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
            @Override
            public void failedLogin() {
                // If the user is still viewing this fragment
                if (getFragmentManager() != null && currContext != null) {
                    mDescriptionText.setText("Incorrect netid and/or password\n");
                    mDescriptionText.setTextColor(ContextCompat.getColor(currContext, R.color.red));
                    resumeGUI();
                }
            }

            @Override
            public void successLogin(BrbInfoQuery.AccountInfo accountInfo) {
                Repository.getInstance().setBrbInfoModel(QueryUtilities.parseBrbInfo(accountInfo));
                BrbInfoModel model = Repository.getInstance().getBrbInfoModel();
                try {
                    if (currContext != null) {
                        mAccountPresenter.outputCredentialsToFile(currContext);
                        InternalStorage.writeObject(currContext, CacheType.BRB, model);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (model == null) {
                    mDescriptionText.setText("Internal Error\n");
                    if (currContext != null) {
                        mDescriptionText.setTextColor(
                                ContextCompat.getColor(currContext, R.color.red));
                    }
                    resumeGUI();
                } else {
                    Repository.getInstance().setBrbInfoModel(model);
                    // If user is still viewing this fragment
                    mOnboardingInfoFragment.endOnboarding();
                }
            }
        };

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // Gets called on every page redirect - initial redirect is loadurl(...)
                GetLoginUtilities.webLogin(url, view, callback);
            }
        });

        return view;
    }

    private void resumeGUI() {
        mOnboardingInfoFragment.resumeLoginGUI();
        mNetID.setEnabled(true);
        mPassword.setEnabled(true);
    }

    private void loadingGUI() {
        mOnboardingInfoFragment.loggingIn();
        mDescriptionText.setText("Logging in. This may take a minute ...\n");
        mNetID.setEnabled(false);
        mPassword.setEnabled(false);
    }

    public void login() {
        loadingGUI();
        mAccountPresenter.setNetID(mNetID.getText().toString());
        mAccountPresenter.setPassword(mPassword.getText().toString());
        // change the login javascript to have the correct username and password
        mAccountPresenter.resetLoginJS();
        mWebView.loadUrl(getString(R.string.getlogin_url));
    }
}
