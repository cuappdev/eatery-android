package com.cornellappdev.android.eatery;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.cornellappdev.android.eatery.model.EateryModel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EateryPagerAdapter extends FragmentPagerAdapter {

  private static final String TAG = "EateryPagerAdapter";

  private WeakReference<Fragment> eateries, weeklyMenu;
  private final static int NUMBER_OF_TABS = 2;

  EateryPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case Page.EATERIES:
      case Page.WEEKLY_MENU:
        return getTabFragmentInstance(position);
      default:
        Log.d(TAG, "Attempted to retrieve unknown fragment from EateryPagerAdapter");
        return null;
    }
  }

  @Override
  public int getCount() {
    return NUMBER_OF_TABS;
  }

  @Override
  public void destroyItem(ViewGroup group, int position, Object obj) {
    switch (position) {
      case Page.EATERIES:
        eateries = null;
        break;
      case Page.WEEKLY_MENU:
        weeklyMenu = null;
        break;
      default:
    }

    super.destroyItem(group, position, obj);
  }

  @Override
  public void destroyItem(@NonNull View container, int position, @NonNull Object obj) {
    switch (position) {
      case Page.EATERIES:
        eateries = null;
        break;
      case Page.WEEKLY_MENU:
        weeklyMenu = null;
        break;
      default:
    }

    super.destroyItem(container, position, obj);
  }

  /* Fragment Reference Util Getters */

  private Fragment getTabFragmentInstance(int page) {
    Fragment fragment = null;

    WeakReference<Fragment> ref = null;

    switch (page) {
      case Page.EATERIES:
        ref = eateries;
        break;
      case Page.WEEKLY_MENU:
        ref = weeklyMenu;
        break;
      default:
    }

    if (ref != null) {
      fragment = ref.get();
    }

    if (fragment == null) {
      switch (page) {
        case Page.EATERIES:
          fragment = new EateriesFragment();
          eateries = new WeakReference<>(fragment);
          break;
        case Page.WEEKLY_MENU:
          fragment = new WeeklyMenuFragment();
          weeklyMenu = new WeakReference<>(fragment);

          Fragment frag = this.getItem(Page.EATERIES);

          if (frag instanceof EateriesFragment) {
            List<EateryModel> eateries = ((EateriesFragment) frag).getCurrentEateries();

            ((WeeklyMenuFragment) fragment).updateEateries(eateries);
          }
          break;
        default:
      }
    }

    return fragment;
  }
}