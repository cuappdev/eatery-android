package com.cornellappdev.android.eatery.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;
import com.google.android.material.tabs.TabLayout;

public class OnboardingActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private TabLayout mOnboardingPageIndicator;
    private boolean isLoggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        // Initiate viewPager, which holds the fragments to scroll between.
        mViewPager = findViewById(R.id.onboarding_viewpager);
        mViewPager.setAdapter(new OnboardingPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Intentionally empty, must be overridden to compile.
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                // Intentionally empty, must be overridden to compile.
            }
            @Override
            public void onPageSelected(int position) {
                ((OnboardingPagerAdapter)mViewPager.getAdapter()).onPageSelected(position);
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Must be overridden.
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                // Must be overridden.
            }
            @Override
            public void onPageSelected(int position) {
                ((OnboardingPagerAdapter)mViewPager.getAdapter()).onPageSelected(position);
            }
        });

        mOnboardingPageIndicator = findViewById(R.id.onboarding_page_indicator);
        mOnboardingPageIndicator.setTabRippleColor(null);
        mOnboardingPageIndicator.setupWithViewPager(mViewPager);
    }

    /**
     * getNextOnboardingPagerItem() increments the viewed onboarding item by one, if a next one exists.
     * For example, moves "Collegetown" to "Transactions" onboarding.
     */
    public void getNextOnboardingPagerItem() {
        int pageIndex = mViewPager.getCurrentItem();
        if (pageIndex < 3) {
            pageIndex ++;
        }
        mViewPager.setCurrentItem(pageIndex);
    }

    public void endOnboarding() {
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        preferences.edit().putBoolean("onboarding_complete", true).apply();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void setLoggingIn(boolean loggingIn) {
        this.isLoggingIn = loggingIn;
    }

    public boolean getLoggingIn() {
        return this.isLoggingIn;
    }

}