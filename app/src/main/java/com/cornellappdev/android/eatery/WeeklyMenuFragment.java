package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.MealMenuModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.MealType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class WeeklyMenuFragment extends Fragment {


  private ExpandableListAdapter listAdapterWest, listAdapterNorth, listAdapterCentral;
  private NonScrollExpandableListView expListViewWest, expListViewNorth, expListViewCentral;
  private TextView westText, northText, centralText;
  private List<CafeModel> mCafes = new ArrayList<>();
  private List<DiningHallModel> mDiningHalls = new ArrayList<>();
  private List<EateryModel> mEateries = new ArrayList<>();
  private MealType mealType;
  private DayOfWeek selectedDayOfWeek;
  private TextView breakfastText;
  private TextView lunchText;
  private TextView dinnerText;
  private LinearLayout linDate;
  private List<TextView> dateList = new ArrayList<>();
  private Map<DayOfWeek, List<Map<String, MealModel>>> weeklyMenu;
  private int lastExpandedPosition;
  private NonScrollExpandableListView lastClickedListView;

  public void updateEateries(List<? extends EateryModel> eateries) {
    for (EateryModel m : eateries) {
      if (m instanceof DiningHallModel) {
        mDiningHalls.add((DiningHallModel) m);
      } else if (m instanceof CafeModel) {
        mCafes.add((CafeModel) m);
      }
    }
    mEateries.addAll(eateries);

    // Parse the weekly menu when the user starts this activity
    weeklyMenu = parseWeeklyMenu(dateList);

    changeListAdapter(MealType.BREAKFAST, selectedDayOfWeek);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    /* Setup reports recycler view... */

    super.onCreate(savedInstanceState);

    final View weeklyMenuFragment = inflater
        .inflate(R.layout.activity_weekly_menu, container, false);

    breakfastText = weeklyMenuFragment.findViewById(R.id.breakfast);
    lunchText = weeklyMenuFragment.findViewById(R.id.lunch);
    dinnerText = weeklyMenuFragment.findViewById(R.id.dinner);
    linDate = weeklyMenuFragment.findViewById(R.id.lin_date);
    expListViewWest = weeklyMenuFragment.findViewById(R.id.expandablelistview_west);
    expListViewNorth = weeklyMenuFragment.findViewById(R.id.expandablelistview_north);
    expListViewCentral = weeklyMenuFragment.findViewById(R.id.expandablelistview_central);
    westText = weeklyMenuFragment.findViewById(R.id.west_header);
    northText = weeklyMenuFragment.findViewById(R.id.north_header);
    centralText = weeklyMenuFragment.findViewById(R.id.central_header);

    // Layout for menu list
    //setTitle("Upcoming Menus");
    DisplayMetrics dm = new DisplayMetrics();
    //getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    expListViewWest.setIndicatorBounds(width - 250, width);
    expListViewNorth.setIndicatorBounds(width - 250, width);
    expListViewCentral.setIndicatorBounds(width - 250, width);

    lastExpandedPosition = -1;
    lastClickedListView = null;

    expListViewCentral.setOnGroupExpandListener(
        i -> {
          if (lastClickedListView != null
              && lastExpandedPosition != -1
              && i != lastExpandedPosition) {
            lastClickedListView.collapseGroup(lastExpandedPosition);
          }
          lastExpandedPosition = i;
          lastClickedListView = expListViewCentral;
        });

    expListViewWest.setOnGroupExpandListener(
        i -> {
          if (lastClickedListView != null
              && lastExpandedPosition != -1
              && i != lastExpandedPosition) {
            lastClickedListView.collapseGroup(lastExpandedPosition);
          }
          lastExpandedPosition = i;
          lastClickedListView = expListViewWest;
        });
    expListViewNorth.setOnGroupExpandListener(
        i -> {
          if (lastClickedListView != null
              && lastExpandedPosition != -1
              && i != lastExpandedPosition) {
            lastClickedListView.collapseGroup(lastExpandedPosition);
          }
          lastExpandedPosition = i;
          lastClickedListView = expListViewNorth;
        });

    // Populate list of date TextViews on header
    dateList.add((TextView) weeklyMenuFragment.findViewById(R.id.date0));
    dateList.add((TextView) weeklyMenuFragment.findViewById(R.id.date1));
    dateList.add((TextView) weeklyMenuFragment.findViewById(R.id.date2));
    dateList.add((TextView) weeklyMenuFragment.findViewById(R.id.date3));
    dateList.add((TextView) weeklyMenuFragment.findViewById(R.id.date4));
    dateList.add((TextView) weeklyMenuFragment.findViewById(R.id.date5));
    dateList.add((TextView) weeklyMenuFragment.findViewById(R.id.date6));

    for (TextView tv : dateList) {
      tv.setOnClickListener(this::dateFilterClick);
    }

    final Context context = getContext();

    // When changing date, highlight Breakfast textview
    linDate.setOnClickListener(
        view -> {
          breakfastText.setTextColor(ContextCompat.getColor(context, R.color.activeMealText));
          lunchText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
          dinnerText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
        });

    selectedDayOfWeek = ZonedDateTime.now().getDayOfWeek();

    LocalDateTime dateTime = LocalDateTime.now();

    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d");

    for (int i = 0; i < 7; i++) {
      // Formatting for each day

      String sDay = dayFormatter.format(dateTime);
      String sDate = dateFormatter.format(dateTime);

      SpannableString ssDate = new SpannableString(sDay + '\n' + sDate);
      ssDate.setSpan(new RelativeSizeSpan(0.8f), 0, 3, 0);
      ssDate.setSpan(new RelativeSizeSpan(2f), 4, ssDate.length(), 0);
      TextView tv = dateList.get(i);
      tv.setText(ssDate);

      dateTime = dateTime.plusDays(1);
    }

    breakfastText.setOnClickListener(
        view -> {
          breakfastText.setTextColor(Color.parseColor("#000000"));
          lunchText.setTextColor(Color.parseColor("#cdcdcd"));
          dinnerText.setTextColor(Color.parseColor("#cdcdcd"));

          mealType = MealType.BREAKFAST;
          changeListAdapter(mealType, selectedDayOfWeek);
        });

    lunchText.setOnClickListener(
        view -> {
          breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
          lunchText.setTextColor(Color.parseColor("#000000"));
          dinnerText.setTextColor(Color.parseColor("#cdcdcd"));

          mealType = MealType.LUNCH;
          changeListAdapter(mealType, selectedDayOfWeek);
        });

    dinnerText.setOnClickListener(
        view -> {
          breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
          lunchText.setTextColor(Color.parseColor("#cdcdcd"));
          dinnerText.setTextColor(Color.parseColor("#000000"));

          mealType = MealType.DINNER;
          changeListAdapter(mealType, selectedDayOfWeek);
        });

    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    LocalTime time = zonedDateTime.toLocalTime();

    LocalTime lunchCutoff = LocalTime.of(11, 0);
    LocalTime dinnerCutoff = LocalTime.of(16, 0);
    LocalTime breakfastCutoff = LocalTime.of(22, 0);

    DayOfWeek currentDayOfWeek = ZonedDateTime.now().getDayOfWeek();

    if (time.isAfter(breakfastCutoff) && time.isBefore(lunchCutoff)) {
      changeListAdapter(MealType.BREAKFAST, currentDayOfWeek);
      mealType = MealType.BREAKFAST;

      breakfastText.setTextColor(ContextCompat.getColor(context, R.color.activeMealText));
      lunchText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      dinnerText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
    } else if (time.isBefore(dinnerCutoff) && time.isAfter(lunchCutoff)) {
      changeListAdapter(MealType.LUNCH, currentDayOfWeek);
      mealType = MealType.LUNCH;

      breakfastText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      lunchText.setTextColor(ContextCompat.getColor(context, R.color.activeMealText));
      dinnerText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
    } else {
      changeListAdapter(MealType.DINNER, currentDayOfWeek);
      mealType = MealType.DINNER;

      breakfastText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      lunchText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      dinnerText.setTextColor(ContextCompat.getColor(context, R.color.activeMealText));
    }

    return weeklyMenuFragment;
  }

  @Override
  public void onResume() {
    super.onResume();
    Context context = getContext();
    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    LocalTime time = zonedDateTime.toLocalTime();

    LocalTime lunchCutoff = LocalTime.of(11, 0);
    LocalTime dinnerCutoff = LocalTime.of(16, 0);
    LocalTime breakfastCutoff = LocalTime.of(22, 0);

    DayOfWeek currentDayOfWeek = ZonedDateTime.now().getDayOfWeek();

    if (time.isAfter(breakfastCutoff) && time.isBefore(lunchCutoff)) {
      changeListAdapter(MealType.BREAKFAST, currentDayOfWeek);
      mealType = MealType.BREAKFAST;

      breakfastText.setTextColor(ContextCompat.getColor(context, R.color.activeMealText));
      lunchText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      dinnerText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
    } else if (time.isBefore(dinnerCutoff) && time.isAfter(lunchCutoff)) {
      changeListAdapter(MealType.LUNCH, currentDayOfWeek);
      mealType = MealType.LUNCH;

      breakfastText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      lunchText.setTextColor(ContextCompat.getColor(context, R.color.activeMealText));
      dinnerText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
    } else {
      changeListAdapter(MealType.DINNER, currentDayOfWeek);
      mealType = MealType.DINNER;

      breakfastText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      lunchText.setTextColor(ContextCompat.getColor(context, R.color.inactiveMealText));
      dinnerText.setTextColor(ContextCompat.getColor(context, R.color.activeMealText));
    }

  }

  /**
   * Changes the text color to black when selected
   */
  public void dateFilterClick(View v) {
    TextView tv = (TextView) v;

    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.activeFilterText));
    changeDateColor(tv);

    int id = tv.getId();

    DayOfWeek dayOfWeek = ZonedDateTime.now().getDayOfWeek();

    int selectedDateOffset = 0;

    switch (id) {
      case R.id.date0:
        selectedDateOffset = 0;
        break;
      case R.id.date1:
        selectedDateOffset = 1;
        break;
      case R.id.date2:
        selectedDateOffset = 2;
        break;
      case R.id.date3:
        selectedDateOffset = 3;
        break;
      case R.id.date4:
        selectedDateOffset = 4;
        break;
      case R.id.date5:
        selectedDateOffset = 5;
        break;
      case R.id.date6:
        selectedDateOffset = 6;
        break;
    }

    selectedDayOfWeek = dayOfWeek.plus(selectedDateOffset);

    changeListAdapter(mealType, selectedDayOfWeek);
  }

  /**
   * Changes the text color to grey if the date is not selected
   */
  public void changeDateColor(TextView v) {
    for (int i = 0; i < 7; i++) {
      if (!dateList.get(i).equals(v)) {
        dateList.get(i).setTextColor(Color.parseColor("#cdcdcd"));
      }
    }
  }

  /**
   * Updates the list of dining halls and menus that is displayed
   */
  public void changeListAdapter(MealType mealType, DayOfWeek dayOfWeek) {
    int mealIndex = 0;
    switch (mealType) {
      case BREAKFAST:
        mealIndex = 0;
        break;
      case LUNCH:
        mealIndex = 1;
        break;
      case DINNER:
        mealIndex = 2;
        break;
    }

    List<Map<String, MealModel>> dayMenu = weeklyMenu.get(dayOfWeek);

    if (dayMenu != null) {
      Map<String, MealModel> mealMenu = dayMenu.get(mealIndex);

      if (mealMenu != null) {

        MenusByCampusArea finalList = generateFinalList(mealMenu);

        // Hides layout elements if there is nothing in the list corresponding to a certain
        // CafeteriaArea
        if (finalList.west.isEmpty()) {
          westText.setVisibility(View.GONE);
          expListViewWest.setVisibility(View.GONE);
        } else {
          westText.setVisibility(View.VISIBLE);
          expListViewWest.setVisibility(View.VISIBLE);

          listAdapterWest = new ExpandableListAdapter(
              getContext().getApplicationContext(),
              finalList.west,
              dayOfWeek,
              mealIndex,
              mEateries
          );
          expListViewWest.setAdapter(listAdapterWest);
        }

        if (finalList.north.isEmpty()) {
          northText.setVisibility(View.GONE);
          expListViewNorth.setVisibility(View.GONE);
        } else {
          northText.setVisibility(View.VISIBLE);
          expListViewNorth.setVisibility(View.VISIBLE);

          listAdapterNorth = new ExpandableListAdapter(
              getContext().getApplicationContext(),
              finalList.north,
              dayOfWeek, mealIndex,
              mEateries
          );
          expListViewNorth.setAdapter(listAdapterNorth);
        }

        if (finalList.central.isEmpty()) {
          centralText.setVisibility(View.GONE);
          expListViewCentral.setVisibility(View.GONE);
        } else {
          centralText.setVisibility(View.VISIBLE);
          expListViewCentral.setVisibility(View.VISIBLE);

          listAdapterCentral = new ExpandableListAdapter(
              getContext().getApplicationContext(),
              finalList.central,
              dayOfWeek, mealIndex,
              mEateries
          );
          expListViewCentral.setAdapter(listAdapterCentral);
        }
      }
    }
  }

  /**
   * Generates a list of for breakfast, lunch, and dinner for a particular day. Day is determined by
   * the dateOffset from the current time.
   */
  public List<Map<String, MealModel>> generateMealLists(DayOfWeek dayOfWeek) {
    List<Map<String, MealModel>> finalList = new ArrayList<>();
    Map<String, MealModel> breakfastList = new TreeMap<>();
    Map<String, MealModel> lunchList = new TreeMap<>();
    Map<String, MealModel> dinnerList = new TreeMap<>();

    for (DiningHallModel m : mDiningHalls) {
      // Checks that dining hall is opened

      // Get MealModel for the day and split into three hashmaps
      List<MealModel> meals = m.getWeeklyMenu().get(dayOfWeek);

      for (MealModel n : meals) {
        if (n.getMenu().getNumberOfCategories() > 0) {
          MealType type = n.getType();

          if ((type == MealType.BREAKFAST || type == MealType.BRUNCH)) {
            breakfastList.put(m.getNickName(), n);
          }

          if (type == MealType.LUNCH
              || type == MealType.BRUNCH
              || type == MealType.LITE_LUNCH) {
            lunchList.put(m.getNickName(), n);
          }
          if (type == MealType.DINNER) {
            dinnerList.put(m.getNickName(), n);
          }
        }
      }
    }

    finalList.add(breakfastList);
    finalList.add(lunchList);
    finalList.add(dinnerList);

    return finalList;
  }

  private class MenusByCampusArea {

    @NonNull
    final Map<String, List<MenuListItem>> west, north, central;

    MenusByCampusArea(@NonNull Map<String, List<MenuListItem>> west,
        @NonNull Map<String, List<MenuListItem>> north,
        @NonNull Map<String, List<MenuListItem>> central) {
      this.west = west;
      this.north = north;
      this.central = central;
    }
  }

  private class MenuListCategory extends MenuListItem {

    MenuListCategory(String item) {
      super(item);
    }
  }

  class MenuListItem {

    private final String item;

    MenuListItem(String item) {
      this.item = item;
    }

    public String getItem() {
      return item;
    }
  }

  /**
   * Converts the MealModel object of the map into an Arraylist
   */
  private MenusByCampusArea generateFinalList(Map<String, MealModel> listToParse) {
    Map<String, List<MenuListItem>> finalWest = new TreeMap<>();
    Map<String, List<MenuListItem>> finalNorth = new TreeMap<>();
    Map<String, List<MenuListItem>> finalCentral = new TreeMap<>();

    for (Map.Entry<String, MealModel> cafe : listToParse.entrySet()) {
      List<MenuListItem> mealToList = new ArrayList<>();

      // Get menu of dining hall
      MealModel m = cafe.getValue();
      MealMenuModel menuModel = m.getMenu();

      // Add both category + meal items into an ArrayList
      for (String category : menuModel.getCategories()) {
        mealToList.add(new MenuListCategory(category));

        List<String> values = menuModel.getItems(category);

        if (values != null) {
          for (String item : values) {
            mealToList.add(new MenuListItem(item));
          }
        }
      }

      EateryModel myCafe = null;
      int count = 0;

      while (count < mEateries.size()) {
        if (mEateries.get(count).getNickName().equals(cafe.getKey())) {
          myCafe = mEateries.get(count);
        }
        count++;
      }

      if (myCafe != null) {
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
      }
    }

    return new MenusByCampusArea(finalWest, finalNorth, finalCentral);
  }

  /**
   * Assume that dateList is not null. Returns an Arraylist of the set(breakfastlist, lunchlist,
   * dinnerlist) for each day in the dateList. Each of the meal lists is a hashmap of EateryModels
   * that have that specific meal and the menu
   */
  public Map<DayOfWeek, List<Map<String, MealModel>>> parseWeeklyMenu(
      @NonNull List<TextView> dateList) {
    Map<DayOfWeek, List<Map<String, MealModel>>> mainList = new HashMap<>();

    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      // List contains set(breakfastlist, lunchlist, dinnerlist) for the day
      List<Map<String, MealModel>> dailyList = generateMealLists(dayOfWeek);
      mainList.put(dayOfWeek, dailyList);
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
