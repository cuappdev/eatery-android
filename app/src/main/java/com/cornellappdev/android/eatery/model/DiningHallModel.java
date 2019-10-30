package com.cornellappdev.android.eatery.model;

import android.content.Context;

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
import java.util.Set;

public class DiningHallModel extends CampusModel implements Serializable {

    private Map<LocalDate, DailyMenuModel> mWeeklyMenu;
    private List<LocalDate> mSortedDates;

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

    // NOTE (yanlam): should probably move this somewhere else, but not sure where.
    /** compareTimes(t1, t2) returns:
     *      -1 if time of t1 comes before t2
     *      0 if t1 is equal to t2
     *      1 if t1 comes after t2
     */
    public static int compareTimes(LocalDateTime t1, LocalDateTime t2) {
        boolean hoursEqual = t1.getHour() == t2.getHour();
        boolean minutesEqual = t1.getMinute() == t2.getMinute();
        if (t1.getHour() < t2.getHour()
                || (hoursEqual && t1.getMinute() < t2.getMinute())
                || (hoursEqual && minutesEqual && t1.getSecond() < t2.getSecond())) {
            return -1;
        }
        if (t1.getHour() > t2.getHour()
                || (hoursEqual && t1.getMinute() > t2.getMinute())
                || (hoursEqual && minutesEqual &&t1.getSecond() > t2.getSecond())) {
            return 1;
        }
        return 0;
    }

    /**
     * getMealIntervals() returns a HashMap of all possible MealTypes and the corresponding
     * time intervals.
     * */
    private HashMap<MealType, Interval> getMealIntervals() {
        HashMap<MealType, Interval> mealIntervalMap = new HashMap<MealType, Interval>();
        for (MealModel mealModel: getCurrentDayMenu().getAllMeals()) {
            Interval i = mealModel.getInterval();
            mealIntervalMap.put(mealModel.getType(), i);
        }
        return mealIntervalMap;
    }

    /**
     * getCurrentMealTypeTabIndex() returns the menu tab index to be displayed, based on the
     * current time. Note that this does NOT represent the index of the corresponding MealType enum.
     * */
    public int getCurrentMealTypeTabIndex() {
        int mealTypeIndex = 4; // Index corresponding to mealType enum.
        int mealTypeTabIndex = 0; // Tab index to return.
        HashMap<MealType, Interval> mealIntervalMap = getMealIntervals();
        Set<MealType> mealIntervalKeys = mealIntervalMap.keySet();
        LocalDateTime currentTime = LocalDateTime.now();
        for(MealType mt: mealIntervalKeys) {
            Interval i = mealIntervalMap.get(mt);
            // Account for absences in mealTypes (ie. no brunch, etc).
            switch (mt) {
                case DINNER:
                    // Always last option.
                    if (compareTimes(currentTime, i.getEnd()) == -1 && mealTypeIndex > mt.getIndex()) {
                        mealTypeTabIndex = mealIntervalKeys.size() - 1;
                        mealTypeIndex = 4;
                    }
                    break;
                case LUNCH:
                    // Either last or second-to-last option, depending if dinner exists.
                    if (compareTimes(currentTime, i.getEnd()) == -1 && mealTypeIndex > mt.getIndex()) {
                        int offset = mealIntervalKeys.contains(MealType.DINNER) ? -2 : -1;
                        mealTypeTabIndex = mealIntervalKeys.size() + offset;
                        mealTypeIndex = 3;
                    }
                    break;
                case BRUNCH:
                    // Either first or second option, depending if breakfast exists.
                    if (compareTimes(currentTime, i.getEnd()) == -1 && mealTypeIndex > mt.getIndex()) {
                        mealTypeTabIndex = mealIntervalKeys.contains(MealType.BREAKFAST) ? 1 : 0;
                        mealTypeIndex = 2;
                    }
                    break;
                case BREAKFAST:
                    // Always first option.
                    if (compareTimes(currentTime, i.getEnd()) == -1 && mealTypeIndex > mt.getIndex()) {
                        mealTypeTabIndex = 0;
                        mealTypeIndex = 1;
                    }
                    break;
            }
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
                // MealType.BREAKFAST, etc.
                MealType type = MealType.fromDescription(event.description());
                // MealModel: type, menu, start, end.
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
