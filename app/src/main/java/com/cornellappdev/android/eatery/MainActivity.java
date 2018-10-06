package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.cornellappdev.android.eatery.data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.model.CampusArea;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.PaymentMethod;
import com.cornellappdev.android.eatery.network.ConnectionUtilities;
import com.cornellappdev.android.eatery.network.JSONUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final EateryModelFilter<CampusArea> AREA_FILTER = (model, area) -> area == model
      .getArea();
  private static final EateryModelFilter<PaymentMethod> PAYMENT_FILTER = EateryModel::hasPaymentMethod;
  private static final EateryModelFilter<String> SEARCH_FILTER = (model, query) -> {
    String cleanQuery = query.trim().toLowerCase();
    boolean remove = true;
    if (model.getNickName().contains(cleanQuery)) {
      remove = false;
    }

    List<String> mealItems = model.getMealItems();
    for (String mealItem : mealItems) {
      if (mealItem.toLowerCase().contains(cleanQuery)) {
        remove = false;
      }
    }
    return !remove;
  };
  public static boolean searchPressed = false;
  private EateryPagerAdapter mAdapter;
  private BottomNavigationView mBottomNavigationView;
  private CafeteriaDbHelper mDBHelper;
  private List<EateryModel> mEateries; // holds all cafes
  private MenuItem mPrevMenuItem;
  private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
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
  private ProgressBar mProgressBar;
  private RelativeLayout mSplash;
  private ViewPager mViewPager;
  private OnNavigationItemSelectedListener mOnNavItemSelectedListener = new OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      switch (item.getItemId()) {
        case R.id.action_week:
          mViewPager.setCurrentItem(Page.WEEKLY_MENU);
          break;
        case R.id.action_home:
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

  public MainActivity() {
    mEateries = new ArrayList<>();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Eatery");
    setContentView(R.layout.activity_main);
    getSupportActionBar().show();
    mBottomNavigationView = findViewById(R.id.bottom_navigation);
    mViewPager = findViewById(R.id.pager);
    EateryPagerAdapter mViewPagerAdapter = new EateryPagerAdapter(getSupportFragmentManager());
    mAdapter = mViewPagerAdapter;
    mViewPager.setAdapter(mViewPagerAdapter);
    mViewPager.setOffscreenPageLimit(2);
    mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavItemSelectedListener);
    mSplash = findViewById(R.id.splash_layout);
    mProgressBar = findViewById(R.id.progress_bar);
    mDBHelper = new CafeteriaDbHelper(this);
    ConnectionUtilities con = new ConnectionUtilities(this);
    if (!con.isNetworkAvailable()) {
      mEateries = new ArrayList<>();
      if (JSONUtilities.parseJson(mDBHelper.getLastRow(), getApplicationContext()) != null) {
        mEateries = JSONUtilities.parseJson(mDBHelper.getLastRow(), getApplicationContext());
      }
      Collections.sort(mEateries);

      mAdapter.forAllItems((fragment) -> {
        fragment.onDataLoaded(Collections.unmodifiableList(mEateries));
      });
    } else {
      new ProcessJSON().execute("");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    mBottomNavigationView.setSelectedItemId(R.id.action_home);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_map:
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("mEatery", new ArrayList<>(mEateries));
        startActivity(intent);
        return true;
      default:
        // The user's action was not recognized, and invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    final MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();
    setTitle("Eateries");
    if (searchView != null) {
      AutoCompleteTextView searchTextView =
          searchView.findViewById(androidx.appcompat.R.id.search_src_text);
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
      searchView.setOnQueryTextListener(
          new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              return mAdapter.getItem(mBottomNavigationView.getSelectedItemId())
                  .onQueryTextSubmit(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
              return mAdapter.getItem(mBottomNavigationView.getSelectedItemId())
                  .onQueryTextChange(newText);
            }
          }
      );
    }

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

  public class ProcessJSON extends AsyncTask<String, Void, List<EateryModel>> {
    @Override
    protected List<EateryModel> doInBackground(String... params) {
      String json = NetworkUtilities.getJSON();
      mDBHelper.addData(json);
      mEateries = JSONUtilities.parseJson(json, getApplicationContext());
      Collections.sort(mEateries);
      return mEateries;
    }

    @Override
    protected void onPostExecute(List<EateryModel> result) {
      super.onPostExecute(result);
      mSplash.setVisibility(View.GONE);
      mBottomNavigationView.setVisibility(View.VISIBLE);
      getSupportActionBar().show();
      mAdapter.forAllItems((fragment) -> {
        fragment.onDataLoaded(Collections.unmodifiableList(mEateries));
      });
      mProgressBar.setVisibility(View.GONE);
    }
  }
}
