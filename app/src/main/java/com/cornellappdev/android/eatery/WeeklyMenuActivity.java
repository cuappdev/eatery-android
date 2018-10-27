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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.enums.MealType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
  ArrayList<DiningHallModel> cafeData;
  ArrayList<DiningHallModel> diningHallList = new ArrayList<>();
  MealType mealType = MealType.BREAKFAST;
  LocalDate selectedDate;
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
    cafeData = (ArrayList<DiningHallModel>) intent.getSerializableExtra("cafeData");

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
            breakfastText.setTextColor(Color.parseColor("#4a90e2"));
            lunchText.setTextColor(Color.parseColor("#7d8288"));
            dinnerText.setTextColor(Color.parseColor("#7d8288"));
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
    for (EateryBaseModel m : cafeData) {
      if (m instanceof DiningHallModel) {
        diningHallList.add((DiningHallModel) m);
      }
    }

    breakfastText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            breakfastText.setTextColor(Color.parseColor("#4a90e2"));
            lunchText.setTextColor(Color.parseColor("#7d8288"));
            dinnerText.setTextColor(Color.parseColor("#7d8288"));

            mealType = MealType.BREAKFAST;
            changeListAdapter(mealType, selectedDate);
          }
        });

    lunchText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            breakfastText.setTextColor(Color.parseColor("#7d8288"));
            lunchText.setTextColor(Color.parseColor("#4a90e2"));
            dinnerText.setTextColor(Color.parseColor("#7d8288"));

            mealType = MealType.LUNCH;
            changeListAdapter(mealType, selectedDate);
          }
        });

    dinnerText.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            breakfastText.setTextColor(Color.parseColor("#7d8288"));
            lunchText.setTextColor(Color.parseColor("#7d8288"));
            dinnerText.setTextColor(Color.parseColor("#4a90e2"));

            mealType = MealType.DINNER;
            changeListAdapter(mealType, selectedDate);
          }
        });
    LocalDateTime currentTime = LocalDateTime.now();
    selectedDate = LocalDate.now();
    if (currentTime.getHour() < 11) {
      changeListAdapter(MealType.BREAKFAST, selectedDate);
      mealType = MealType.BREAKFAST;
      breakfastText.setTextColor(Color.parseColor("#4a90e2"));
      lunchText.setTextColor(Color.parseColor("#7d8288"));
      dinnerText.setTextColor(Color.parseColor("#7d8288"));
    } else if (currentTime.getHour()  < 16) {
      changeListAdapter(MealType.LUNCH, selectedDate);
      mealType = MealType.LUNCH;
      breakfastText.setTextColor(Color.parseColor("#7d8288"));
      lunchText.setTextColor(Color.parseColor("#4a90e2"));
      dinnerText.setTextColor(Color.parseColor("#7d8288"));
    } else if (currentTime.getHour() < 22) {
      changeListAdapter(MealType.DINNER, selectedDate);
      mealType = MealType.DINNER;
      breakfastText.setTextColor(Color.parseColor("#7d8288"));
      lunchText.setTextColor(Color.parseColor("#7d8288"));
      dinnerText.setTextColor(Color.parseColor("#4a90e2"));
    } else {
      selectedDate = selectedDate.plusDays(1);
      changeListAdapter(MealType.BREAKFAST, selectedDate);
      mealType = MealType.BREAKFAST;
      breakfastText.setTextColor(Color.parseColor("#4a90e2"));
      lunchText.setTextColor(Color.parseColor("#7d8288"));
      dinnerText.setTextColor(Color.parseColor("#7d8288"));
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
        selectedDate = LocalDate.now().plusDays(0);
        break;
      case R.id.date1:
        selectedDate = LocalDate.now().plusDays(1);
        break;
      case R.id.date2:
        selectedDate = LocalDate.now().plusDays(2);
        break;
      case R.id.date3:
        selectedDate = LocalDate.now().plusDays(3);
        break;
      case R.id.date4:
        selectedDate = LocalDate.now().plusDays(4);
        break;
      case R.id.date5:
        selectedDate = LocalDate.now().plusDays(5);
        break;
      case R.id.date6:
        selectedDate = LocalDate.now().plusDays(6);
        break;
    }
    changeListAdapter(mealType, selectedDate);
  }

  /** Changes the text color to grey if the date is not selected */
  public void changeDateColor(TextView v) {
    for (int i = 0; i < 7; i++) {
      if (!dateList.get(i).equals(v)) {
        dateList.get(i).setTextColor(Color.parseColor("#7d8288"));
      }
    }
  }

  /** Updates the list of dining halls and menus that is displayed */
  public void changeListAdapter(MealType mealType, LocalDate date) {
    HashMap<String, ArrayList<DiningHallModel>> finalList = generateAreaLists(mealType, date);
    ArrayList<DiningHallModel> westList = finalList.get("West");
    ArrayList<DiningHallModel> northList = finalList.get("North");
    ArrayList<DiningHallModel> centralList = finalList.get("Central");
    Log.d("log-weekly", "HIHIIHIHIHI");

    // Hides layout elements if there is nothing in the list corresponding to a certain
    // CafeteriaArea
    if (westList != null && westList.size() == 0) {
      westText.setVisibility(View.GONE);
      expListViewWest.setVisibility(View.GONE);
    } else {
      westText.setVisibility(View.VISIBLE);
      expListViewWest.setVisibility(View.VISIBLE);

      listAdapterWest =
          new ExpandableListAdapter(
              this, date, mealType, westList);
      expListViewWest.setAdapter(listAdapterWest);
    }
    if (northList != null && northList.size() == 0) {
      northText.setVisibility(View.GONE);
      expListViewNorth.setVisibility(View.GONE);
    } else {
      northText.setVisibility(View.VISIBLE);
      expListViewNorth.setVisibility(View.VISIBLE);

      listAdapterNorth =
          new ExpandableListAdapter(
              this, date, mealType, northList);
      expListViewNorth.setAdapter(listAdapterNorth);
    }
    if (centralList != null && centralList.size() == 0) {
      centralText.setVisibility(View.GONE);
      expListViewCentral.setVisibility(View.GONE);
    } else {
      centralText.setVisibility(View.VISIBLE);
      expListViewCentral.setVisibility(View.VISIBLE);

      listAdapterCentral =
          new ExpandableListAdapter(
              this, date, mealType, centralList);
      expListViewCentral.setAdapter(listAdapterCentral);
    }
  }

  public HashMap<String, ArrayList<DiningHallModel>> generateAreaLists(MealType mealType, LocalDate date) {
    ArrayList<DiningHallModel> westList = new ArrayList<>();
    ArrayList<DiningHallModel> northList = new ArrayList<>();
    ArrayList<DiningHallModel> centralList = new ArrayList<>();
    for (DiningHallModel dhm : diningHallList) {
      if (dhm.getMealByDateAndType(date, mealType) != null) {
        switch (dhm.getArea()) {
          case NORTH:
            northList.add(dhm);
            break;
          case WEST:
            westList.add(dhm);
            break;
          case CENTRAL:
            centralList.add(dhm);
            break;
        }
      }
    }
    HashMap<String, ArrayList<DiningHallModel>> finalList = new HashMap<>();
    finalList.put("West", westList);
    finalList.put("North", northList);
    finalList.put("Central", centralList);
    return finalList;
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
