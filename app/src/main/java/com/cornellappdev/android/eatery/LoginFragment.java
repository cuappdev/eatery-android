package com.cornellappdev.android.eatery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;

public class LoginFragment extends Fragment {
    private TextView mDescriptionText;
    private Button mLoginButton;
    private EditText mNetID;
    private EditText mPassword;
    private TextView mPrivacy;
    private ProgressBar mProgressBar;
    private CheckBox mSaveInfoCheck;
    private AccountInfoFragment accountInfoFragment = new AccountInfoFragment();

    private AccountPresenter mAccountPresenter = new AccountPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");

       if(mAccountPresenter.isLoggedIn()){
            loadAccountPage(true);
            return rootView;
        }
        mPrivacy = rootView.findViewById(R.id.privacyStatement);
        mDescriptionText = rootView.findViewById(R.id.descriptionText);
        mSaveInfoCheck = rootView.findViewById(R.id.saveInfoCheck);
        mLoginButton = rootView.findViewById(R.id.login);
        mNetID = rootView.findViewById(R.id.net_id_input);
        mPassword = rootView.findViewById(R.id.password_input);
        mProgressBar = rootView.findViewById(R.id.progress_loader);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                android.graphics.PorterDuff.Mode.MULTIPLY);
        if(mAccountPresenter.isLoggingIn()) {
            mAccountPresenter.setContext(getContext());
            String[] loginInfo = mAccountPresenter.readSavedCredentials();
            if(loginInfo!=null){
                mNetID.setText(loginInfo[0]);
                mPassword.setText(loginInfo[1]);
                mSaveInfoCheck.setChecked(true);
                mAccountPresenter.setSaveCredentials(true);
            }
            loadingGUI();
        }
        else {
            resumeGUI();
            mSaveInfoCheck.setChecked(mAccountPresenter.getSaveCredentials());
            mPrivacy.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    PrivacyFragment privacyFragment = new PrivacyFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction
                            .replace(R.id.frame_fragment_holder, privacyFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            mSaveInfoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    mAccountPresenter.setSaveCredentials(isChecked);
                }
            });
            GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
                @Override
                public void failedLogin() {
                    if (getFragmentManager() != null) {
                        mDescriptionText.setText("Incorrect netid and/or password\n");
                        mDescriptionText.setTextColor(getResources().getColor(R.color.red));
                        mAccountPresenter.setLoggingIn(false);
                        resumeGUI();
                    }
                }

                @Override
                public void successLogin(BrbInfoQuery.AccountInfo accountInfo) {
                    BrbInfoModel model = JsonUtilities.parseBrbInfo(accountInfo);
                    mAccountPresenter.setBrbModel(model);
                    mAccountPresenter.setLoggingIn(false);
                    if(getFragmentManager()!=null) { // If user is still viewing this fragment
                        loadAccountPage(false);
                    }
                }
            };

            MainActivity.sLoginWebView.getSettings().setJavaScriptEnabled(true);
            MainActivity.sLoginWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Gets called on every page redirect
                    GetLoginUtilities.loginBrb(url, view, callback);
                }
            });
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mAccountPresenter.setLoggingIn(true);
                    loadingGUI();
                    mAccountPresenter.setNetID(mNetID.getText().toString());
                    mAccountPresenter.setPassword(mPassword.getText().toString());
                    mAccountPresenter.setContext(getContext());
                    mAccountPresenter.resetLoginJson();

                    MainActivity.sLoginWebView.loadUrl("https://get.cbord.com/cornell/full/login.php?mobileapp=1");
                }
            });


        }

        return rootView;
    }

    private void loadingGUI() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLoginButton.setBackgroundColor(getResources().getColor(R.color.fadedblue));
        mLoginButton.setText("");
        mDescriptionText.setTextColor(getResources().getColor(R.color.closed));
        mDescriptionText.setText("Logging in. This may take a minute ...\n");
        mNetID.setEnabled(false);
        mPassword.setEnabled(false);
        mSaveInfoCheck.setEnabled(false);
        Log.i("Enabled", "NOP");
    }

    private void resumeGUI() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mLoginButton.setBackgroundColor(getResources().getColor(R.color.blue));
        mLoginButton.setText(R.string.login_label);
        mNetID.setEnabled(true);
        mPassword.setEnabled(true);
        mSaveInfoCheck.setEnabled(true);
    }

    private void loadAccountPage(boolean autologged) {

        if (getFragmentManager().findFragmentById(R.id.frame_fragment_holder) instanceof LoginFragment) {
            if(!autologged) {
                if (mAccountPresenter.getSaveCredentials()) {
                    mAccountPresenter.outputCredentialsToFile();
                } else {
                    mAccountPresenter.eraseSavedCredentials();
                }
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction
                    .replace(R.id.frame_fragment_holder, accountInfoFragment)
                    .commit();
        }

    }
    // Called from auto-logging in on app launch
    public void setLoading(boolean b){
        mAccountPresenter.setLoggingIn(b);
    }

}
