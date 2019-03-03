package com.cornellappdev.android.eatery;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private TextView mDescriptionText;
    private Button mLoginButton;
    private int num_redirects;
    private EditText mNetID;
    private EditText mPassword;
    private WebView mGetLoginWebView; // Should never be displayed, methods just used to auto-submit form for session_id
    private FrameLayout mFrameLayout;
    @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_login, container, false);
    getActivity().setTitle("Login");

    mDescriptionText = rootView.findViewById(R.id.descriptionText);
    mLoginButton = rootView.findViewById(R.id.login);
    mNetID = rootView.findViewById(R.id.net_id_input);
    mPassword = rootView.findViewById(R.id.password_input);
    mFrameLayout = rootView.findViewById(R.id.frameLayout);
    mGetLoginWebView = rootView.findViewById(R.id.invisWebView);
    mGetLoginWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                if (mGetLoginWebView != null) {
                    mGetLoginWebView.clearHistory();
                    mGetLoginWebView.clearCache(true);
                }

                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
                mGetLoginWebView.clearHistory();
                mGetLoginWebView.clearCache(true);

                num_redirects = 0;

                // Because the session_id will exist sometimes (lasts 30 min right?),
                // this js code will try and set the value of fields that won't exist as the WebView
                // will have automatically redirected to the page with the session_id. Do not think this is
                // something that should be worried about. HOW DO I CLEAR CACHE??????
                String netid = mNetID.getText().toString();
                String password = mPassword.getText().toString();
                final String loginJS = "document.getElementById('netid').value = '"+netid+"';" +
                        "document.getElementById('password').value = '"+password+"';" +
                        "document.getElementsByName('login')[0].submit();";
                mGetLoginWebView.loadUrl("about:blank");
                mGetLoginWebView.loadUrl("https://get.cbord.com/cornell/full/login.php?mobileapp=1");
                mGetLoginWebView.getSettings().setJavaScriptEnabled(true);
                mGetLoginWebView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload){
                        super.doUpdateVisitedHistory(view, url, isReload);
                        view.clearHistory();
                    }

                    @Override
                    public void onPageFinished(WebView view, String url){
                        if(num_redirects==0) {
                            num_redirects += 1;
                            view.evaluateJavascript(loginJS, (String nothing_returned) ->{});
                            //evaluates javascript

                        }
                        else if(num_redirects >= 1) {
                            num_redirects +=1;
                            if (url.contains("sessionId=")) {

                                // Then I get the updated url containing the session_id after this submission]
                                String getUrl = mGetLoginWebView.getUrl();
                                Log.i("LOGIN_INFO", "SUCCESS "+num_redirects);

                                //parses session_id from url
                                String sessionId = getUrl.substring(getUrl.indexOf("sessionId=") + 10);
                                Log.i("Session_id", sessionId);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                                //tried clearing cookies again
                                CookieManager.getInstance().removeAllCookies(null);
                                CookieManager.getInstance().flush();

                                AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
                                Bundle args = new Bundle();
                                args.putString("sessionId", sessionId);
                                accountInfoFragment.setArguments(args);

                                transaction
                                        .replace(R.id.frame_fragment_holder, accountInfoFragment)
                                        .commit();

                            }else{
                                String checkJS = "(function() { var element = document.getElementById('netid'); " +
                                        "if(element) return 'LOGINFAIL'; else return 'LOGINSUCCESS'; })()";
                                //checks if the page still has an element by the id 'netid'. If it does return 'netid'
                                view.evaluateJavascript(checkJS, (String returned) ->
                                {
                                    //javascript returns strings in the format "string" not just string
                                    if(returned.equals("\"LOGINFAIL\"")) {
                                        Log.i("LOGIN_INFO", "FAILURE");
                                        mDescriptionText.setText("Incorrect netid and/or password");
                                        mDescriptionText.setTextColor(getResources().getColor(R.color.red));
                                    }
                                });
                            }
                        }

                    }
                });
            }
        });

    return rootView;
  }
}
