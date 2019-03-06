package com.cornellappdev.android.eatery;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private TextView mDescriptionText;
    private Button mLoginButton;
    private EditText mNetID;
    private EditText mPassword;
    private WebView mGetLoginWebView; // Should never be displayed, methods just used to auto-submit form for session_id
    private CheckBox mCheckBox;
    private ProgressBar mProgressBar;
    private boolean mEvaluatedJS = false;
    private String loginJS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");
        if(Repository.getInstance().getBrbInfoModel()!=null){
            loadNextPage(Repository.getInstance().getBrbInfoModel());
            return rootView;
        }
        mDescriptionText = rootView.findViewById(R.id.descriptionText);
        mLoginButton = rootView.findViewById(R.id.login);
        mNetID = rootView.findViewById(R.id.net_id_input);
        mPassword = rootView.findViewById(R.id.password_input);
        mCheckBox = rootView.findViewById(R.id.checkbox_save);
        mProgressBar = rootView.findViewById(R.id.progress_loader);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.getIndeterminateDrawable().setColorFilter(0xffffffff,
                android.graphics.PorterDuff.Mode.MULTIPLY);
        mGetLoginWebView = rootView.findViewById(R.id.invisWebView);

        readSavedCredentials();

        // When checkbox is unclicked, remove the contents of the file
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    eraseSavedCredentials();
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mLoginButton.setBackgroundColor(getResources().getColor(R.color.fadedblue));
                mLoginButton.setText("");

                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();

                String netid = mNetID.getText().toString();
                String password = mPassword.getText().toString();

                loginJS = "document.getElementById('netid').value = '" + netid + "';" +
                        "document.getElementById('password').value = '" + password + "';" +
                        "document.getElementsByName('login')[0].submit();";
                if (mCheckBox.isChecked()) {
                    outputCredentialsToFile(netid, password);
                }

                mEvaluatedJS = false;
                mGetLoginWebView.loadUrl("https://get.cbord.com/cornell/full/login.php?mobileapp=1");
            }
        });

        mGetLoginWebView.getSettings().setJavaScriptEnabled(true);
        mGetLoginWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // Gets executed on every page redirect. Only want to evaluate JS once
                if (!mEvaluatedJS) {
                    view.evaluateJavascript(loginJS, (String s) -> {
                        mEvaluatedJS = true;
                    });
                } else {
                    if (url.contains("sessionId=")) {
                        String getUrl = mGetLoginWebView.getUrl();
                        String sessionId = getUrl.substring(getUrl.indexOf("sessionId=") + "sessionId=".length());
                        NetworkUtilities.getBrbInfo(sessionId, new NetworkUtilities.BRBAccountCallback() {
                            @Override
                            public void retrievedAccountInfo(BrbInfoQuery.AccountInfo accountInfo) {
                                getActivity().runOnUiThread(() -> {
                                    BrbInfoModel model = JsonUtilities.parseBrbInfo(accountInfo);
                                    loadNextPage(model);
                                });
                            }
                        });

                    } else {
                        String checkJS = "(function() { var element = document.getElementById('netid'); " +
                                "if(element) return 'LOGINFAIL'; else return 'LOGINSUCCESS'; })()";
                        // Checks if the page still has an element by the id 'netid'. If it does return 'LOGINFAIL'
                        view.evaluateJavascript(checkJS, (String returned) ->
                        {
                            if (returned.equals("\"LOGINFAIL\"")) {
                                mDescriptionText.setText("Incorrect netid and/or password");
                                mDescriptionText.setTextColor(getResources().getColor(R.color.red));
                                mProgressBar.setVisibility(View.INVISIBLE);
                                mLoginButton.setBackgroundColor(getResources().getColor(R.color.blue));
                                mLoginButton.setText("Login");
                            }
                        });
                    }
                }

            }
        });
        return rootView;
    }

    private void readSavedCredentials() {
        FileInputStream inputStream;
        String fileContents = "";
        try {
            inputStream = getContext().openFileInput("saved_data");
            byte nextByte = -1;
            while (inputStream.available() > 0) {
                nextByte = (byte) inputStream.read();
                fileContents += (char) nextByte;
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileContents.indexOf('\n') > 0) {
            mCheckBox.setChecked(true);
            String temp_netid = fileContents.substring(0, fileContents.indexOf('\n'));
            String temp_pass = fileContents.substring(fileContents.indexOf('\n') + 1);
            mNetID.setText(temp_netid);
            mPassword.setText(temp_pass);
        }
    }

    private void outputCredentialsToFile(String netid, String pass) {
        FileOutputStream outputStream;
        try {
            outputStream = getContext().openFileOutput("saved_data", Context.MODE_PRIVATE);
            outputStream.write(netid.getBytes());
            outputStream.write('\n');
            outputStream.write(pass.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eraseSavedCredentials() {
        FileOutputStream outputStream;
        try {
            outputStream = getContext().openFileOutput("saved_data", Context.MODE_PRIVATE);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNextPage(BrbInfoModel model) {
        Repository.getInstance().setBrbInfoModel(model);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
        transaction
                .replace(R.id.frame_fragment_holder, accountInfoFragment)
                .commit();
    }


}
