package com.cornellappdev.android.eatery.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cornellappdev.android.eatery.R;

public class OnboardingFragment extends Fragment {
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context currContext = getContext();
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        // Initiate viewPager, which holds the fragments to scroll between.
        viewPager = view.findViewById(R.id.onboarding_viewpager);
        viewPager.setAdapter(new OnboardingPagerAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(0);
        return view;
    }

    /**
     * getNextOnboardingPagerItem() increments the viewed onboarding item by one, if a next one exists.
     * For example, moves "Collegetown" to "Transactions" onboarding.
     */
    public void getNextOnboardingPagerItem() {
        int pageIndex = viewPager.getCurrentItem();
        if (pageIndex < 4) {
            pageIndex ++;
        }
        viewPager.setCurrentItem(pageIndex);
    }
}