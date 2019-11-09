package com.cornellappdev.android.eatery.onboarding;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class OnboardingPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

    public OnboardingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnboardingInfoFragment("Menus", "See what’s being served at any campus eatery.");
            case 1:
                return new OnboardingInfoFragment("Collegetown", "Find info about your favorite Collegetown spots.");
            case 2:
                return new OnboardingInfoFragment("Transactions", "Track your swipes, BRBs, meal history, and more.");
            case 3:
                return new OnboardingLoginFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
