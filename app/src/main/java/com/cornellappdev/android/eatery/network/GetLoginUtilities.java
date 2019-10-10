package com.cornellappdev.android.eatery.network;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.enums.CacheType;
import com.cornellappdev.android.eatery.util.AccountManagerUtil;
import com.cornellappdev.android.eatery.util.InternalStorage;

import java.io.IOException;

public class GetLoginUtilities {
    private static boolean mEvaluatedJS = false;
    private static String loginJS;

    public static void resetLoginAbility(String netid, String password) {
        mEvaluatedJS = false;
        loginJS = "document.getElementById('netid').value = '" + netid + "';" +
                "document.getElementById('password').value = '" + password + "';" +
                "document.getElementsByName('login')[0].submit();";
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }

    public static void autoLogin(Context c, WebView sLoginWebView) {
        try {
            BrbInfoModel model = (BrbInfoModel) InternalStorage.readObject(c, CacheType.BRB);
            Repository.getInstance().setBrbInfoModel(model);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Callback for successful autologin on launch
        GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
            @Override
            public void failedLogin() {
                // Don't do anything on auto login fail
            }

            @Override
            public void successLogin(com.cornellappdev.android.eatery.BrbInfoQuery.AccountInfo accountInfo) {
                BrbInfoModel model = JsonUtilities.parseBrbInfo(accountInfo);
                Repository.getInstance().setBrbInfoModel(model);
                try {
                    // Cache data internally
                    InternalStorage.writeObject(c, CacheType.BRB, model);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        };

        sLoginWebView.getSettings().setJavaScriptEnabled(true);
        String[] fileData = AccountManagerUtil.readSavedCredentials(c);
        if (fileData != null) { // Automatically log into user's account if file exists
            // A nonexistent file (fileData == null) means that the user has specified they do not
            // want to save data
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
            loadedPage.evaluateJavascript(loginJS, (String s) -> {
                mEvaluatedJS = true;
            });
        } else {
            if (url.contains("sessionId=")) {
                String sessionId = url.substring(url.indexOf("sessionId=") + "sessionId=".length());
                NetworkUtilities.getBrbInfo(sessionId, new NetworkUtilities.BRBAccountCallback() {
                    @Override
                    public void retrievedAccountInfo(BrbInfoQuery.AccountInfo accountInfo) {
                        callback.successLogin(accountInfo);
                    }
                });
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
