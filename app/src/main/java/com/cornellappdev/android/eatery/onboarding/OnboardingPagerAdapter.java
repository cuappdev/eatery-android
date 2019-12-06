package com.cornellappdev.android.eatery.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cornellappdev.android.eatery.model.enums.OnboardingPageType;

public class OnboardingPagerAdapter extends FragmentPagerAdapter {
    private OnboardingInfoFragment menusFragment;
    private OnboardingInfoFragment collegetownFragment;
    private OnboardingInfoFragment transactionsFragment;
    private OnboardingInfoFragment loginFragment;

    OnboardingPagerAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        menusFragment = new OnboardingInfoFragment(OnboardingPageType.MENUS);
        collegetownFragment = new OnboardingInfoFragment(OnboardingPageType.COLLEGETOWN);
        transactionsFragment = new OnboardingInfoFragment(OnboardingPageType.TRANSACTIONS);
        loginFragment = new OnboardingInfoFragment(OnboardingPageType.LOGIN);
    }

    void onPageSelected(int position) {
        switch (position) {
            case 0: menusFragment.reloadAnimation(); return;
            case 1: collegetownFragment.reloadAnimation(); return;
            case 2: transactionsFragment.reloadAnimation(); return;
            case 3: return;
            default: return;
        }
    }

    @Override
    @NonNull
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
                return menusFragment;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
