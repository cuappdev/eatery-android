package com.cornellappdev.android.eatery.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends FragmentActivity {
    private ViewPager2 mViewPager;
    private boolean isLoggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        // Initiate viewPager, which holds the fragments to scroll between.
        mViewPager = findViewById(R.id.onboarding_viewpager);
        mViewPager.setAdapter(new OnboardingPagerAdapter(getSupportFragmentManager(), getLifecycle()));
        mViewPager.setCurrentItem(0);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (mViewPager.getAdapter() instanceof OnboardingPagerAdapter) {
                    ((OnboardingPagerAdapter) mViewPager.getAdapter()).onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        TabLayout mOnboardingPageIndicator = findViewById(R.id.onboarding_page_indicator);
        mOnboardingPageIndicator.setTabRippleColor(null);
        new TabLayoutMediator(mOnboardingPageIndicator, mViewPager,
                (tab, position) -> {
                }
        ).attach();
    }

    /**
     * getNextOnboardingPagerItem() increments the viewed onboarding item by one, if a next one exists.
     * For example, moves "Collegetown" to "Transactions" onboarding.
     */
    public void getNextOnboardingPagerItem() {
        int pageIndex = mViewPager.getCurrentItem();
        if (pageIndex < 1) {
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
