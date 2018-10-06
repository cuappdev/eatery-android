package com.cornellappdev.android.eatery.page;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.cornellappdev.android.eatery.page.eateries.EateriesFragment;
import com.cornellappdev.android.eatery.page.menu.WeeklyMenuFragment;
import java.lang.ref.WeakReference;

public class EateryPagerAdapter extends FragmentPagerAdapter {

  private final static int NUMBER_OF_TABS = 2;
  private static final String TAG = "EateryPagerAdapter";
  private WeakReference<EateryTabFragment> eateries, weeklyMenu;

  public EateryPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public EateryTabFragment getItem(int position) {
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

  private EateryTabFragment getTabFragmentInstance(int page) {
    EateryTabFragment fragment = null;

    WeakReference<EateryTabFragment> ref = null;

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
          break;
        default:
      }
    }

    return fragment;
  }

  public void forAllItems(ForAllItems forAllItems) {
    forAllItems.action(getItem(Page.EATERIES));
    forAllItems.action(getItem(Page.WEEKLY_MENU));
  }

  public interface ForAllItems {
    void action(EateryTabFragment fragment);
  }
}