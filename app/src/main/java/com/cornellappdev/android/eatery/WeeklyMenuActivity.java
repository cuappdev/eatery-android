package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.cornellappdev.android.eatery.model.CafeteriaModel;
import com.cornellappdev.android.eatery.model.MealModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WeeklyMenuActivity extends AppCompatActivity {
  public BottomNavigationView bnv;
  ExpandableListAdapter listAdapterWest;
  ExpandableListAdapter listAdapterNorth;
  ExpandableListAdapter listAdapterCentral;
  NonScrollExpandableListView expListViewWest;
  NonScrollExpandableListView expListViewNorth;
  NonScrollExpandableListView expListViewCentral;
  TextView westText;
  TextView northText;
  TextView centralText;
  ArrayList<CafeteriaModel> cafeData;
  ArrayList<CafeteriaModel> diningHall = new ArrayList<>();
  String mealType = "breakfast";
  int selectedDate;
  TextView breakfastText;
  TextView lunchText;
  TextView dinnerText;
  LinearLayout linDate;
  ArrayList<TextView> dateList = new ArrayList<>();
  ArrayList<ArrayList<TreeMap<String, MealModel>>> weeklyMenu;
  int lastExpandedPosition;
  NonScrollExpandableListView lastClickedListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_weekly_menu);
    bnv = findViewById(R.id.bottom_navigation);
    breakfastText = findViewById(R.id.breakfast);
    lunchText = findViewById(R.id.lunch);
    dinnerText = findViewById(R.id.dinner);
    linDate = findViewById(R.id.lin_date);
    expListViewWest = findViewById(R.id.expandablelistview_west);
    expListViewNorth = findViewById(R.id.expandablelistview_north);
    expListViewCentral = findViewById(R.id.expandablelistview_central);
    westText = findViewById(R.id.west_header);
    northText = findViewById(R.id.north_header);
    centralText = findViewById(R.id.central_header);

    Intent intent = getIntent();
    cafeData = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("cafeData");

    // Layout for menu list
    setTitle("Upcoming Menus");
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    expListViewWest.setIndicatorBounds(width - 250, width);
    expListViewNorth.setIndicatorBounds(width - 250, width);
    expListViewCentral.setIndicatorBounds(width - 250, width);

    lastExpandedPosition = -1;
    lastClickedListView = null;

    expListViewCentral.setOnGroupExpandListener(
        new NonScrollExpandableListView.OnGroupExpandListener() {
          @Override
          public void onGroupExpand(int i) {
            if (lastClickedListView != null
                && lastExpandedPosition != -1
                && i != lastExpandedPosition) {
              lastClickedListView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = i;
            lastClickedListView = expListViewCentral;
          }
        });

    expListViewWest.setOnGroupExpandListener(
        new NonScrollExpandableListView.OnGroupExpandListener() {
          @Override
          public void onGroupExpand(int i) {
            if (lastClickedListView != null
                && lastExpandedPosition != -1
                && i != lastExpandedPosition) {
              lastClickedListView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = i;
            lastClickedListView = expListViewWest;
          }
        });
    expListViewNorth.setOnGroupExpandListener(
        new NonScrollExpandableListView.OnGroupExpandListener() {
          @Override
          public void onGroupExpand(int i) {
            if (lastClickedListView != null
                && lastExpandedPosition != -1
                && i != lastExpandedPosition) {
              lastClickedListView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = i;
            lastClickedListView = expListViewNorth;
          }
        });

    // Populate list of date TextViews on header
    dateList.add((TextView) findViewById(R.id.date0));
    dateList.add((TextView) findViewById(R.id.date1));
    dateList.add((TextView) findViewById(R.id.date2));
    dateList.add((TextView) findViewById(R.id.date3));
    dateList.add((TextView) findViewById(R.id.date4));
    dateList.add((TextView) findViewById(R.id.date5));
    dateList.add((TextView) findViewById(R.id.date6));

    // When changing date, highlight Breakfast textview
    linDate.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            breakfastText.setTextColor(Color.parseColor("#000000"));
            lunchText.setTextColor(Color.parseColor("#cdcdcd"));
            dinnerText.setTextColor(Color.parseColor("#cdcdcd"));
          }
        });

    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);
    for (int i = 0; i < 7; i++) {
      // Formatting for each day
      SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");
      dateFormat.setCalendar(cal);
      String sDay = dateFormat.format(cal.getTime());

      // Formatting for each date
      int date = cal.get(Calendar.DAY_OF_MONTH);
      String sDate = Integer.toString(date);
      SpannableString ssDate = new SpannableString(sDay + '\n' + sDate);
      ssDate.setSpan(new RelativeSizeSpan(0.8f), 0, 3, 0);
      ssDate.setSpan(new RelativeSizeSpan(2f), 4, ssDate.length(), 0);
      TextView tv = dateList.get(i);
      tv.setText(ssDate);

      cal.add(Calendar.DAY_OF_YEAR, 1);
    }

    // Highlight the icon selected
    bnv.setSelectedItemId(R.id.action_week);

    // Get list of dining halls
    for (CafeteriaModel m : cafeData) {
      if (m.getIsDiningHall()) {
        diningHall.add(m);
      }
    }

    // Parse the weekly menu when the user starts this activity
    weeklyMenu = parseWeeklyMenu(dateList);

    breakfastText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            breakfastText.setTextColor(Color.parseColor("#000000"));
            lunchText.setTextColor(Color.parseColor("#cdcdcd"));
            dinnerText.setTextColor(Color.parseColor("#cdcdcd"));

            mealType = "breakfast";
            changeListAdapter(mealType, selectedDate);
          }
        });

    lunchText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
            lunchText.setTextColor(Color.parseColor("#000000"));
            dinnerText.setTextColor(Color.parseColor("#cdcdcd"));

            mealType = "lunch";
            changeListAdapter(mealType, selectedDate);
          }
        });

    dinnerText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
            lunchText.setTextColor(Color.parseColor("#cdcdcd"));
            dinnerText.setTextColor(Color.parseColor("#000000"));

            mealType = "dinner";
            changeListAdapter(mealType, selectedDate);
          }
        });

    Date date = new Date();
    if (date.getHours() < 11) {
      changeListAdapter("breakfast", 0);
      mealType = "breakfast";
      breakfastText.setTextColor(Color.parseColor("#000000"));
      lunchText.setTextColor(Color.parseColor("#cdcdcd"));
      dinnerText.setTextColor(Color.parseColor("#cdcdcd"));
    } else if (date.getHours() < 16) {
      changeListAdapter("lunch", 0);
      mealType = "lunch";
      breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
      lunchText.setTextColor(Color.parseColor("#000000"));
      dinnerText.setTextColor(Color.parseColor("#cdcdcd"));
    } else if (date.getHours() < 22) {
      changeListAdapter("dinner", 0);
      mealType = "dinner";
      breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
      lunchText.setTextColor(Color.parseColor("#cdcdcd"));
      dinnerText.setTextColor(Color.parseColor("#000000"));
    } else {
      changeListAdapter("breakfast", 1);
      mealType = "breakfast";
      breakfastText.setTextColor(Color.parseColor("#000000"));
      lunchText.setTextColor(Color.parseColor("#cdcdcd"));
      dinnerText.setTextColor(Color.parseColor("#cdcdcd"));
    }

    // Adds functionality to bottom nav bar
    bnv.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.action_home:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
              case R.id.action_week:
                ScrollView sv = findViewById(R.id.weekly_scroll);
                sv.smoothScrollTo(0, 0);
                break;
              case R.id.action_brb:
                Snackbar snackbar =
                    Snackbar.make(
                        findViewById(R.id.weekly_activity),
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

  /** Changes the text color to black when selected */
  public void dateFilterClick(View v) {
    TextView tv = (TextView) v;

    tv.setTextColor(Color.parseColor("#000000"));
    changeDateColor(tv);

    int id = tv.getId();

    switch (id) {
      case R.id.date0:
        selectedDate = 0;
        break;
      case R.id.date1:
        selectedDate = 1;
        break;
      case R.id.date2:
        selectedDate = 2;
        break;
      case R.id.date3:
        selectedDate = 3;
        break;
      case R.id.date4:
        selectedDate = 4;
        break;
      case R.id.date5:
        selectedDate = 5;
        break;
      case R.id.date6:
        selectedDate = 6;
        break;
    }
    changeListAdapter(mealType, selectedDate);
  }

  /** Changes the text color to grey if the date is not selected */
  public void changeDateColor(TextView v) {
    for (int i = 0; i < 7; i++) {
      if (!dateList.get(i).equals(v)) {
        dateList.get(i).setTextColor(Color.parseColor("#cdcdcd"));
      }
    }
  }

  /** Updates the list of dining halls and menus that is displayed */
  public void changeListAdapter(String mealType, int dateOffset) {
    int mealIndex = 0;
    switch (mealType) {
      case "breakfast":
        mealIndex = 0;
        break;
      case "lunch":
        mealIndex = 1;
        break;
      case "dinner":
        mealIndex = 2;
        break;
    }
    HashMap<String, TreeMap<String, ArrayList<String>>> finalList =
        generateFinalList(weeklyMenu.get(dateOffset).get(mealIndex));

    // Hides layout elements if there is nothing in the list corresponding to a certain
    // CafeteriaArea
    if (finalList.get("West") != null && finalList.get("West").size() == 0) {
      westText.setVisibility(View.GONE);
      expListViewWest.setVisibility(View.GONE);
    } else {
      westText.setVisibility(View.VISIBLE);
      expListViewWest.setVisibility(View.VISIBLE);

      listAdapterWest =
          new ExpandableListAdapter(
              getApplicationContext(), finalList.get("West"), dateOffset, mealIndex, cafeData);
      expListViewWest.setAdapter(listAdapterWest);
    }
    if (finalList.get("North") != null && finalList.get("North").size() == 0) {
      northText.setVisibility(View.GONE);
      expListViewNorth.setVisibility(View.GONE);
    } else {
      northText.setVisibility(View.VISIBLE);
      expListViewNorth.setVisibility(View.VISIBLE);

      listAdapterNorth =
          new ExpandableListAdapter(
              getApplicationContext(), finalList.get("North"), dateOffset, mealIndex, cafeData);
      expListViewNorth.setAdapter(listAdapterNorth);
    }
    if (finalList.get("Central") != null && finalList.get("Central").size() == 0) {
      centralText.setVisibility(View.GONE);
      expListViewCentral.setVisibility(View.GONE);
    } else {
      centralText.setVisibility(View.VISIBLE);
      expListViewCentral.setVisibility(View.VISIBLE);

      listAdapterCentral =
          new ExpandableListAdapter(
              getApplicationContext(), finalList.get("Central"), dateOffset, mealIndex, cafeData);
      expListViewCentral.setAdapter(listAdapterCentral);
    }
  }

  /**
   * Generates a list of for breakfast, lunch, and dinner for a particular day. Day is determined by
   * the dateOffset from the current time.
   */
  public ArrayList<TreeMap<String, MealModel>> generateMealLists(int dateOffset) {
    ArrayList<TreeMap<String, MealModel>> finalList = new ArrayList<>();
    TreeMap<String, MealModel> breakfastList = new TreeMap<>();
    TreeMap<String, MealModel> lunchList = new TreeMap<>();
    TreeMap<String, MealModel> dinnerList = new TreeMap<>();
    for (CafeteriaModel m : diningHall) {
      // Checks that dining hall is opened
      if (m.indexOfCurrentDay() != -1) {
        // Get MealModel for the day and split into three hashmaps
        ArrayList<MealModel> meals = m.getWeeklyMenu().get(m.indexOfCurrentDay() + dateOffset);
        for (MealModel n : meals) {
          if (n.getMenu().size() > 0) {
            if ((n.getType().equals("Breakfast") || n.getType().equals("Brunch"))) {
              breakfastList.put(m.getNickName(), n);
            }
            if (n.getType().equals("Lunch")
                || n.getType().equals("Brunch")
                || n.getType().equals("Lite Lunch")) {
              lunchList.put(m.getNickName(), n);
            }
            if (n.getType().equals("Dinner")) {
              dinnerList.put(m.getNickName(), n);
            }
          }
        }
      }
    }
    finalList.add(breakfastList);
    finalList.add(lunchList);
    finalList.add(dinnerList);

    return finalList;
  }

  /** Converts the MealModel object of the map into an Arraylist */
  private HashMap<String, TreeMap<String, ArrayList<String>>> generateFinalList(
      TreeMap<String, MealModel> listToParse) {
    HashMap<String, TreeMap<String, ArrayList<String>>> listFinal = new HashMap<>();
    TreeMap<String, ArrayList<String>> finalWest = new TreeMap<>();
    TreeMap<String, ArrayList<String>> finalNorth = new TreeMap<>();
    TreeMap<String, ArrayList<String>> finalCentral = new TreeMap<>();

    for (Map.Entry<String, MealModel> cafe : listToParse.entrySet()) {
      ArrayList<String> mealToList = new ArrayList<String>();

      // Get menu of dining hall
      MealModel m = cafe.getValue();
      HashMap<String, ArrayList<String>> entrySet = m.getMenu();

      // Add both category + meal items into an ArrayList
      for (Map.Entry<String, ArrayList<String>> entry : entrySet.entrySet()) {
        // Add '3' in front to denote category
        String key = "3" + entry.getKey();
        ArrayList<String> values = entry.getValue();

        mealToList.add(key);
        for (String items : values) {
          mealToList.add(items);
        }
      }
      mealToList.add(" ");

      CafeteriaModel myCafe = null;
      int count = 0;

      while (count < cafeData.size()) {
        if (cafeData.get(count).getNickName().equals(cafe.getKey())) {
          myCafe = cafeData.get(count);
        }
        count++;
      }

      switch (myCafe.getArea()) {
        case WEST:
          finalWest.put(cafe.getKey(), mealToList);
          break;
        case NORTH:
          finalNorth.put(cafe.getKey(), mealToList);
          break;
        case CENTRAL:
          finalCentral.put(cafe.getKey(), mealToList);
          break;
      }
      listFinal.put("West", finalWest);
      listFinal.put("North", finalNorth);
      listFinal.put("Central", finalCentral);
    }
    return listFinal;
  }

  /**
   * Assume that dateList is not null. Returns an Arraylist of the set(breakfastlist, lunchlist,
   * dinnerlist) for each day in the dateList. Each of the meal lists is a hashmap of
   * CafeteriaModels that have that specific meal and the menu
   */
  public ArrayList<ArrayList<TreeMap<String, MealModel>>> parseWeeklyMenu(
      ArrayList<TextView> dateList) {
    ArrayList<ArrayList<TreeMap<String, MealModel>>> mainList = new ArrayList<>();

    for (int i = 0; i < dateList.size(); i++) {
      // List contains set(breakfastlist, lunchlist, dinnerlist) for the day
      ArrayList<TreeMap<String, MealModel>> dailyList = new ArrayList<>();
      dailyList = generateMealLists(i);
      mainList.add(dailyList);
    }
    return mainList;
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
