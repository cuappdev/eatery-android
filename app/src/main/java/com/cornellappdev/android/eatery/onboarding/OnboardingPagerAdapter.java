package com.cornellappdev.android.eatery.onboarding;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cornellappdev.android.eatery.model.enums.OnboardingPageType;

public class OnboardingPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

    public OnboardingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnboardingInfoFragment(OnboardingPageType.MENUS);
            case 1:
                return new OnboardingInfoFragment(OnboardingPageType.COLLEGETOWN);
            case 2:
                return new OnboardingInfoFragment(OnboardingPageType.TRANSACTIONS);
            case 3:
                return new OnboardingInfoFragment(OnboardingPageType.LOGIN);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
