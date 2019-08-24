package com.cornellappdev.android.eatery.network;

import android.webkit.CookieManager;
import android.webkit.WebView;

import com.cornellappdev.android.eatery.BrbInfoQuery;

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

    public static void loginBrb(String url, WebView loadedPage,
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
