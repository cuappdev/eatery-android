package com.cornellappdev.android.eatery.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.enums.CacheType;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.network.QueryUtilities;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;
import com.cornellappdev.android.eatery.util.InternalStorage;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;

public class OnboardingLoginFragment extends Fragment {
    private EditText mNetID;
    private EditText mPassword;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView mDescriptionText;

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_login, container, false);
        Context currContext = getContext();

        mNetID = view.findViewById(R.id.onboarding_netid_input);
        mPassword = view.findViewById(R.id.onboarding_password_input);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(currContext);
        mDescriptionText = view.findViewById(R.id.onboarding_login_description);

        mainActivity = (MainActivity) getActivity();

        if (mainActivity.isAccountPresenterLoggedIn()) {
            String[] loginInfo = mainActivity.getLoginInfo();
            if (loginInfo != null) {
                // This is not null upon auto-logging in on app launch. Thus, set the textfields
                // to the values found in the files
                mNetID.setText(loginInfo[0]);
                mPassword.setText(loginInfo[1]);
            }
        } else {
            GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
                @Override
                public void failedLogin() {
                    // If the user is still viewing this fragment
                    if (getFragmentManager() != null) {
                        mDescriptionText.setText("Incorrect netid and/or password\n");
                        mDescriptionText.setTextColor(getResources().getColor(R.color.red));
                        mainActivity.setAccountPresenterLoggingIn(false);
                    }
                }

                @Override
                public void successLogin(BrbInfoQuery.AccountInfo accountInfo) {
                    Repository.getInstance().setBrbInfoModel(QueryUtilities.parseBrbInfo(accountInfo));
                    BrbInfoModel model = Repository.getInstance().getBrbInfoModel();
                    try {
                        InternalStorage.writeObject(currContext, CacheType.BRB, model);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (model == null) {
                        mDescriptionText.setText("Internal Error\n");
                        mDescriptionText.setTextColor(getResources().getColor(R.color.red));
                        mainActivity.setAccountPresenterLoggingIn(false);
                    } else {
                        mainActivity.setAccountPresenterBrbInfo(model);
                        mainActivity.setAccountPresenterLoggingIn(false);
                    }
                }
            };
            MainActivity.sLoginWebView.getSettings().setJavaScriptEnabled(true);
            MainActivity.sLoginWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Gets called on every page redirect - initial redirect is loadurl(...)
                    GetLoginUtilities.webLogin(url, view, callback);
                }
            });
        }

        return view;
    }

    private void loadingGUI() {
        mDescriptionText.setTextColor(getResources().getColor(R.color.white));
        mDescriptionText.setText("Logging in. This may take a minute ...\n");
        mNetID.setEnabled(false);
        mPassword.setEnabled(false);
    }

    public void login() {
        mFirebaseAnalytics.logEvent("user_brb_login", null);
        mainActivity.setAccountPresenterLoggingIn(true);
        mainActivity.setAccountPresenterFields(mNetID.getText().toString(), mPassword.getText().toString());
        loadingGUI();

        // change the login javascript to have the correct username and password
        mainActivity.resetAccountPresenterJS();

        MainActivity.sLoginWebView.loadUrl(getString(R.string.getlogin_url));
    }
}
