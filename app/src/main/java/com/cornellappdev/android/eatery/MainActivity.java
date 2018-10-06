package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.arch.core.util.Function;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cornellappdev.android.eatery.data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.model.CampusArea;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.PaymentMethod;
import com.cornellappdev.android.eatery.network.ConnectionUtilities;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnQueryTextListener {

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
  public BottomNavigationView bnv;
  public Chip brbButton;
  public Chip centralButton;
  public CafeteriaDbHelper dbHelper;
  public List<EateryModel> eateries = new ArrayList<>(); // holds all cafes
  public EateryRecyclerViewAdapter listAdapter;
  //final QueryListener queryListener = new QueryListener();
  public RecyclerView mRecyclerView;
  public Chip northButton;
  public ProgressBar progressBar;
  public RelativeLayout splash;
  public Chip swipesButton;
  public Chip westButton;
  private String mCurrentQuery;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("Eatery");
    setContentView(R.layout.activity_main);
    dbHelper = new CafeteriaDbHelper(this);
    mRecyclerView = findViewById(R.id.cafe_list);
    northButton = findViewById(R.id.northButton);
    westButton = findViewById(R.id.westButton);
    centralButton = findViewById(R.id.centralButton);
    swipesButton = findViewById(R.id.swipesButton);
    brbButton = findViewById(R.id.brbButton);
    progressBar = findViewById(R.id.progress_bar);
    bnv = findViewById(R.id.bottom_navigation);
    splash = findViewById(R.id.relative_layout_splash);
    bnv.setVisibility(View.GONE);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }
    ConnectionUtilities con = new ConnectionUtilities(this);
    if (!con.isNetworkAvailable()) {
      eateries = new ArrayList<>();
      if (JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext()) != null) {
        eateries = JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext());
      }
      Collections.sort(eateries);
      mRecyclerView.setHasFixedSize(true);
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
      mRecyclerView.setLayoutManager(layoutManager);
      listAdapter = new EateryRecyclerViewAdapter(
          getApplicationContext(),
          eateries,
          EateryModel::compareTo
      );
      mRecyclerView.setAdapter(listAdapter);
    } else {
      new ProcessJson().execute("");
    }
    // Add functionality to bottom nav bar
    bnv.setOnNavigationItemSelectedListener(
        item -> {
          Intent intent;
          switch (item.getItemId()) {
            case R.id.action_home:
              ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
              sv.smoothScrollTo(0, 0);
              break;
            case R.id.action_week:
              intent = new Intent(getApplicationContext(), WeeklyMenuActivity.class);
              intent.putExtra("mEatery", new ArrayList<>(eateries));
              startActivity(intent);
              overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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
        });

    Function<PaymentMethod, OnCheckedChangeListener> paymentListener = (paymentMethod) -> (compoundButton, b) -> {
      if (b) {
        listAdapter.addFilter(paymentMethod, PAYMENT_FILTER);
      } else {
        listAdapter.removeFilters(PAYMENT_FILTER);
      }
      listAdapter.filter();
    };

    Function<CampusArea, OnCheckedChangeListener> areaListener = (area) -> (compoundButton, b) -> {
      if (b) {
        listAdapter.addFilter(area, AREA_FILTER);
      } else {
        listAdapter.removeFilters(AREA_FILTER);
      }
      listAdapter.filter();
    };

    swipesButton.setOnCheckedChangeListener(paymentListener.apply(PaymentMethod.SWIPES));
    brbButton.setOnCheckedChangeListener(paymentListener.apply(PaymentMethod.BRB));

    northButton.setOnCheckedChangeListener(areaListener.apply(CampusArea.NORTH));
    westButton.setOnCheckedChangeListener(areaListener.apply(CampusArea.WEST));
    centralButton.setOnCheckedChangeListener(areaListener.apply(CampusArea.CENTRAL));
  }

  @Override
  public void onResume() {
    super.onResume();
    bnv.setSelectedItemId(R.id.action_home);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_map:
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("mEatery", new ArrayList<>(eateries));
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
      searchView.setOnQueryTextListener(this);
    }

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    mCurrentQuery = query;
    listAdapter.addFilter(query, SEARCH_FILTER);
    listAdapter.filter();
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    listAdapter.removeFilter(mCurrentQuery, SEARCH_FILTER);
    return onQueryTextSubmit(newText);
  }

  public class SnackBarListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      Intent browser =
          new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cornellappdev.com/apply/"));
      startActivity(browser);
    }
  }

  public class ProcessJson extends AsyncTask<String, Void, List<EateryModel>> {
    @Override
    protected List<EateryModel> doInBackground(String... params) {
      String json = NetworkUtilities.getJSON();
      dbHelper.addData(json);
      eateries = JsonUtilities.parseJson(json, getApplicationContext());
      Collections.sort(eateries);
      return eateries;
    }

    @Override
    protected void onPostExecute(List<EateryModel> result) {
      super.onPostExecute(result);
      splash.setVisibility(View.GONE);
      bnv.setVisibility(View.VISIBLE);
      getSupportActionBar().show();
      mRecyclerView.setHasFixedSize(true);
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
      mRecyclerView.setLayoutManager(layoutManager);
      listAdapter =
          new EateryRecyclerViewAdapter(getApplicationContext(), new ArrayList<>(eateries),
              EateryModel::compareTo);
      mRecyclerView.setAdapter(listAdapter);
      mRecyclerView.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.GONE);
    }
  }
}
