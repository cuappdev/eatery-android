package com.cornellappdev.android.eatery;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.cornellappdev.android.eatery.data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.model.CampusArea;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.network.ConnectionUtilities;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EateriesFragment extends Fragment implements
    MainListAdapter.ListAdapterOnClickHandler {

  public static final String PAYMENT_SWIPE = "Meal Plan - Swipe";
  public static final String PAYMENT_CARD = "Cornell Card";
  public static final String FILTER_BG_COLOR_ON = "#4B7FBE";
  public static final String FILTER_BG_COLOR_OFF = "#F2F2F2";
  public static final String FILTER_TXT_COLOR_ON = "#FFFFFF";
  public static final String FILTER_TXT_COLOR_OFF = "#4B7FBE";

  final QueryListener queryListener = new QueryListener();
  public static boolean searchPressed = false;

  public List<EateryModel> cafeList = new ArrayList<>(); // holds all cafes
  public List<EateryModel> currentList = new ArrayList<>(); // button filter list
  public List<EateryModel> searchList = new ArrayList<>(); // searchbar filter list
  public CafeteriaDbHelper dbHelper;

  public Button northButton;
  public Button westButton;
  public Button centralButton;
  public Button swipesButton;
  public Button brbButton;
  public Button areaButtonPressed;
  public Button paymentButtonPressed;

  public MainListAdapter listAdapter;
  public ProgressBar progressBar;
  public RecyclerView mRecyclerView;

  public List<EateryModel> getCurrentEateries() {
    return new ArrayList<>(cafeList);
  }

  //public RelativeLayout splash;
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final View eateriesFragment = inflater.inflate(R.layout.eateries_fragment, container, false);

    mRecyclerView = eateriesFragment.findViewById(R.id.cafe_list);
    northButton = eateriesFragment.findViewById(R.id.northButton);
    westButton = eateriesFragment.findViewById(R.id.westButton);
    centralButton = eateriesFragment.findViewById(R.id.centralButton);
    swipesButton = eateriesFragment.findViewById(R.id.swipes);
    brbButton = eateriesFragment.findViewById(R.id.brb);
    progressBar = eateriesFragment.findViewById(R.id.progress_bar);

    Context applicationContext = getContext().getApplicationContext();
    dbHelper = new CafeteriaDbHelper(getContext());
    ConnectionUtilities con = new ConnectionUtilities(getContext());

    if (!con.isNetworkAvailable()) {
      cafeList = new ArrayList<>();
      if (JsonUtilities.parseJson(dbHelper.getLastRow(), applicationContext) != null) {
        cafeList = JsonUtilities.parseJson(dbHelper.getLastRow(), applicationContext);
      }
      Collections.sort(cafeList);
      currentList = cafeList;
      searchList = cafeList;

      mRecyclerView.setHasFixedSize(true);
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(applicationContext, LinearLayout.VERTICAL, false);
      mRecyclerView.setLayoutManager(layoutManager);

      listAdapter =
          new MainListAdapter(applicationContext, this, cafeList.size(), cafeList);
      mRecyclerView.setAdapter(listAdapter);
    } else {
      new ProcessJson().execute("");
    }

    return eateriesFragment;
  }

  public class ProcessJson extends AsyncTask<String, Void, List<EateryModel>> {

    @Override
    protected List<EateryModel> doInBackground(String... params) {
      String json = NetworkUtilities.getJSON();
      dbHelper.addData(json);

      cafeList = JsonUtilities.parseJson(json, getContext().getApplicationContext());
      Collections.sort(cafeList);
      currentList = cafeList;
      searchList = cafeList;

      return cafeList;
    }

    @Override
    protected void onPostExecute(List<EateryModel> result) {
      super.onPostExecute(result);

      //splash.setVisibility(View.GONE);
      // TODO mBottomNavigationView.setVisibility(View.VISIBLE);

      mRecyclerView.setHasFixedSize(true);
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(getContext().getApplicationContext(), LinearLayout.VERTICAL,
              false);
      mRecyclerView.setLayoutManager(layoutManager);

      listAdapter =
          new MainListAdapter(getContext().getApplicationContext(), EateriesFragment.this,
              result.size(),
              new ArrayList<>(cafeList));
      mRecyclerView.setAdapter(listAdapter);
      mRecyclerView.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.GONE);
    }
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

    //if (searchPressed) {
    //  queryListener.onQueryTextChange(queryListener.query);
    //} else {
    Collections.sort(cafesToDisplay);
    listAdapter.setList(cafesToDisplay, cafesToDisplay.size(), null);
    //}
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

  @Override
  public void onClick(int position, List<EateryModel> list) {
    //Intent intent = new Intent(this, EateriesFragment.class);
    //intent.putExtra("testData", new ArrayList<>(list));
    //intent.putExtra("cafeInfo", list.get(position));
    //intent.putExtra("locName", list.get(position).getNickName());
    //startActivity(intent);
  }

  public void changeButtonColor(String textColor, String backgroundColor, Button button) {
    button.setTextColor(Color.parseColor(textColor));
    GradientDrawable bgShape = (GradientDrawable) button.getBackground();
    bgShape.setColor(Color.parseColor(backgroundColor));
  }

}
