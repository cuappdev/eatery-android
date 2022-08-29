package com.cornellappdev.android.eatery.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.cornellappdev.android.eatery.model.enums.OnboardingPageType;

public class OnboardingPagerAdapter extends FragmentStateAdapter {
    private OnboardingInfoFragment menusFragment;
    private OnboardingInfoFragment transactionsFragment;
    private OnboardingInfoFragment loginFragment;

    OnboardingPagerAdapter(FragmentManager fm, Lifecycle lifecycle) {
        super(fm, lifecycle);
        menusFragment = new OnboardingInfoFragment(OnboardingPageType.MENUS);
        transactionsFragment = new OnboardingInfoFragment(OnboardingPageType.TRANSACTIONS);
        loginFragment = new OnboardingInfoFragment(OnboardingPageType.LOGIN);
    }

    void onPageSelected(int position) {
        switch (position) {
            case 0:
                menusFragment.reloadAnimation();
                return;
            case 1:
                transactionsFragment.reloadAnimation();
                return;
            case 2:
                return;
            default: return;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return transactionsFragment;
            case 2:
                return loginFragment;
            default:
                return menusFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
