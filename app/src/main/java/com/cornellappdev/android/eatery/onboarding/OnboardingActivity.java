package com.cornellappdev.android.eatery.onboarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cornellappdev.android.eatery.R;

public class OnboardingActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

//        mPager = findViewById(R.id.pager);
//        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
//        mPager.setAdapter(pagerAdapter);
    }

//    private void toggleFullscreen(boolean fullscreen) {
//        if (fullscreen) {
//            getSupportActionBar().hide();
//        } else {
//            getSupportActionBar().show();
//        }
//    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new OnboardingScrollView("Menus", "See what’s being served at any campus eatery.");
            } else if (position == 1) {
                return new OnboardingScrollView("Collegetown", "Find info about your favorite Collegetown spots.");
            } else if (position == 2) {
                return new OnboardingScrollView("Transactions", "Track your swipes, BRBs, meal history, and more.");
            } else {
                return new OnboardingScrollView("Login", "To get the most out of Eatery, log in with your netID.");
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}