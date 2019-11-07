package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.model.enums.MealType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DiningHallModel extends CampusModel implements Serializable {

    private Map<LocalDate, DailyMenuModel> mWeeklyMenu;
    private List<LocalDate> mSortedDates;

    public static DiningHallModel fromEatery(Context context, boolean hardcoded,
                                             AllEateriesQuery.Eatery eatery) {
        DiningHallModel model = new DiningHallModel();
        model.parseEatery(context, hardcoded, eatery);
        return model;
    }

    /**
     * getMealIntervals returns a HashMap of all possible MealTypes and the corresponding
     * time intervals.
     */
    private HashMap<MealType, Interval> getMealIntervals() {
        HashMap<MealType, Interval> mealIntervalMap = new HashMap<MealType, Interval>();
        for (MealModel mealModel: getCurrentDayMenu().getAllMeals()) {
            mealIntervalMap.put(mealModel.getType(), mealModel.getInterval());
        }
        return mealIntervalMap;
    }

    /**
     * isBeforeMealType returns true if the current time comes before mealType. Otherwise, returns false.
     */
    public boolean isBeforeMealType(MealType mealType, Set<MealType> set, HashMap<MealType, Interval> map) {
        return set.contains(mealType) && LocalDateTime.now().compareTo(map.get(mealType).getEnd()) < 0;
    }

    /**
     * getCurrentMealTypeTabIndex returns the menu tab index to be displayed, based on the
     * current time. Note that this does NOT represent the index of the corresponding MealType enum.
     */
    public int getCurrentMealTypeTabIndex() {
        int mealTypeTabIndex = 0; // Tab index to return.
        HashMap<MealType, Interval> mealIntervalMap = getMealIntervals();
        Set<MealType> mealIntervalKeys = mealIntervalMap.keySet();
        if (isBeforeMealType(MealType.BREAKFAST, mealIntervalKeys, mealIntervalMap)) {
            // Breakfast is always first option if it exists.
            mealTypeTabIndex = 0;
        } else if (isBeforeMealType(MealType.BRUNCH, mealIntervalKeys, mealIntervalMap)) {
            // Brunch is either first or second option, depending if breakfast exists.
            mealTypeTabIndex = mealIntervalKeys.contains(MealType.BREAKFAST) ? 1 : 0;
        } else if (isBeforeMealType(MealType.LUNCH, mealIntervalKeys, mealIntervalMap)) {
            // Lunch is either last or second-to-last option, depending if dinner exists.
            int offset = mealIntervalKeys.contains(MealType.DINNER) ? -2 : -1;
            mealTypeTabIndex = mealIntervalKeys.size() + offset;
        } else if (isBeforeMealType(MealType.DINNER, mealIntervalKeys, mealIntervalMap)) {
            // Dinner is always last option if it exists.
            mealTypeTabIndex = mealIntervalKeys.size() - 1;
        }

        return mealTypeTabIndex;
    }

    public DailyMenuModel getCurrentDayMenu() {
        LocalDate today = LocalDate.now();
        if (mOpenPastMidnight && LocalDateTime.now().getHour() <= 3) {
            today = today.minusDays(1);
        }
        return mWeeklyMenu.get(today);
    }

    private MealModel getCurrentMeal() {
        DailyMenuModel todaysMenu = getCurrentDayMenu();
        if (todaysMenu == null) {
            return null;
        }
        for (MealModel meal : todaysMenu.getAllMeals()) {
            if (meal.containsTime(LocalDateTime.now())) {
                return meal;
            }
        }
        return null;
    }

    private void sortDates() {
        mSortedDates.clear();
        mSortedDates.addAll(mWeeklyMenu.keySet());
        Collections.sort(mSortedDates);
    }

    private LocalDateTime findNextOpen() {
        sortDates();
        for (LocalDate date : mSortedDates) {
            if (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) {
                ArrayList<MealModel> mealsForDate = mWeeklyMenu.get(date).getAllMeals();
                for (MealModel meal : mealsForDate) {
                    if (meal.afterTime(LocalDateTime.now())) {
                        return meal.getStart();
                    }
                }
            }
        }
        return null;
    }

    public MealMenuModel getMealByDateAndType(LocalDate date, MealType mealType) {
        if (mWeeklyMenu.keySet().contains(date)) {
            DailyMenuModel daysMeals = mWeeklyMenu.get(date);
            MealModel mealModel = daysMeals.getMeal(mealType);
            // If there is no meal that matches BREAKFAST, LUNCH, or DINNER, try BRUNCH
            if (mealModel == null) {
                mealModel = daysMeals.getMeal(MealType.BRUNCH);
            }
            // If mealModel is not null, return the menu
            if (mealModel != null) {
                return mealModel.getMenu();
            }
        }
        return null;
    }

    public Interval getIntervalByDateAndType(LocalDate date, MealType mealType) {
        if (mWeeklyMenu.keySet().contains(date)) {
            DailyMenuModel daysMeals = mWeeklyMenu.get(date);
            MealModel mealModel = daysMeals.getMeal(mealType);
            if (mealModel == null) {
                mealModel = daysMeals.getMeal(MealType.BRUNCH);
            }
            if (mealModel != null) {
                return mealModel.getInterval();
            }
        }
        return null;
    }

    public Status getCurrentStatus() {
        MealModel currentMeal = getCurrentMeal();
        if (currentMeal == null) {
            return Status.CLOSED;
        } else if (currentMeal.getEnd().minusMinutes(30).isBefore(LocalDateTime.now())) {
            return Status.CLOSINGSOON;
        }
        return Status.OPEN;
    }

    public LocalDateTime getChangeTime() {
        if (getCurrentStatus() == Status.CLOSINGSOON || getCurrentStatus() == Status.OPEN) {
            MealModel currentMeal = getCurrentMeal();
            return currentMeal.getEnd();
        } else {
            return findNextOpen();
        }
    }

    public MealModel getMenu() {
        return getCurrentMeal();
    }

    public HashSet<String> getMealItems() {
        HashSet<String> items = new HashSet<String>();
        if (getCurrentStatus() == Status.CLOSED) {
            return items;
        }
        MealModel current = getCurrentMeal();
        items.addAll(current.getMenu().getAllItems());
        return items;
    }

    @Override
    public void parseEatery(Context context, boolean hardcoded, AllEateriesQuery.Eatery eatery) {
        super.parseEatery(context, hardcoded, eatery);
        mWeeklyMenu = new HashMap<>();
        mSortedDates = new ArrayList<>();
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mma")
                .toFormatter(Locale.US);
        mId = eatery.id();
        // Each Operating Hour is a single day for dining halls
        for (AllEateriesQuery.OperatingHour operatingHour : eatery.operatingHours()) {
            DailyMenuModel dailyMenuModel = new DailyMenuModel();
            LocalDate localDate = LocalDate.parse(operatingHour.date().toUpperCase());
            for (AllEateriesQuery.Event event : operatingHour.events()) {
                LocalDateTime start = null, end = null;
                start = LocalTime.parse(event.startTime().toUpperCase().substring(11),
                        timeFormatter).atDate(localDate);
                end = LocalTime.parse(event.endTime().toUpperCase().substring(11),
                        timeFormatter).atDate(localDate);
                MealType type = MealType.fromDescription(event.description());
                MealModel mealModel = new MealModel(start, end);
                mealModel.setType(type);
                MealMenuModel mealMenuModel = MealMenuModel.fromGraphQL(event.menu());
                mealModel.setMenu(mealMenuModel);
                if (type != null) {
                    dailyMenuModel.addMeal(type, mealModel);
                }
            }
            mWeeklyMenu.put(localDate, dailyMenuModel);
        }
    }
}
