package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.Interval;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CafeModel extends EateryBaseModel implements Serializable {
  private static Set<String> HARDCODED_CAFE_ITEMS = new HashSet<>(Arrays.asList("Starbucks Coffees",
      "Pepsi Beverages",
      "Breakfast Menu",
      "Salads",
      "Soup",
      "Chili",
      "Personal Pizzas",
      "Burgers",
      "Chicken Tenders",
      "Quesadillas",
      "Burritos",
      "Tacos",
      "Hot Wraps",
      "Bok Choy",
      "Fried Rice",
      "Lo Mein",
      "Baked Goods"));
  private List<String> mCafeMenu;
  private Map<LocalDate, List<Interval>> mHours;

  public CafeModel() {
    this.mCafeMenu = mCafeMenu;
    this.mHours = mHours;
  }

  public static CafeModel fromJSONObject(Context context, boolean hardcoded, JSONObject cafe)
      throws JSONException {
    CafeModel model = new CafeModel();
    model.parseJSONObject(context, hardcoded, cafe);
    return model;
  }

  @Override
  public LocalDateTime getChangeTime() {
    return null;
  }

  @Override
  public MealModel getMenu() {
    return null;
  }

  public List<String> getCafeMenu(){
    return mCafeMenu;
  }

  @Override
  public Status getCurrentStatus() {
    return null;
  }

  public void setHours(LocalDate date, List<Interval> hours) {
    List<Interval> sortedHours = new ArrayList<>(hours);
    Collections.sort(sortedHours, Interval::compareTo);
    this.mHours.put(date, sortedHours);
  }

  public void parseJSONObject(Context context, boolean hardcoded, JSONObject cafe)
      throws JSONException {
    super.parseJSONObject(context, hardcoded, cafe);
    List<String> cafeItems = new ArrayList<>();
    // Parse cafe items available at eatery
    JSONArray diningItems = cafe.getJSONArray("diningItems");
    for (int z = 0; z < diningItems.length(); z++) {
      JSONObject item = diningItems.getJSONObject(z);
      cafeItems.add(item.getString("item"));
    }
    // Note(lesley): trillium is missing cafe items data in the JSON, so it requires
    // hardcoded items
    if (mName.equalsIgnoreCase("trillium")) {
      cafeItems.addAll(HARDCODED_CAFE_ITEMS);
    }
    mCafeMenu = cafeItems;
    JSONArray operatingHours = cafe.getJSONArray("operatingHours");
    for (int c = 0; c < operatingHours.length(); c++) {
      // First entry represents opening time, second entry represents closing time
      JSONObject operatingPeriod = operatingHours.getJSONObject(c);
      JSONArray events = operatingPeriod.getJSONArray("events");
      List<LocalDate> localDates = new ArrayList<>();
      if (operatingPeriod.has("date")) {
        String rawDate = operatingPeriod.getString("date");
        LocalDate localDate = LocalDate.parse(rawDate, DateTimeFormatter
            .ofPattern("yyyy-MM-dd", context.getResources().getConfiguration().locale));
        localDates.add(localDate);
      }
      if (operatingPeriod.has("weekday")) {
        LocalDate localDate = LocalDate.now(TimeUtil.getInstance().getCornellTimeZone());
        String rawDays = operatingPeriod.getString("weekday").toUpperCase();
        String[] rawDayArr = rawDays.split("-");
        String rawEnd = null, rawStart = rawDayArr[0].trim();
        if (rawDayArr.length > 1) {
          rawEnd = rawDayArr[1].trim();
        }
        DayOfWeek start = DayOfWeek.valueOf(rawStart);
        if (rawEnd != null) {
          DayOfWeek end = DayOfWeek.valueOf(rawEnd);
          DayOfWeek endPlusOne = end.plus(1);
          for (DayOfWeek current = start; !current.equals(endPlusOne); current = current.plus(1)) {
            LocalDate nextLocalDateOfCurrent = localDate
                .with(TemporalAdjusters.nextOrSame(current));
            localDates.add(nextLocalDateOfCurrent);
          }
        } else {
          localDate = localDate.with(TemporalAdjusters.nextOrSame(start));
          localDates.add(localDate);
        }
      }
      for (LocalDate localDate : localDates) {
        List<Interval> dailyHours = new ArrayList<>();
        for (int e = 0; e < events.length(); e++) {
          JSONObject obj = events.getJSONObject(e);
          LocalDateTime start = null, end = null;
          dailyHours = new ArrayList<>();
          // TODO Don't read start again for everydate (not a huge performance impact)
          if (obj.has("start")) {
            // TODO: Java only parses AM/PM (not am/pm), so add a case insensitive parser
            String rawStart = obj.getString("start").toUpperCase();
            LocalTime localTime = LocalTime.parse(rawStart, DateTimeFormatter
                .ofPattern("h:mma", context.getResources().getConfiguration().locale));
            start = localTime.atDate(localDate);
          }
          if (obj.has("end")) {
            String rawEnd = obj.getString("end").toUpperCase();
            LocalTime localTime = LocalTime.parse(rawEnd, DateTimeFormatter
                .ofPattern("h:mma", context.getResources().getConfiguration().locale));
            end = localTime.atDate(localDate);
          }
          LocalDateTime midnightTomorrow = localDate.atTime(LocalTime.MIDNIGHT);
          if (start != null && end != null) {
            if (end.isBefore(start) && (end.isEqual(midnightTomorrow) || end
                .isAfter(midnightTomorrow))) {
              mOpenPastMidnight = true;
              end = end.plusDays(1);
            }
            dailyHours.add(new Interval(start, end));
          }
        }
        setHours(localDate, dailyHours);
      }
    }
  }
}
