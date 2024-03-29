package com.cornellappdev.android.eatery.loginviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import java.io.IOException;

/**
 * This fragment is the login page reached from the bottomnavbar, and is the screen where users
 * input their netid and password to view their account information. Upon a successful login,
 * redirect to AccountInfoFragment
 */

@SuppressLint("SetJavaScriptEnabled")
public class LoginFragment extends Fragment {
    private EditText mNetID;
    private EditText mPassword;
    private TextView mDescriptionText;
    private TextView mPrivacy;
    private Button mLoginButton;
    private ProgressBar mProgressBar;
    private AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        Context currContext = getContext();
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        if (getActivity() != null) {
            getActivity().setTitle("Login");
            ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (bar != null) {
                // Disable the back button
                bar.setDisplayHomeAsUpEnabled(false);
            }
        }

        mainActivity = (MainActivity) getActivity();

        mPrivacy = rootView.findViewById(R.id.privacyStatement);
        mDescriptionText = rootView.findViewById(R.id.descriptionText);
        mLoginButton = rootView.findViewById(R.id.login);
        mNetID = rootView.findViewById(R.id.net_id_input);
        mPassword = rootView.findViewById(R.id.password_input);
        mProgressBar = rootView.findViewById(R.id.progress_loader);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                android.graphics.PorterDuff.Mode.MULTIPLY);

        if (mainActivity.isAccountPresenterLoggedIn()) {
            // If the user has already logged in in this session of the app, just load the
            // accountInfo page and don't require another login
            loadAccountPage(true);
            return rootView;
        }

        if (mainActivity.isAccountPresenterLoggingIn()) {
            String[] loginInfo = mainActivity.getLoginInfo();
            if (loginInfo != null) {
                // This is not null upon auto-logging in on app launch. Thus, set the textfields
                // to the values found in the files
                mNetID.setText(loginInfo[0]);
                mPassword.setText(loginInfo[1]);
            }
            // Always display animation whether logging in automatically or after
            // manually clicking logging in and renavigating to this page
            loadingGUI(currContext);
        } else {
            resumeGUI(currContext);
            mPrivacy.setOnClickListener((View v) -> {
                // On privacy statement clicked
                if (getFragmentManager() != null) {
                    PrivacyFragment privacyFragment = new PrivacyFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_fragment_holder,
                            privacyFragment).addToBackStack(
                            null).commit();
                }
            });
            GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
                @Override
                public void failedLogin() {
                    // If the user is still viewing this fragment
                    if (getFragmentManager() != null) {
                        mDescriptionText.setText("Incorrect netid and/or password\n");
                        if (currContext != null) {
                            mDescriptionText.setTextColor(
                                    ContextCompat.getColor(currContext, R.color.red));
                        }
                        mainActivity.setAccountPresenterLoggingIn(false);
                        resumeGUI(currContext);
                    }
                }

                @Override
                public void successLogin(BrbInfoQuery.AccountInfo accountInfo) {
                    Repository.getInstance().setBrbInfoModel(
                            QueryUtilities.parseBrbInfo(accountInfo));
                    BrbInfoModel model = Repository.getInstance().getBrbInfoModel();
                    try {
                        if (currContext != null) {
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
                        mainActivity.setAccountPresenterLoggingIn(false);
                        resumeGUI(currContext);
                    } else {
                        mainActivity.outputAccountPresenterCredentialsToFile();
                        mainActivity.setAccountPresenterBrbInfo(model);
                        mainActivity.setAccountPresenterLoggingIn(false);
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
                    GetLoginUtilities.webLogin(url, view, callback);
                }
            });
            mLoginButton.setOnClickListener((View v) -> {
                mainActivity.login(mNetID.getText().toString(), mPassword.getText().toString());
                loadingGUI(currContext);
            });
        }
        return rootView;
    }

    public void setAccountFetchData(boolean fetching) {
        accountInfoFragment.setProgressBarVisibility(fetching);
    }

    /**
     * This method is called simply to display that the GUI is loading. When the user tries to log
     * in with get, loadingGUI is called. This method disables all textInputs and displays the
     * loading progress bar
     */
    private void loadingGUI(Context context) {
//        mProgressBar.setVisibility(View.VISIBLE);
        mLoginButton.setBackgroundColor(ContextCompat.getColor(context, R.color.fadedBlue));
        mLoginButton.setText("");
        mDescriptionText.setTextColor(ContextCompat.getColor(context, R.color.primary));
        mDescriptionText.setText("Logging in. This may take a minute ...\n");
        mNetID.setEnabled(false);
        mPassword.setEnabled(false);
        mPrivacy.setEnabled(false);
    }

    /*
     * Called to make sure the UI of this page is all in the correct display for the user to
     * interact with. Called once if the page is not loading anyone, and called once if the
     * user failed to log in
     */
    private void resumeGUI(Context context) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mProgressBar.setVisibility(View.INVISIBLE);
                mLoginButton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                mLoginButton.setText(R.string.login_label);
                mNetID.setEnabled(true);
                mPassword.setEnabled(true);
                mPrivacy.setEnabled(true);
            });
        }
    }

    /*
     * @requires alreadyLoggedIn - if in this session of the app the user has already logged in
     * (if there already exists a BrbInfoModel). If alreadyLoggedIn==true then there is no need to
     * save/erase credentials. The other condition would be the first time the user puts in
     * their data or gets logged in
     * This method loads the accountInfoPage
     */
    private void loadAccountPage(boolean alreadyLoggedIn) {
        if (getFragmentManager() != null && getFragmentManager().findFragmentById(
                R.id.frame_fragment_holder) instanceof LoginFragment) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_fragment_holder, accountInfoFragment).commit();
        }
    }
}
