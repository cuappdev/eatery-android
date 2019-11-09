package com.cornellappdev.android.eatery.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cornellappdev.android.eatery.CampusMenuActivity;
import com.cornellappdev.android.eatery.R;
import com.google.android.material.tabs.TabLayout;

public class OnboardingFragment extends Fragment {
    private ViewPager mViewPager;
//    private View mPageIndicatorMenus;
//    private View mPageIndicatorCollegetown;
//    private View mPageIndicatorTransactions;
//    private View mPageIndicatorLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        // Initiate viewPager, which holds the fragments to scroll between.
        mViewPager = view.findViewById(R.id.onboarding_viewpager);
        mViewPager.setAdapter(new OnboardingPagerAdapter(getChildFragmentManager()));
        mViewPager.setCurrentItem(0);

//        mPageIndicatorMenus = view.findViewById(R.id.onboarding_page_indicator_menus);
//        mPageIndicatorCollegetown = view.findViewById(R.id.onboarding_page_indicator_collegetown);
//        mPageIndicatorTransactions = view.findViewById(R.id.onboarding_page_indicator_transactions);
//        mPageIndicatorLogin = view.findViewById(R.id.onboarding_page_indicator_login);

//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.onboarding_page_indicator);
//        tabLayout.setupWithViewPager(mViewPager, true);



//        updatePageIndicator();

        return view;
    }
//
//    private void updatePageIndicator () {
//        mPageIndicatorMenus.setAlpha((float) 0.30);
//        mPageIndicatorCollegetown.setAlpha((float) 0.30);
//        mPageIndicatorTransactions.setAlpha((float) 0.30);
//        mPageIndicatorLogin.setAlpha((float) 0.30);
//        int pageIndex = mViewPager.getCurrentItem();
//        switch (pageIndex) {
//            case 0:
//                mPageIndicatorMenus.setAlpha((float) 1);
//                break;
//            case 1:
//                mPageIndicatorCollegetown.setAlpha((float) 1);
//                break;
//            case 2:
//                mPageIndicatorTransactions.setAlpha((float) 1);
//                break;
//            case 3:
//                mPageIndicatorLogin.setAlpha((float) 1);
//                break;
//        }
//
//    }

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
//        updatePageIndicator();
    }
}