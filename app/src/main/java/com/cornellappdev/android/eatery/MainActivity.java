package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.cornellappdev.android.eatery.data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.model.CampusArea;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.PaymentMethod;
import com.cornellappdev.android.eatery.network.ConnectionUtilities;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnQueryTextListener {

  public static final String FILTER_BG_COLOR_OFF = "#F2F2F2";
  public static final String FILTER_BG_COLOR_ON = "#4B7FBE";
  public static final String FILTER_TXT_COLOR_OFF = "#4B7FBE";
  public static final String FILTER_TXT_COLOR_ON = "#FFFFFF";
  public static final String PAYMENT_CARD = "Cornell Card";
  public static final String PAYMENT_SWIPE = "Meal Plan - Swipe";
  private static final EateryModelFilter<CampusArea> AREA_FILTER = (model, area) -> area == model
      .getArea();
  private static final EateryModelFilter<PaymentMethod> PAYMENT_FILTER = EateryModel::hasPaymentMethod;
  private static final EateryModelFilter<String> SEARCH_FILTER = (model, query) -> {
    String cleanQuery = query.trim().toLowerCase();
    boolean remove = true;
    if (model.getNickName().contains(query)) {
      remove = false;
    }

    List<String> mealItems = model.getMealItems();
    for (String mealItem : mealItems) {
      if (mealItem.toLowerCase().contains(cleanQuery)) {
        remove = false;
      }
    }
    return remove;
  };
  public static boolean searchPressed = false;
  public Button areaButtonPressed;
  public BottomNavigationView bnv;
  public Button brbButton;
  public Button centralButton;
  public CafeteriaDbHelper dbHelper;
  public List<EateryModel> eateries = new ArrayList<>(); // holds all cafes
  public EateryRecyclerViewAdapter listAdapter;
  //final QueryListener queryListener = new QueryListener();
  public RecyclerView mRecyclerView;
  public Button northButton;
  public Button paymentButtonPressed;
  public ProgressBar progressBar;
  public RelativeLayout splash;
  public Button swipesButton;
  public Button westButton;

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
    swipesButton = findViewById(R.id.swipes);
    brbButton = findViewById(R.id.brb);
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
          eateries.size(),
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
  }

  @Override
  public void onResume() {
    super.onResume();
    bnv.setSelectedItemId(R.id.action_home);
  }

  public void changeButtonColor(String textColor, String backgroundColor, Button button) {
    button.setTextColor(Color.parseColor(textColor));
    GradientDrawable bgShape = (GradientDrawable) button.getBackground();
    bgShape.setColor(Color.parseColor(backgroundColor));
  }

  private CampusArea getCurrentArea() {
    if (northButton.equals(areaButtonPressed)) {
      return CampusArea.NORTH;
    } else if (westButton.equals(areaButtonPressed)) {
      return CampusArea.WEST;
    } else if (centralButton.equals(areaButtonPressed)) {
      return CampusArea.CENTRAL;
    } else {
      return null;
    }
  }

  private PaymentMethod getCurrentPaymentType() {
    if (brbButton.equals(paymentButtonPressed)) {
      return PaymentMethod.BRB;
    } else if (swipesButton.equals(paymentButtonPressed)) {
      return PaymentMethod.SWIPES;
    } else {
      return null;
    }
  }

  private void handleAreaButtonPress(Button button, CampusArea area) {
    CampusArea currentArea = getCurrentArea();
    if (!button.equals(areaButtonPressed)) {
      changeButtonColor(FILTER_TXT_COLOR_ON, FILTER_BG_COLOR_ON, button);
      if (areaButtonPressed != null) {
        changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, areaButtonPressed);
      }
      if (currentArea != null) {
        listAdapter.removeFilter(currentArea, AREA_FILTER);
      }
      areaButtonPressed = button;
      listAdapter.addFilter(area, AREA_FILTER);
    } else {
      changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, button);
      areaButtonPressed = null;
      listAdapter.removeFilter(area, AREA_FILTER);
    }
    listAdapter.filter();
  }

  private void handlePaymentButtonPress(Button button, PaymentMethod payment) {
    PaymentMethod paymentMethod = getCurrentPaymentType();
    if (!button.equals(paymentButtonPressed)) {
      changeButtonColor(FILTER_TXT_COLOR_ON, FILTER_BG_COLOR_ON, button);
      if (paymentButtonPressed != null) {
        changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, paymentButtonPressed);
      }
      if (paymentMethod != null) {
        listAdapter.removeFilter(paymentMethod, PAYMENT_FILTER);
      }
      paymentButtonPressed = button;
      listAdapter.addFilter(payment, PAYMENT_FILTER);
    } else {
      changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, button);
      paymentButtonPressed = null;
      listAdapter.removeFilter(payment, PAYMENT_FILTER);
    }
    listAdapter.filter();
  }

  public void filterClick(View view) {
    switch (view.getId()) {
      case R.id.northButton:
        handleAreaButtonPress(northButton, CampusArea.NORTH);
        return;
      case R.id.westButton:
        handleAreaButtonPress(westButton, CampusArea.WEST);
        return;
      case R.id.centralButton:
        handleAreaButtonPress(centralButton, CampusArea.CENTRAL);
        return;
      case R.id.swipes:
        handlePaymentButtonPress(swipesButton, PaymentMethod.SWIPES);
        return;
      case R.id.brb:
        handlePaymentButtonPress(brbButton, PaymentMethod.BRB);
    }
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
    searchView.setOnQueryTextListener(this);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    return false;
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
          new EateryRecyclerViewAdapter(getApplicationContext(), result.size(),
              new ArrayList<>(eateries), EateryModel::compareTo);
      mRecyclerView.setAdapter(listAdapter);
      mRecyclerView.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.GONE);
    }
  }
}
