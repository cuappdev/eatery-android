package com.cornellappdev.android.eatery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.CampusArea;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.MealMenuModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.MealType;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class WeeklyMenuFragment extends EateryTabFragment {
  private Chip breakfastChip, lunchChip, dinnerChip;
  private DateButtonGroup dateButtonGroup;
  private List<DateButton> dateList = new ArrayList<>();
  private List<CafeModel> mCafes = new ArrayList<>();
  private List<DiningHallModel> mDiningHalls = new ArrayList<>();
  private List<EateryModel> mEateries;
  private ChipGroup mealTypeChipGroup, areaChipGroup;
  private NonScrollExpandableListView menuExpandableList;
  private CampusArea selectedArea;
  private DayOfWeek selectedDayOfWeek;
  private MealType selectedMealType;

  public WeeklyMenuFragment() {
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final View weeklyMenuFragment = inflater
        .inflate(R.layout.fragment_weekly_menu, container, false);

    mealTypeChipGroup = weeklyMenuFragment.findViewById(R.id.mealTypeChipGroup);
    areaChipGroup = weeklyMenuFragment.findViewById(R.id.areaChipGroup);
    breakfastChip = weeklyMenuFragment.findViewById(R.id.breakfast);
    dinnerChip = weeklyMenuFragment.findViewById(R.id.dinner);
    lunchChip = weeklyMenuFragment.findViewById(R.id.lunch);

    dateButtonGroup = weeklyMenuFragment.findViewById(R.id.dateButtonGroup);
    dateButtonGroup.setOnCheckedChangeListener((group, checkedId) -> dateFilterClick(checkedId));
    menuExpandableList = weeklyMenuFragment.findViewById(R.id.menuList);

    // Populate list of date Chips on header
    dateList.add(weeklyMenuFragment.findViewById(R.id.date0));
    dateList.add(weeklyMenuFragment.findViewById(R.id.date1));
    dateList.add(weeklyMenuFragment.findViewById(R.id.date2));
    dateList.add(weeklyMenuFragment.findViewById(R.id.date3));
    dateList.add(weeklyMenuFragment.findViewById(R.id.date4));
    dateList.add(weeklyMenuFragment.findViewById(R.id.date5));
    dateList.add(weeklyMenuFragment.findViewById(R.id.date6));

    // When changing date, highlight Breakfast Chip
    Context context = getContext();

    LocalDateTime dateTime = LocalDateTime.now();
    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d");
    for (int i = 0; i < 7; i++) {
      // Formatting for each day
      String sDay = dayFormatter.format(dateTime);
      String sDate = dateFormatter.format(dateTime);
      /// SpannableString ssDate = new SpannableString(sDay + '\n' + sDate);
      //ssDate.setSpan(new RelativeSizeSpan(0.8f), 0, 3, 0);
      //ssDate.setSpan(new RelativeSizeSpan(2f), 4, ssDate.length(), 0);
      DateButton tv = dateList.get(i);
      tv.setDateText(sDay, sDate);
      dateTime = dateTime.plusDays(1);
    }
    // Parse the weekly menu when the user starts this activity

    mealTypeChipGroup.setOnCheckedChangeListener((chipGroup, id) -> {
      switch (id) {
        case R.id.breakfast:
          selectedMealType = MealType.BREAKFAST;
          break;
        case R.id.lunch:
          selectedMealType = MealType.LUNCH;
          break;
        case R.id.dinner:
          selectedMealType = MealType.DINNER;
          break;
      }
      changeListAdapter();
    });

    areaChipGroup.setOnCheckedChangeListener((chipGroup, id) -> {
      switch (id) {
        case R.id.north:
          selectedArea = CampusArea.NORTH;
          break;
        case R.id.west:
          selectedArea = CampusArea.WEST;
          break;
        case R.id.central:
          selectedArea = CampusArea.CENTRAL;
          break;
        default:
          selectedArea = null;
      }
      changeListAdapter();
    });

    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    LocalTime time = zonedDateTime.toLocalTime();
    LocalTime lunchCutoff = LocalTime.of(11, 0);
    LocalTime dinnerCutoff = LocalTime.of(16, 0);
    LocalTime breakfastCutoff = LocalTime.of(22, 0);
    selectedDayOfWeek = ZonedDateTime.now().getDayOfWeek();
    selectedArea = CampusArea.NORTH;

    if (time.isAfter(breakfastCutoff) && time.isBefore(lunchCutoff)) {
      selectedMealType = MealType.BREAKFAST;
    } else if (time.isBefore(dinnerCutoff) && time.isAfter(lunchCutoff)) {
      selectedMealType = MealType.LUNCH;
    } else {
      selectedMealType = MealType.DINNER;
    }

    populate();

    return weeklyMenuFragment;
  }

  /**
   * Changes the text color to black when selected
   */
  public void dateFilterClick(@IdRes int dateButtonId) {

    //tv.setTextColor(ContextCompat.getColor(getContext(), R.color.activeFilterText));
    //changeDateColor(tv);

    DayOfWeek dayOfWeek = ZonedDateTime.now().getDayOfWeek();
    int selectedDateOffset = 0;
    switch (dateButtonId) {
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
    changeListAdapter();
  }

  /**
   * Changes the text color to grey if the date is not selected
   */
  public void changeDateColor(Chip v) {
    // for (int i = 0; i < 7; i++) {
    //   if (!dateList.get(i).equals(v)) {
    //     dateList.get(i).setTextColor(Color.parseColor("#cdcdcd"));
    //   }
    // }
  }

  /**
   * Updates the list of dining halls and menus that is displayed
   */
  public void changeListAdapter() {
    populate();
  }

  /**
   * Converts the MealModel object of the map into an Arraylist
   */
  private Map<DiningHallModel, List<MenuListItem>> generateFinalList(
      List<EateryModel> listToParse) {
    Map<DiningHallModel, List<MenuListItem>> finalWest = new TreeMap<>();

    for (EateryModel cafe : listToParse) {
      if (!(cafe instanceof DiningHallModel)) {
        continue;
      }
      if (selectedArea == null || cafe.getArea() == selectedArea) {
        List<MenuListItem> mealToList = new ArrayList<>();
        // Get menu of dining hall
        MealModel m = ((DiningHallModel) cafe)
            .getMenuForDay(selectedDayOfWeek)
            .getMeal(selectedMealType);
        if (m != null) {
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
          finalWest.put((DiningHallModel) cafe, mealToList);
        }

      }
    }
    return finalWest;
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    return true;
  }

  @Override
  public void onDataLoaded(List<EateryModel> eateries) {
    mEateries = new ArrayList<>(eateries);

    // Get list of dining halls
    for (EateryModel m : eateries) {
      if (m instanceof DiningHallModel) {
        mDiningHalls.add((DiningHallModel) m);
      } else if (m instanceof CafeModel) {
        mCafes.add((CafeModel) m);
      }
    }
  }

  public void populate() {
    if (mEateries != null && dateList != null) {
      Map<DiningHallModel, List<MenuListItem>> finalList = generateFinalList(mEateries);
      Context applicationContext = getContext().getApplicationContext();

      menuExpandableList.setVisibility(View.VISIBLE);
      ExpandableListAdapter listAdapterWest = new ExpandableListAdapter(
          applicationContext,
          finalList,
          selectedDayOfWeek,
          selectedMealType,
          mEateries
      );
      menuExpandableList.setAdapter(listAdapterWest);
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

}
