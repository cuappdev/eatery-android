package com.cornellappdev.android.eatery.loginviews;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
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

import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * This fragment is the login page reached from the bottomnavbar, and is the screen where users
 * input their netid and password to view their account information. Upon a successful login,
 * redirect to AccountInfoFragment
 */
public class LoginFragment extends Fragment {
    private EditText mNetID;
    private EditText mPassword;
    private TextView mDescriptionText;
    private TextView mPrivacy;
    private Button mLoginButton;
    private ProgressBar mProgressBar;
    private CheckBox mSaveInfoCheck;
    private AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
    private AccountPresenter mAccountPresenter = new AccountPresenter();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        if (getActivity() != null) {
            getActivity().setTitle("Login");
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                // Disable the back button
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
        if (mAccountPresenter.isLoggedIn()) {
            // If the user has already logged in in this session of the app, just load the
            // accountInfo page and don't require another login
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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        if (mAccountPresenter.isLoggingIn()) {
            mAccountPresenter.setContext(getContext());
            String[] loginInfo = mAccountPresenter.readSavedCredentials();
            if (loginInfo != null) {
                // This is not null upon auto-logging in on app launch. Thus, set the textfields
                // to the values found in the files
                mNetID.setText(loginInfo[0]);
                mPassword.setText(loginInfo[1]);
                mSaveInfoCheck.setChecked(true);
                mAccountPresenter.setSaveCredentials(true);
            }
            // Always display animation whether logging in automatically or after
            // manually clicking logging in and renavigating to this page
            loadingGUI();
        } else {
            resumeGUI();
            mSaveInfoCheck.setChecked(mAccountPresenter.getSaveCredentials());
            mPrivacy.setOnClickListener(new View.OnClickListener() {
                // On privacy statement clicked
                public void onClick(View v) {
                    PrivacyFragment privacyFragment = new PrivacyFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_fragment_holder, privacyFragment).addToBackStack(
                            null).commit();
                }
            });
            mSaveInfoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mAccountPresenter.setSaveCredentials(isChecked);
                    if (!isChecked) {
                        // Erase data if checkbox is unclicked
                        mAccountPresenter.setContext(getContext());
                        mAccountPresenter.eraseSavedCredentials();
                    }
                }
            });
            GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
                @Override
                public void failedLogin() {
                    // If the user is still viewing this fragment
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
                    if (model == null) {
                        mDescriptionText.setText("Internal Error\n");
                        mDescriptionText.setTextColor(getResources().getColor(R.color.red));
                        mAccountPresenter.setLoggingIn(false);
                        resumeGUI();
                    }
                    else {
                        mAccountPresenter.setBrbModel(model);
                        mAccountPresenter.setLoggingIn(false);
                        // If user is still viewing this fragment
                        if (getFragmentManager() != null) {
                            loadAccountPage(false);
                        }
                    }
                }
            };
            // Basically, the way we get credentials is to load a specific url on a webview and
            // execute javascript to submit form data with the user's netid and password. Then,
            // we retrieve a session_id from the resulting url
            MainActivity.sLoginWebView.getSettings().setJavaScriptEnabled(true);
            MainActivity.sLoginWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Gets called on every page redirect - initial redirect is loadurl(...)
                    GetLoginUtilities.loginBrb(url, view, callback);
                }
            });
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mFirebaseAnalytics.logEvent("user_brb_login", null);
                    mAccountPresenter.setLoggingIn(true);
                    loadingGUI();
                    mAccountPresenter.setNetID(mNetID.getText().toString());
                    mAccountPresenter.setPassword(mPassword.getText().toString());
                    // set the context to the context on
                    // which the button was clicked. This context will then be used for file
                    // writing later
                    mAccountPresenter.setContext(getContext());
                    // change the login javascript to have the correct username and password
                    mAccountPresenter.resetLoginJS();

                    MainActivity.sLoginWebView.loadUrl(getString(R.string.getlogin_url));
                }
            });
        }
        return rootView;
    }

    /**
     * This method is called simply to display that the GUI is loading. When the user tries to log
     * in with get, loadingGUI is called. This method disables all textInputs and displays the
     * loading progress bar
     */
    private void loadingGUI() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLoginButton.setBackgroundColor(getResources().getColor(R.color.fadedBlue));
        mLoginButton.setText("");
        mDescriptionText.setTextColor(getResources().getColor(R.color.closed));
        mDescriptionText.setText("Logging in. This may take a minute ...\n");
        mNetID.setEnabled(false);
        mPassword.setEnabled(false);
        mSaveInfoCheck.setEnabled(false);
        mPrivacy.setEnabled(false);
    }

    /*
     * Called to make sure the UI of this page is all in the correct display for the user to
     * interact with. Called once if the page is not loading anyone, and called once if the
     * user failed to log in
     */
    private void resumeGUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                mLoginButton.setBackgroundColor(getResources().getColor(R.color.blue));
                mLoginButton.setText(R.string.login_label);
                mNetID.setEnabled(true);
                mPassword.setEnabled(true);
                mPrivacy.setEnabled(true);
                mSaveInfoCheck.setEnabled(true);
            }
        });
    }

    /*
     * @requires alreadyLoggedIn - if in this session of the app the user has already logged in
     * (if there already exists a BrbInfoModel). If alreadyLoggedIn==true then there is no need to
     * save/erase credentials. The other condition would be the first time the user puts in
     * their data or gets logged in
     * This method loads the accountInfoPage
     */
    private void loadAccountPage(boolean alreadyLoggedIn) {
        if (getFragmentManager().findFragmentById(
                R.id.frame_fragment_holder) instanceof LoginFragment) {
            if (!alreadyLoggedIn) {
                // If the user manually clicked login, then save credentials or erase credentials
                if (mAccountPresenter.getSaveCredentials()) {
                    mAccountPresenter.outputCredentialsToFile();
                } else {
                    mAccountPresenter.eraseSavedCredentials();
                }
            }
            // If the user was already logged in (just navigated with bottom view)
            // don't do anything with file writing/erasing
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_fragment_holder, accountInfoFragment).commit();
        }
    }

    // Called from auto-logging in on app launch. Need to be able to notify this page that
    // it is currently trying to log in
    public void setLoading(boolean loggingIn) {
        mAccountPresenter.setLoggingIn(loggingIn);
    }

}
