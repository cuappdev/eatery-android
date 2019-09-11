package com.cornellappdev.android.eatery.model;

import android.content.Context;
import android.util.Log;

import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.model.enums.MealType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiningHallModel extends EateryBaseModel implements Serializable {

    private Map<LocalDate, DailyMenuModel> mWeeklyMenu;
    private List<LocalDate> mSortedDates;
    private List<Swipe> mSwipeDataList;

    public static DiningHallModel fromJSON(Context context, boolean hardcoded, JSONObject eatery)
            throws JSONException {
        DiningHallModel model = new DiningHallModel();
        model.parseJSONObject(context, hardcoded, eatery);
        return model;
    }

    public static DiningHallModel fromEatery(Context context, boolean hardcoded,
            AllEateriesQuery.Eatery eatery) {
        DiningHallModel model = new DiningHallModel();
        model.parseEatery(context, hardcoded, eatery);
        return model;
    }

    public DailyMenuModel getCurrentDayMenu() {
        LocalDate today = LocalDate.now();
        if (mOpenPastMidnight && LocalDateTime.now().getHour() <= 3) {
            today = today.minusDays(1);
        }
        return mWeeklyMenu.get(today);
    }

    public List<Swipe> getSwipeData() {
        return mSwipeDataList;
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
        mSwipeDataList = new ArrayList<>();
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mma")
                .toFormatter(Locale.US);
        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy-MM-dd")
                .toFormatter(Locale.US);

        mId = eatery.id();

        for (AllEateriesQuery.SwipeDatum swipeDatum : eatery.swipeData()) {
            LocalTime start = null, end = null;
            start = LocalTime.parse(swipeDatum.startTime().toUpperCase().replaceAll("\\s+", ""),
                    timeFormatter);
            end = LocalTime.parse(swipeDatum.endTime().toUpperCase().replaceAll("\\s+", ""),
                    timeFormatter);
            mSwipeDataList.add(
                    new Swipe(start, end, swipeDatum.swipeDensity(), swipeDatum.waitTimeLow(),
                            swipeDatum.waitTimeHigh()));
        }
        Collections.sort(mSwipeDataList);

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

    @Override
    public void parseJSONObject(Context context, boolean hardcoded, JSONObject eatery)
            throws JSONException {
        super.parseJSONObject(context, hardcoded, eatery);
        mWeeklyMenu = new HashMap<>();
        mSortedDates = new ArrayList<>();
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mma")
                .toFormatter();
        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy-MM-dd")
                .toFormatter();

        // Each Operating Hour is a single day for dining halls
        JSONArray operatingHours = eatery.getJSONArray("operatingHours");
        mId = eatery.getInt("id");
        for (int k = 0; k < operatingHours.length(); k++) {
            DailyMenuModel dailyMenuModel = new DailyMenuModel();
            JSONObject mealPeriods = operatingHours.getJSONObject(k);
            // Each meal in meals is breakfast, lunch, dinner, etc.
            JSONArray meals = mealPeriods.getJSONArray("events");
            LocalDate localDate;
            if (mealPeriods.has("date")) {
                String rawDate = mealPeriods.getString("date").toUpperCase();
                localDate = LocalDate.parse(rawDate, dateFormatter);
            } else {
                localDate = LocalDate.now();
            }
            // Iterates through each meal in one dining hall
            for (int l = 0; l < meals.length(); l++) {
                // Meal object represents a single meal at the eatery (ie. lunch)
                JSONObject meal = meals.getJSONObject(l);
                LocalDateTime start = null, end = null;

                // Start Time
                if (meal.has("start")) {
                    String rawStart = meal.getString("start").toUpperCase();
                    LocalTime localTime = LocalTime.parse(rawStart, timeFormatter);
                    start = localTime.atDate(localDate);
                }

                // End Time
                if (meal.has("end")) {
                    String rawEnd = meal.getString("end").toUpperCase();
                    LocalTime localTime = LocalTime.parse(rawEnd, timeFormatter);
                    end = localTime.atDate(localDate);
                }
                // Setting which meal of the day this meal is (ie. LUNCH)
                MealType type = MealType.fromDescription(meal.getString("descr"));

                if (start != null && end != null) {
                    MealModel mealModel = new MealModel(start, end);
                    mealModel.setType(type);

                    // Sets the entire menu for the meal (Stations and items at stations)
                    JSONArray menu = meal.getJSONArray("menu");
                    MealMenuModel mealMenuModel = MealMenuModel.fromJSONArray(menu);
                    mealModel.setMenu(mealMenuModel);

                    // We only care about Breakfast, Lunch, Dinner, and Brunch
                    if (type != null) {
                        dailyMenuModel.addMeal(type, mealModel);
                    }
                }
            }
            mWeeklyMenu.put(localDate, dailyMenuModel);
        }
    }
}
