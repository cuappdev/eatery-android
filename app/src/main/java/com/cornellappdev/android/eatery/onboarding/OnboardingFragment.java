package com.cornellappdev.android.eatery.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.R;
import com.google.android.material.tabs.TabLayout;

public class OnboardingFragment extends Fragment {
    private ViewPager mViewPager;
    private TabLayout mOnboardingPageIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        // Initiate viewPager, which holds the fragments to scroll between.
        mViewPager = view.findViewById(R.id.onboarding_viewpager);
        mViewPager.setAdapter(new OnboardingPagerAdapter(getChildFragmentManager()));
        mViewPager.setCurrentItem(0);

        mOnboardingPageIndicator = view.findViewById(R.id.onboarding_page_indicator);
        mOnboardingPageIndicator.setTabRippleColor(null);
        mOnboardingPageIndicator.setupWithViewPager(mViewPager);

        return view;
    }

    /**
     * getNextOnboardingPagerItem() increments the viewed onboarding item by one, if a next one exists.
     * For example, moves "Collegetown" to "Transactions" onboarding.
     */
    public void getNextOnboardingPagerItem() {
        int pageIndex = mViewPager.getCurrentItem();
        if (pageIndex < 4) {
            pageIndex ++;
        }
        mViewPager.setCurrentItem(pageIndex);
    }

    public void endOnboarding() {
        ((MainActivity) getActivity()).endOnboarding();
    }
}