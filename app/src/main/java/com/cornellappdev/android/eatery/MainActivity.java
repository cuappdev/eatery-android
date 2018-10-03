package com.cornellappdev.android.eatery;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.cornellappdev.android.eatery.network.ConnectionUtilities;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements MainListAdapter.ListAdapterOnClickHandler {

  public static final String PAYMENT_SWIPE = "Meal Plan - Swipe";
  public static final String PAYMENT_CARD = "Cornell Card";
  public static final String FILTER_BG_COLOR_ON = "#4B7FBE";
  public static final String FILTER_BG_COLOR_OFF = "#F2F2F2";
  public static final String FILTER_TXT_COLOR_ON = "#FFFFFF";
  public static final String FILTER_TXT_COLOR_OFF = "#4B7FBE";

  public static boolean searchPressed = false;

  public List<EateryModel> cafeList = new ArrayList<>(); // holds all cafes
  public List<EateryModel> currentList = new ArrayList<>(); // button filter list
  public List<EateryModel> searchList = new ArrayList<>(); // searchbar filter list
  public BottomNavigationView bnv;
  public Button northButton;
  public Button westButton;
  public Button centralButton;
  public Button swipesButton;
  public Button brbButton;
  public Button areaButtonPressed;
  public Button paymentButtonPressed;
  public CafeteriaDbHelper dbHelper;
  public MainListAdapter listAdapter;
  public ProgressBar progressBar;
  final QueryListener queryListener = new QueryListener();
  public RecyclerView mRecyclerView;
  public RelativeLayout splash;

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
      cafeList = new ArrayList<>();
      if (JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext()) != null) {
        cafeList = JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext());
      }
      Collections.sort(cafeList);
      currentList = cafeList;
      searchList = cafeList;

      mRecyclerView.setHasFixedSize(true);
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
      mRecyclerView.setLayoutManager(layoutManager);

      listAdapter =
          new MainListAdapter(
              getApplicationContext(), MainActivity.this, cafeList.size(), cafeList);
      mRecyclerView.setAdapter(listAdapter);
    } else {
      new ProcessJson().execute("");
    }

    // Add functionality to bottom nav bar
    bnv.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
              case R.id.action_home:
                ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
                sv.smoothScrollTo(0, 0);
                break;
              case R.id.action_week:
                intent = new Intent(getApplicationContext(), WeeklyMenuActivity.class);
                intent.putExtra("mEatery", new ArrayList<>(cafeList));
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
          }
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

  private void filterCurrentList() {
    for (EateryModel model : currentList) {
      final boolean areaFuzzyMatches =
          getCurrentArea() == null || model.getArea() == getCurrentArea();
      final boolean paymentFuzzyMatches =
          getCurrentPaymentType() == null
              || model.getPayMethods().contains(getCurrentPaymentType());
      if (areaFuzzyMatches && paymentFuzzyMatches) {
        model.setMatchesFilter(true);
      } else {
        model.setMatchesFilter(false);
      }
    }
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

  private String getCurrentPaymentType() {
    if (brbButton.equals(paymentButtonPressed)) {
      return PAYMENT_CARD;
    } else if (swipesButton.equals(paymentButtonPressed)) {
      return PAYMENT_SWIPE;
    } else {
      return null;
    }
  }

  private void handleAreaButtonPress(Button button, CampusArea area) {
    if (!button.equals(areaButtonPressed)) {
      changeButtonColor(FILTER_TXT_COLOR_ON, FILTER_BG_COLOR_ON, button);
      if (areaButtonPressed != null) {
        changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, areaButtonPressed);
      }
      areaButtonPressed = button;
    } else {
      changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, button);
      areaButtonPressed = null;
    }
    filterCurrentList();
  }

  private void handlePaymentButtonPress(Button button, String payment) {
    if (!button.equals(paymentButtonPressed)) {
      changeButtonColor(FILTER_TXT_COLOR_ON, FILTER_BG_COLOR_ON, button);
      if (paymentButtonPressed != null) {
        changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, paymentButtonPressed);
      }
      paymentButtonPressed = button;
    } else {
      changeButtonColor(FILTER_TXT_COLOR_OFF, FILTER_BG_COLOR_OFF, button);
      paymentButtonPressed = null;
    }
    filterCurrentList();
  }

  public void filterClick(View view) {
    switch (view.getId()) {
      case R.id.northButton:
        handleAreaButtonPress(northButton, CampusArea.NORTH);
        break;
      case R.id.centralButton:
        handleAreaButtonPress(centralButton, CampusArea.CENTRAL);
        break;
      case R.id.westButton:
        handleAreaButtonPress(westButton, CampusArea.WEST);
        break;
      case R.id.swipes:
        handlePaymentButtonPress(swipesButton, PAYMENT_SWIPE);
        break;
      case R.id.brb:
        handlePaymentButtonPress(brbButton, PAYMENT_CARD);
        break;
    }
    ArrayList<EateryModel> cafesToDisplay = new ArrayList<>();
    for (EateryModel cm : currentList) {
      if (cm.matchesFilter()) {
        cafesToDisplay.add(cm);
      }
    }
    searchList = cafesToDisplay;
    if (searchPressed) {
      queryListener.onQueryTextChange(queryListener.query);
    } else {
      Collections.sort(cafesToDisplay);
      listAdapter.setList(cafesToDisplay, cafesToDisplay.size(), null);
    }
  }

  @Override
  public void onClick(int position, List<EateryModel> list) {
    Intent intent = new Intent(this, MenuActivity.class);
    intent.putExtra("testData", new ArrayList<>(list));
    intent.putExtra("cafeInfo", list.get(position));
    intent.putExtra("locName", list.get(position).getNickName());
    startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_map:
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("mEatery", new ArrayList<>(cafeList));
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

    searchView.setOnQueryTextListener(queryListener);
    return super.onCreateOptionsMenu(menu);
  }

  public class QueryListener implements SearchView.OnQueryTextListener {

    public String query = "";

    private void searchList(String query) {
      final String lowercaseQuery = query.toLowerCase();
      for (EateryModel model : searchList) {
        final List<String> mealSet = model.getMealItems();

        boolean foundNickName = false;
        if (model.getNickName().toLowerCase().contains(lowercaseQuery)) {
          foundNickName = true;
        }

        ArrayList<String> matchedItems = new ArrayList<>();
        boolean foundItem = false;
        for (String item : mealSet) {
          if (item.toLowerCase().contains(lowercaseQuery)) {
            foundItem = true;
            matchedItems.add(item);
          }
        }

        if (model.matchesFilter() && (foundItem || foundNickName)) {
          if (foundNickName) {
            model.setSearchedItems(new ArrayList<>(mealSet));
          } else {
            model.setSearchedItems(matchedItems);
          }
          model.setMatchesSearch(true);
        } else {
          model.setMatchesSearch(false);
        }
      }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
      this.query = query;
      ArrayList<EateryModel> cafesToDisplay = new ArrayList<>();
      if (query.length() == 0) {
        searchList = currentList;
        searchPressed = false;
        for (EateryModel cm : searchList) {
          if (cm.matchesFilter()) {
            cafesToDisplay.add(cm);
          }
        }
      } else {
        searchPressed = true;
        searchList(query);
        for (EateryModel cm : searchList) {
          if (cm.matchesFilter() && cm.matchesSearch()) {
            cafesToDisplay.add(cm);
          }
        }
      }
      Collections.sort(cafesToDisplay);
      listAdapter.setList(
          cafesToDisplay, cafesToDisplay.size(), query.length() == 0 ? null : query);
      return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
      return onQueryTextSubmit(newText);
    }
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

      cafeList = JsonUtilities.parseJson(json, getApplicationContext());
      Collections.sort(cafeList);
      currentList = cafeList;
      searchList = cafeList;

      return cafeList;
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
          new MainListAdapter(getApplicationContext(), MainActivity.this, result.size(),
              new ArrayList<>(cafeList));
      mRecyclerView.setAdapter(listAdapter);
      mRecyclerView.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.GONE);
    }
  }
}
