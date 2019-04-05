package com.cornellappdev.android.eatery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    public static boolean JSON_FALLBACK = false;
    // Should never be displayed, used to retrieve session_id from Cornell web page
    public static WebView sLoginWebView;
    public BottomNavigationView bnv;
    public CafeteriaDbHelper dbHelper;
    LoginFragment loginFragment;
    private MainPresenter presenter;
    public Button mCollegetownSwitchButton;
    private MainListFragment mainListFragment;
    private WeeklyMenuFragment weeklyMenuFragment;
    private AboutFragment aboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter();
        dbHelper = new CafeteriaDbHelper(this);
        mainListFragment = new MainListFragment();
        weeklyMenuFragment = new WeeklyMenuFragment();
        mCollegetownSwitchButton = findViewById(R.id.collegetown_switch_button);
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
                // If the user is viewing the loginFragment
                if (getSupportFragmentManager().findFragmentById(
                        R.id.frame_fragment_holder) instanceof LoginFragment) {
                    FragmentTransaction transaction =
                            getSupportFragmentManager().beginTransaction();
                    transaction
                            .replace(R.id.frame_fragment_holder, new AccountInfoFragment())
                            .commit();
                }
            }
        };

        // Removed saved data from webview with CookeManager
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        sLoginWebView = findViewById(R.id.login_webview);
        sLoginWebView.getSettings().setJavaScriptEnabled(true);
        String[] fileData = AccountManagerUtil.readSavedCredentials(getApplicationContext());
        if (fileData != null) { // Automatically log into user's account if file exists
            // A nonexistent file (fileData == null) means that the user has specified they do not
            // want to save data
            loginFragment.setLoading(true);
            GetLoginUtilities.resetLoginAbility(fileData[0], fileData[1]);
            sLoginWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Gets executed on every page redirect. Only want to evaluate JS once
                    Log.i("Loading page",url);
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
                        FragmentTransaction transaction =
                                getSupportFragmentManager().beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                transaction
                                        .replace(R.id.frame_fragment_holder, mainListFragment)
                                        .commit();
                                break;
                            case R.id.action_week:
                                transaction
                                        .replace(R.id.frame_fragment_holder, weeklyMenuFragment)
                                        .commit();
                                break;
                            case R.id.action_brb:
                                transaction
                                        .replace(R.id.frame_fragment_holder, loginFragment)
                                        .commit();
                                break;
                        }
                        return true;
                    }
                });

        // Try pulling data from GraphQL, if not fallback to json from cornell dining
        NetworkUtilities.getEateries(this);
        if (JSON_FALLBACK) {
            new ProcessJson().execute("");
        }

        // Disable switch button if failed to retrieve data for collegetown eateries
        NetworkUtilities.getCtEateries(this);
        if (NetworkUtilities.collegetownEateriesLoaded) {
            mCollegetownSwitchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainListFragment.handleCollegetownSwitchButtonPress();
                }
            });
        } else {
            mCollegetownSwitchButton.setEnabled(false);
        }
    }

    public void setLoginInstance(LoginFragment instance) {
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
                    (ConnectivityManager) getApplicationContext().getSystemService(
                            Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (!isConnected
                    && JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext())
                    != null) {

                eateryList = JsonUtilities.parseJson(dbHelper.getLastRow(),
                        getApplicationContext());
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
