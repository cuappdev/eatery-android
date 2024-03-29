package com.cornellappdev.android.eatery.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.loginviews.LoginFragment;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.enums.CacheType;
import com.cornellappdev.android.eatery.util.AccountManagerUtil;
import com.cornellappdev.android.eatery.util.InternalStorage;

import java.io.IOException;

/*
General flow of how get login works on Eatery:

1) Call resetLoginAbility with the user's username and password, which will update loginJS
2) Load Get Login's URL in a hidden webview within the app
3) After that page loads once, use the JS to log in (set mEvaluatedJS to true)
4) After the page loads again, parse the sessionID from URL and use that to query the backend
 */

@SuppressLint("SetJavaScriptEnabled")
public class GetLoginUtilities {
    private static boolean mEvaluatedJS = false;
    private static String loginJS;

    // Must call this before navigating to the GET site in the first place
    public static void resetLoginAbility(String netid, String password) {
        mEvaluatedJS = false;
        loginJS = "document.getElementsByName('j_username')[0].value = '" + netid + "';" +
                "document.getElementsByName('j_password')[0].value = '" + password + "';" +
                "document.getElementsByName('_eventId_proceed')[0].click();";
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }

    public static void autoLogin(Activity activity, Context c, WebView sLoginWebView,
                                 LoginFragment loginFragment) {
        // Callback for successful autologin on launch
        GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
            @Override
            public void failedLogin() {
                activity.runOnUiThread(() -> {
                    loginFragment.setAccountFetchData(false);
                });
                // Don't do anything on auto login fail
            }

            @Override
            public void successLogin(
                    com.cornellappdev.android.eatery.BrbInfoQuery.AccountInfo accountInfo) {
                BrbInfoModel model = QueryUtilities.parseBrbInfo(accountInfo);
                Repository.getInstance().setBrbInfoModel(model);
                try {
                    // Cache data internally
                    InternalStorage.writeObject(c, CacheType.BRB, model);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(() -> {
                    loginFragment.setAccountFetchData(false);
                });
            }
        };

        sLoginWebView.getSettings().setJavaScriptEnabled(true);
        String[] fileData = AccountManagerUtil.readSavedCredentials(c);
        if (fileData == null) {
            // clear the cached data if the users account is null
            try {
                InternalStorage.writeObject(c, CacheType.BRB, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // Automatically log into user's account if file exists
            // A nonexistent file (fileData == null) means that the user has specified they do not
            // want to save data
            activity.runOnUiThread(() -> {
                loginFragment.setAccountFetchData(true);
            });
            GetLoginUtilities.resetLoginAbility(fileData[0], fileData[1]);
            sLoginWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Gets executed on every page redirect. Only want to evaluate JS once
                    GetLoginUtilities.webLogin(url, view, callback);
                }
            });
            sLoginWebView.loadUrl("https://get.cbord.com/cornell/full/login.php?mobileapp=1");
        }
    }

    public static void webLogin(String url, WebView loadedPage,
                                GetLoginUtilities.getLoginCallback callback) {
        if (!mEvaluatedJS) {
            loadedPage.evaluateJavascript(loginJS, (String s) -> mEvaluatedJS = true);
        } else {
            if (url.contains("sessionId=")) {
                String sessionId = url.substring(url.indexOf("sessionId=") + "sessionId=".length());
                NetworkUtilities.getBrbInfo(sessionId, callback::successLogin);
            } else {
                String checkJS = "(function() { var element = document.getElementById('netid'); " +
                        "if(element) return 'LOGINFAIL'; else return 'LOGINSUCCESS'; })()";
                // Checks if the page still has an element by the id 'netid'. If it does return
                // 'LOGINFAIL'
                loadedPage.evaluateJavascript(checkJS, (String returned) ->
                {
                    if (returned.equals("\"LOGINFAIL\"")) {
                        callback.failedLogin();
                    }
                });
            }
        }
    }

    public interface getLoginCallback {

        void failedLogin();

        void successLogin(BrbInfoQuery.AccountInfo accountInfo);
    }
}
