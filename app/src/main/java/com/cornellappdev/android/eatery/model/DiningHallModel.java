package com.cornellappdev.android.eatery.model;

import android.content.Context;
import com.cornellappdev.android.eatery.TimeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Evan Welsh on 10/2/18.
 */
public class DiningHallModel extends EateryModel {

  private List<List<MealModel>> mWeeklyMenu;


  public DiningHallModel() {
    this.mWeeklyMenu = new ArrayList<>();
  }

  public static DiningHallModel fromJSON(Context context, boolean hardcoded, JSONObject eatery)
      throws JSONException {
    DiningHallModel model = new DiningHallModel();
    model.parseJSONObject(context, hardcoded, eatery);
    return model;
  }

  public List<List<MealModel>> getWeeklyMenu() {
    return mWeeklyMenu;
  }

  private void setWeeklyMenu(List<List<MealModel>> mWeeklyMenu) {
    this.mWeeklyMenu = mWeeklyMenu;
  }

  public List<String> getMealItems() {
    List<String> items = new ArrayList<>();
    Status status = getCurrentStatus();

    if (status.isOpen()) {
      LocalDateTime now = LocalDateTime.now();

      for (List<MealModel> day : getWeeklyMenu()) {
        for (MealModel meal : day) {
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

  public int indexOfCurrentDay() {
    LocalDate now = LocalDate.now();

    for (int i = 0; i < getWeeklyMenu().size(); i++) {
      List<MealModel> day = getWeeklyMenu().get(i);
      if (day.size() > 0) {
        MealModel firstMeal = day.get(0);

        if (firstMeal.getStart().toLocalDate().isEqual(now)) {
          return i;
        }
      }
    }

    return 0;
  }

  @Override
  public LocalDateTime getNextOpening(LocalDateTime time) {
    for (List<MealModel> day : getWeeklyMenu()) {
      for (MealModel meal : day) {
        if (meal.getStart().isBefore(time) && meal.getEnd().isAfter(time)) {
          return time;
        } else if (meal.getStart().isAfter(time)) {
          return meal.getStart();
        }
      }
    }
    return null;
  }

  public String stringTo() {
    String info = "Name/mNickName: " + mName + "/" + mNickName;
    String locationString = "Location: " + ", Area: " + mArea;
    String payMethodsString = "Pay Methods: " + mPayMethods.toString();
    StringBuilder menuString = new StringBuilder();

    for (List<MealModel> meal : getWeeklyMenu()) {
      for (MealModel mealIndiv : meal) {
        menuString.append(mealIndiv.stringTo());
      }

    }

    return info + "\n" + locationString + "\n" + payMethodsString + "\n" + "Menu" + "\n"
        + menuString;
  }

  @Override
  public Status getCurrentStatus() {
    ZonedDateTime now = ZonedDateTime.now();

    for (List<MealModel> day : getWeeklyMenu()) {
      for (MealModel meal : day) {
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
    }

    return Status.CLOSED;
  }

  @Override
  public void parseJSONObject(Context context, boolean hardcoded, JSONObject eatery) throws
      JSONException {
    super.parseJSONObject(context, hardcoded, eatery);
    List<List<MealModel>> weeklyMenu = new ArrayList<>();
    JSONArray operatingHours = eatery.getJSONArray("operatingHours");

    DateTimeFormatter timeFormatter = DateTimeFormatter
        .ofPattern("h:mma", context.getResources().getConfiguration().locale);
    DateTimeFormatter dateFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd", context.getResources().getConfiguration().locale);

    this.id = eatery.getInt("id");

    // Note(lesley): A single operatingHours object contains operating times for a single day
    for (int k = 0; k < operatingHours.length(); k++) {
      // mealModelArray contains all mealModel objects for a single eatery
      List<MealModel> mealModelArray = new ArrayList<>();
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
        MealModel mealModel = new MealModel();
        // Meal object represents a single meal at the eatery (ie. lunch)
        JSONObject meal = events.getJSONObject(l);

        /* Start Time */

        if (meal.has("start")) {
          String rawStart = meal.getString("start").toUpperCase();
          LocalTime localTime = LocalTime.parse(rawStart, timeFormatter);

          mealModel.setStart(localTime.atDate(localDate));
        }

        /* End Time */

        if (meal.has("end")) {
          String rawEnd = meal.getString("end").toUpperCase();
          LocalTime localTime = LocalTime.parse(rawEnd, timeFormatter);

          mealModel.setEnd(localTime.atDate(localDate));
        }

        /* Meal Type */

        MealType type = MealType.fromDescription(meal.getString("descr"));

        if (type == null) {
          type = MealType.BREAKFAST; // TODO should this be the default?
        }

        mealModel.setType(type);

        /* Meal Menu */

        JSONArray menu = meal.getJSONArray("menu");
        MealMenuModel menuModel = MealMenuModel.fromJSONArray(menu);

        mealModel.setMenu(menuModel);
        mealModelArray.add(mealModel);
      }

      weeklyMenu.add(mealModelArray);
    }
    setWeeklyMenu(weeklyMenu);
  }
}
