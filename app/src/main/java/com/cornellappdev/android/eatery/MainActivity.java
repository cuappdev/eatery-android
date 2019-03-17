package com.cornellappdev.android.eatery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cornellappdev.android.eatery.data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import com.cornellappdev.android.eatery.presenter.MainPresenter;
import com.cornellappdev.android.eatery.util.AccountManagerUtil;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    private MainPresenter presenter;
    public BottomNavigationView bnv;
    public CafeteriaDbHelper dbHelper;

    public static boolean JSON_FALLBACK = false;

    LoginFragment loginFragment;
    public static WebView sLoginWebView; // Should never be displayed, methods just used to auto-submit form for session_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginFragment = new LoginFragment();

        GetLoginUtilities.getLoginCallback callback = new GetLoginUtilities.getLoginCallback() {
            @Override
            public void failedLogin() {
                loginFragment.setLoading(false);
            }

            @Override
            public void successLogin(BrbInfoQuery.AccountInfo accountInfo) {
                BrbInfoModel model = JsonUtilities.parseBrbInfo(accountInfo);
                Repository.getInstance().setBrbInfoModel(model);
                loginFragment.setLoading(false);
                if (getSupportFragmentManager().findFragmentById(R.id.frame_fragment_holder) instanceof LoginFragment) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction
                            .replace(R.id.frame_fragment_holder, new AccountInfoFragment())
                            .commit();
                }
            }
        };

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        sLoginWebView=findViewById(R.id.login_webview);
        sLoginWebView.getSettings().setJavaScriptEnabled(true);
        String[] fileData = AccountManagerUtil.readSavedCredentials(getApplicationContext());
        if(fileData!=null) {
            loginFragment.setLoading(true);
            GetLoginUtilities.resetLoginAbility(fileData[0], fileData[1]);
            sLoginWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Gets executed on every page redirect. Only want to evaluate JS once
                    GetLoginUtilities.loginBrb(url, view, callback);
                }
            });
            sLoginWebView.loadUrl("https://get.cbord.com/cornell/full/login.php?mobileapp=1");
        }

        presenter = new MainPresenter();
        dbHelper = new CafeteriaDbHelper(this);
        bnv = findViewById(R.id.bottom_navigation);
        // Add functionality to bottom nav bar
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                transaction
                                        .replace(R.id.frame_fragment_holder, new MainListFragment())
                                        .commit();
                                break;
                            case R.id.action_week:
                                transaction
                                        .replace(R.id.frame_fragment_holder, new WeeklyMenuFragment())
                                        .commit();
                                break;
                            case R.id.action_brb:
                                // Because state needs to be saved
                                transaction
                                        .replace(R.id.frame_fragment_holder, loginFragment, "Login")
                                        .commit();
                                break;
                        }
                        return true;
                    }
                });

        // Try pulling data from GraphQL, if not fallback to json from cornell dining
        NetworkUtilities.getEateries(presenter, this);
        if (JSON_FALLBACK) {
            new ProcessJson().execute("");
        }
    }

    public void setLoginInstance(LoginFragment instance){
        loginFragment = instance;
    }

    public class ProcessJson extends AsyncTask<String, Void, ArrayList<EateryBaseModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<EateryBaseModel> doInBackground(String... params) {
            ArrayList<EateryBaseModel> eateryList = new ArrayList<>();
            ConnectivityManager cm =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (!isConnected
                    && JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext()) != null) {
                eateryList = JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext());
            } else {
                String json = NetworkUtilities.getJSON();
                dbHelper.addData(json);
                eateryList = JsonUtilities.parseJson(json, getApplicationContext());
            }
            Collections.sort(eateryList);
            return eateryList;
        }

        @Override
        protected void onPostExecute(ArrayList<EateryBaseModel> result) {
            presenter.setEateryList(result);

            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_fragment_holder, new MainListFragment())
                        .commit();
            } catch (Exception e) {
                super.onPostExecute(result);
            }
        }
    }
}
