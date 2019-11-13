package com.cornellappdev.android.eatery.onboarding;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cornellappdev.android.eatery.model.enums.OnboardingPageType;

public class OnboardingPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;
    private OnboardingInfoFragment menusFragment;
    private OnboardingInfoFragment collegetownFragment;
    private OnboardingInfoFragment transactionsFragment;
    private OnboardingInfoFragment loginFragment;

    public OnboardingPagerAdapter(FragmentManager fm) {
        super(fm);
        menusFragment = new OnboardingInfoFragment(OnboardingPageType.MENUS);
        collegetownFragment = new OnboardingInfoFragment(OnboardingPageType.COLLEGETOWN);
        transactionsFragment = new OnboardingInfoFragment(OnboardingPageType.TRANSACTIONS);
        loginFragment = new OnboardingInfoFragment(OnboardingPageType.LOGIN);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return menusFragment;
            case 1:
                return collegetownFragment;
            case 2:
                return transactionsFragment;
            case 3:
                return loginFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
