package com.cornellappdev.android.eatery;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.MealModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  public BottomNavigationView mBottomNavigationView;

  private ViewPager mViewPager;
  private MenuItem mPrevMenuItem;
  private EateryPagerAdapter mViewPagerAdapter;

  @Override
  public void onResume() {
    super.onResume();
    mBottomNavigationView.setSelectedItemId(R.id.action_eateries);
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_map:
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        Fragment fragment = mViewPagerAdapter.getItem(Page.EATERIES);
        if (fragment instanceof EateriesFragment) {
          List<EateryModel> eateries = ((EateriesFragment) fragment).getCurrentEateries();
          intent.putExtra("mEatery", new ArrayList<>(eateries));
        }

        startActivity(intent);
        return true;
      default:
        // The user's action was not recognized, and invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    setTitle("Eatery");

    mBottomNavigationView = findViewById(R.id.bottom_navigation);
    mViewPager = findViewById(R.id.pager);

    getSupportActionBar().show();

    mViewPagerAdapter = new EateryPagerAdapter(getSupportFragmentManager());
    mViewPager.setAdapter(mViewPagerAdapter);
    mViewPager.setOffscreenPageLimit(2);
    mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavItemSelectedListener);

  }

  /* Listeners */

  private OnNavigationItemSelectedListener mOnNavItemSelectedListener = new OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      switch (item.getItemId()) {
        case R.id.action_weekly_menu:
          mViewPager.setCurrentItem(Page.WEEKLY_MENU);
          break;
        case R.id.action_eateries:
          mViewPager.setCurrentItem(Page.EATERIES);
          break;
        case R.id.action_brb:
          Snackbar snackbar =
              Snackbar.make(
                  findViewById(R.id.main_activity),
                  "If you would like"
                      + " to see this feature, consider joining our Android dev team!",
                  Snackbar.LENGTH_LONG);
          snackbar.setAction("Apply", new SnackBarListener());
          snackbar.show();
          break;
      }
      return true;
    }
  };

  private List<EateryModel> mEateries;
  private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

      switch (position) {
        case Page.EATERIES:
          break;
        case Page.WEEKLY_MENU:
          Fragment fragment = mViewPagerAdapter.getItem(Page.EATERIES);

          if (fragment instanceof EateriesFragment) {
            mEateries = ((EateriesFragment) fragment).getCurrentEateries();
          }

          fragment = mViewPagerAdapter.getItem(Page.WEEKLY_MENU);

          if (fragment instanceof WeeklyMenuFragment) {
            ((WeeklyMenuFragment) fragment).updateEateries(mEateries);
          }
          break;
        default:
      }

      if (mPrevMenuItem != null) {
        mPrevMenuItem.setChecked(false);
      } else {
        /* If no menu item was previously selected, deselect EVERYTHING */
        mBottomNavigationView.getMenu().getItem(Page.EATERIES).setChecked(false);
        mBottomNavigationView.getMenu().getItem(Page.WEEKLY_MENU).setChecked(false);
        mBottomNavigationView.getMenu().getItem(Page.BRB).setChecked(false);
      }

      /* Set currently selected item... */
      mBottomNavigationView.getMenu().getItem(position).setChecked(true);
      mPrevMenuItem = mBottomNavigationView.getMenu().getItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
  };

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    final MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();
    setTitle("Eateries");
    AutoCompleteTextView searchTextView =
        searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
    searchView.setMaxWidth(2000);
    try {
      Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
      mCursorDrawableRes.setAccessible(true);
      mCursorDrawableRes.set(
          searchTextView,
          R.drawable
              .cursor); // This sets the cursor resource ID to 0 or @null which will make it visible
      // on white background
    } catch (Exception e) {
      // Don't do anything
    }

    //searchView.setOnQueryTextListener(queryListener);
    return super.onCreateOptionsMenu(menu);
  }


  public class SnackBarListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      Intent browser =
          new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cornellappdev.com/apply/"));
      startActivity(browser);
    }
  }


}
