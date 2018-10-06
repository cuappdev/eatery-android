package com.cornellappdev.android.eatery.model;

import android.content.Context;
import com.cornellappdev.android.eatery.util.TimeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class DiningHallModel extends EateryModel {
  private Map<Integer, DiningHallMenuModel> mWeeklyMenu;

  public DiningHallModel() {
    this.mWeeklyMenu = new HashMap<>();
  }

  public static DiningHallModel fromJSON(Context context, boolean hardcoded, JSONObject eatery)
      throws JSONException {
    DiningHallModel model = new DiningHallModel();
    model.parseJSONObject(context, hardcoded, eatery);
    return model;
  }

  public DiningHallMenuModel getMenuForDay(DayOfWeek day) {
    DiningHallMenuModel val = mWeeklyMenu.get(day.getValue());
    if (val != null) {
      return val;
    }
    return DiningHallMenuModel.EMPTY;
  }

  public Map<DayOfWeek, DiningHallMenuModel> getWeeklyMenu() {
    Map<DayOfWeek, DiningHallMenuModel> mapping = new HashMap<>();
    for (Entry<Integer, DiningHallMenuModel> entry : mWeeklyMenu.entrySet()) {
      mapping.put(DayOfWeek.of(entry.getKey()), entry.getValue());
    }

    /*
     * This ensures that every possible value contains at least an empty list,
     * ensuring that it is easy to iterate over.
     */
    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      if (!mapping.containsKey(dayOfWeek)) {
        mapping.put(dayOfWeek, DiningHallMenuModel.EMPTY);
      }
    }

    return mapping;
  }

  private void setMenuForDay(DayOfWeek day, DiningHallMenuModel mWeeklyMenu) {
    this.mWeeklyMenu.put(day.getValue(), mWeeklyMenu);
  }

  public List<String> getMealItems() {
    List<String> items = new ArrayList<>();
    if (isOpen()) {
      LocalDateTime now = LocalDateTime.now();
      for (Map.Entry<DayOfWeek, DiningHallMenuModel> day : getWeeklyMenu().entrySet()) {
        for (MealModel meal : day.getValue()) {
          LocalDateTime startTime = meal.getStart(), endTime = meal.getEnd();
          if (now.isAfter(startTime) && now.isBefore(endTime)) {
            MealMenuModel menu = meal.getMenu();
            items.addAll(menu.getAllItems());
          }
        }
      }
    }
    return items;
  }

  @Override
  public ZonedDateTime getNextOpening() {
    ZonedDateTime time = ZonedDateTime.now();
    ZonedDateTime closestStartTime = null;
    Map<DayOfWeek, DiningHallMenuModel> weeklyMenu = getWeeklyMenu();
    DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();

    /*
     * Loop through every possible day of the week value, note i is incremented and
     * immediately returned
     */
    for (int i = 0; i < DayOfWeek.values().length; i++) {
      currentDay = currentDay.plus(i);
      DiningHallMenuModel menu = weeklyMenu.get(currentDay);
      for (MealModel meal : menu) {
        ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();
        ZonedDateTime startTime = meal.getStart().atZone(cornell);
        if (startTime.isAfter(time) && (closestStartTime == null || closestStartTime
            .isAfter(startTime))) {
          closestStartTime = startTime;
        }
      }
    }
    return closestStartTime;
  }

  public String stringTo() {
    String info = "Name/mNickName: " + mName + "/" + mNickName;
    String locationString = "Location: " + ", Area: " + mArea;
    String payMethodsString = "Pay Methods: " + mPayMethods.toString();
    StringBuilder menuString = new StringBuilder();
    for (Map.Entry<DayOfWeek, DiningHallMenuModel> day : getWeeklyMenu().entrySet()) {
      for (MealModel meal : day.getValue()) {
        menuString.append(meal.stringTo());
      }
    }
    return info + "\n" + locationString + "\n" + payMethodsString + "\n" + "Menu" + "\n"
        + menuString;
  }

  @Override
  public Status getCurrentStatus() {
    ZonedDateTime now = ZonedDateTime.now();
    DiningHallMenuModel mealModels = getMenuForDay(now.getDayOfWeek());
    for (MealModel meal : mealModels) {
      ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();
      ZonedDateTime startTime = meal.getStart().atZone(cornell);
      ZonedDateTime endTime = meal.getEnd().atZone(cornell);
      if (now.isAfter(startTime) && now.isBefore(endTime)) {
        if (endTime.toEpochSecond() - now.toEpochSecond() < (60 * 30)) { // TODO
          // 60 seconds in 1 minute, 30 minutes = half-hour,
          return Status.CLOSING_SOON;
        }
        return Status.OPEN;
      }
    }
    if (isOpenPastMidnight()) {
      mealModels = getMenuForDay(now.minusDays(1).getDayOfWeek());
      MealModel openPeriod = mealModels.getLastMeal();
      ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();
      ZonedDateTime startTime = openPeriod.getStart().atZone(cornell);
      ZonedDateTime endTime = openPeriod.getEnd().atZone(cornell);
      if (endTime.toLocalDate().isEqual(now.toLocalDate()) && now.isAfter(startTime) && now
          .isBefore(endTime)) {
        if (endTime.toEpochSecond() - now.toEpochSecond() < (60 * 30)) { // TODO
          // 60 seconds in 1 minute, 30 minutes = half-hour,
          return Status.CLOSING_SOON;
        }
        return Status.OPEN;
      }
    }
    return Status.CLOSED;
  }

  @Override
  public ZonedDateTime getCloseTime() {
    ZonedDateTime now = ZonedDateTime.now();
    DiningHallMenuModel mealModels = getMenuForDay(now.getDayOfWeek());
    for (MealModel meal : mealModels) {
      ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();
      ZonedDateTime startTime = meal.getStart().atZone(cornell);
      ZonedDateTime endTime = meal.getEnd().atZone(cornell);
      if (now.isAfter(startTime) && now.isBefore(endTime)) {
        return endTime;
      }
    }
    if (isOpenPastMidnight()) {
      mealModels = getMenuForDay(now.minusDays(1).getDayOfWeek());
      MealModel openPeriod = mealModels.getLastMeal();
      ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();
      ZonedDateTime startTime = openPeriod.getStart().atZone(cornell);
      ZonedDateTime endTime = openPeriod.getEnd().atZone(cornell);
      if (endTime.toLocalDate().isEqual(now.toLocalDate()) && now.isAfter(startTime) && now
          .isBefore(endTime)) {
        return endTime;
      }
    }
    return null;
  }

  @Override
  public void parseJSONObject(Context context, boolean hardcoded, JSONObject eatery)
      throws JSONException {
    super.parseJSONObject(context, hardcoded, eatery);
    JSONArray operatingHours = eatery.getJSONArray("operatingHours");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mma",
        context.getResources().getConfiguration().locale);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd",
        context.getResources().getConfiguration().locale);
    mId = eatery.getInt("id");
    // Note(lesley): A single operatingHours object contains operating times for a
    // single day
    for (int k = 0; k < operatingHours.length(); k++) {
      // mealModelArray contains all mealModel objects for a single eatery
      DiningHallMenuModel menuModel = new DiningHallMenuModel();
      JSONObject mealPeriods = operatingHours.getJSONObject(k);
      JSONArray events = mealPeriods.getJSONArray("events");
      LocalDate localDate;
      if (mealPeriods.has("date")) {
        String rawDate = mealPeriods.getString("date").toUpperCase();
        localDate = LocalDate.parse(rawDate, dateFormatter);
      } else {
        localDate = LocalDate.now();
      }
      // Iterates through each meal in one dining hall
      for (int l = 0; l < events.length(); l++) {
        // Meal object represents a single meal at the eatery (ie. lunch)
        JSONObject meal = events.getJSONObject(l);
        LocalDateTime start = null, end = null;

        /* Start Time */
        if (meal.has("start")) {
          String rawStart = meal.getString("start").toUpperCase();
          LocalTime localTime = LocalTime.parse(rawStart, timeFormatter);
          start = localTime.atDate(localDate);
        }

        /* End Time */
        if (meal.has("end")) {
          String rawEnd = meal.getString("end").toUpperCase();
          LocalTime localTime = LocalTime.parse(rawEnd, timeFormatter);
          end = localTime.atDate(localDate);
        }

        /* Meal Type */
        MealType type = MealType.fromDescription(meal.getString("descr"));
        if (type == null) {
          type = MealType.BREAKFAST; // TODO should this be the default?
        }
        if (start != null && end != null) {
          MealModel mealModel = new MealModel(start, end);
          mealModel.setType(type);

          /* Meal Menu */
          JSONArray menu = meal.getJSONArray("menu");
          MealMenuModel mealMenuModel = MealMenuModel.fromJSONArray(menu);
          mealModel.setMenu(mealMenuModel);
          menuModel.setMeal(type, mealModel);
        }
      }
      setMenuForDay(localDate.getDayOfWeek(), menuModel);
    }
  }
}
