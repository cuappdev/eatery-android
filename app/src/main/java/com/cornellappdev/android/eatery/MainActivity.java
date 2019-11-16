package com.cornellappdev.android.eatery;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.cornellappdev.android.eatery.loginviews.LoginFragment;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CacheType;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import com.cornellappdev.android.eatery.onboarding.OnboardingFragment;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;
import com.cornellappdev.android.eatery.presenter.MainPresenter;
import com.cornellappdev.android.eatery.util.InternalStorage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    // Should never be displayed, used to retrieve session_id from Cornell web page
    public static WebView sLoginWebView;
    public BottomNavigationView bnv;
    private FirebaseAnalytics mFirebaseAnalytics;
    private LoginFragment loginFragment;
    private MainPresenter presenter;
    private MainListFragment mainListFragment;
    private WeeklyMenuFragment weeklyMenuFragment;

    private AccountPresenter mAccountPresenter = new AccountPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter();
        mainListFragment = new MainListFragment();
        weeklyMenuFragment = new WeeklyMenuFragment();
        loginFragment = new LoginFragment();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sLoginWebView = findViewById(R.id.login_webview);
        GetLoginUtilities.autoLogin(getApplicationContext(), sLoginWebView);

        presenter = new MainPresenter();
        bnv = findViewById(R.id.bottom_navigation);
        // Add functionality to bottom nav bar

        try {
            BrbInfoModel brbModel = (BrbInfoModel) InternalStorage.readObject(getApplicationContext(), CacheType.BRB);
            Repository.getInstance().setBrbInfoModel(brbModel);
            ArrayList<EateryBaseModel> campusEateries = (ArrayList<EateryBaseModel>) InternalStorage
                    .readObject(getApplicationContext(), CacheType.CAMPUS_EATERY);
            Repository.getInstance().setEateryList(campusEateries);
            ArrayList<EateryBaseModel> ctownEateries = (ArrayList<EateryBaseModel>) InternalStorage
                    .readObject(getApplicationContext(), CacheType.CTOWN_EATERY);
            Repository.getInstance().setCtEateryList(ctownEateries);
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
        }
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction =
                                getSupportFragmentManager().beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                mFirebaseAnalytics.logEvent("eatery_tab_press", null);
                                transaction
                                        .replace(R.id.frame_fragment_holder, mainListFragment)
                                        .commit();
                                break;
                            case R.id.action_week:
                                mFirebaseAnalytics.logEvent("lookahead_tab_press", null);
                                transaction
                                        .replace(R.id.frame_fragment_holder, weeklyMenuFragment)
                                        .commit();
                                break;
                            case R.id.action_brb:
                                mFirebaseAnalytics.logEvent("brb_tab_press", null);
                                transaction
                                        .replace(R.id.frame_fragment_holder, loginFragment)
                                        .commit();
                                break;
                        }
                        return true;
                    }
                });

        // TODO (yanlam): Check if onboarding has occurred, and skip this.
        startOnboarding();

        // Try pulling data from GraphQL
        NetworkUtilities.getEateries(this, mainListFragment);
        NetworkUtilities.getCtEateries(this);

        // The first time a map is loaded in the app, the app automatically takes time to initialize
        // google play services apis. We load it here at the beginning of the app
        MapView mDummyMapInitializer = findViewById(R.id.dummy_map);
        mDummyMapInitializer.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
            }
        });

    }

    public boolean isAccountPresenterLoggedIn() {
        return mAccountPresenter.isLoggedIn();
    }

    public boolean isAccountPresenterLoggingIn() {
        return mAccountPresenter.isLoggingIn();
    }

    public String[] getLoginInfo() {
        String[] loginInfo = mAccountPresenter.readSavedCredentials(getApplicationContext());
        return loginInfo;
    }

    public void setAccountPresenterLoggingIn(boolean b) {
        mAccountPresenter.setLoggingIn(b);
    }

    public void setAccountPresenterBrbInfo(BrbInfoModel model) {
        mAccountPresenter.setBrbModel(model);
    }

    public void setAccountPresenterFields(String netId, String password) {
        mAccountPresenter.setNetID(netId);
        mAccountPresenter.setPassword(password);
    }

    public void resetAccountPresenterJS() {
        mAccountPresenter.resetLoginJS();
    }

    public void outputAccountPresenterCredentialsToFile() {
        mAccountPresenter.outputCredentialsToFile(getApplicationContext());
    }

    public void startOnboarding() {
        FrameLayout frameLayout = findViewById(R.id.frame_fragment_holder);
        RelativeLayout.LayoutParams frameLayoutParams = (RelativeLayout.LayoutParams)frameLayout.getLayoutParams();
        frameLayoutParams.setMargins(0, 0, 0, 0);
        frameLayout.setLayoutParams(frameLayoutParams);
        frameLayout.requestLayout();

        OnboardingFragment onboardingFragment = new OnboardingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment_holder,
                onboardingFragment).commit();
        getSupportActionBar().hide();
        bnv.setVisibility(View.GONE);
    }

    public void endOnboarding() {
        FrameLayout frameLayout = findViewById(R.id.frame_fragment_holder);
        RelativeLayout.LayoutParams frameLayoutParams = (RelativeLayout.LayoutParams)frameLayout.getLayoutParams();
        frameLayoutParams.setMargins(0, 0, 0, bnv.getMinimumHeight());
        frameLayout.setLayoutParams(frameLayoutParams);
        frameLayout.requestLayout();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment_holder,
                mainListFragment).commit();
        getSupportActionBar().show();
        bnv.setVisibility(View.VISIBLE);
    }

    public void setLoginInstance(LoginFragment instance) {
        loginFragment = instance;
    }

}
