package com.cornellappdev.android.eatery.model;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.Pair;
import com.cornellappdev.android.eatery.TimeUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * Created by abdullahislam on 3/2/18.
 */

public class CafeModel extends EateryModel implements Serializable {

  private List<String> mCafeMenu;
  private Map<LocalDate, List<Interval>> mHours, mHoursHardcoded;

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


  public CafeModel() {
    this.mHours = new HashMap<>();
    this.mHoursHardcoded = new HashMap<>();
    this.mCafeMenu = new ArrayList<>();
  }

  public List<String> getCafeMenu() {
    return mCafeMenu;
  }

  public void setCafeMenu(List<String> cafeMenu) {
    this.mCafeMenu = cafeMenu;
  }

  public List<Interval> getHours(LocalDate date) {
    return mHours.get(date);
  }

  // Note(lesley): This method assumes that a cafe has only one opening and closing time window per day
  public void setHours(LocalDate date, List<Interval> hours) {
    this.mHours.put(date, hours);
  }

  public List<Interval> getHardcodedHours(LocalDate date) {
    return mHoursHardcoded.get(date);
  }

  public void setHoursHardcoded(LocalDate date, List<Interval> hoursH) {
    this.mHoursHardcoded.put(date, hoursH);
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
    if (getName().equalsIgnoreCase("trillium")) {
      cafeItems.addAll(HARDCODED_CAFE_ITEMS);
    }

    mCafeMenu = cafeItems;

    // cafeHours contains <Date object: unique date> : <daily hours with opening and closing time>
    //   LongSparseArray<List<Interval>> cafeHours = new LongSparseArray<>();
    // Note(lesley): A single operatingHours object contains operating times for a single day
    JSONArray operatingHours = cafe
        .getJSONArray("operatingHours"); //a single operating hour is a single day

    for (int c = 0; c < operatingHours.length(); c++) {
      // TODO FIX

      // First entry represents opening time, second entry represents closing time
      List<Interval> dailyHours = new ArrayList<>();
      JSONObject operating = operatingHours.getJSONObject(c);
      JSONArray events = operating.getJSONArray("events");

      LocalDate localDate;

      if (operating.has("date")) {
        String rawDate = operating.getString("date");
        localDate = LocalDate.parse(rawDate, DateTimeFormatter
            .ofPattern("yyyy-MM-dd", context.getResources().getConfiguration().locale));
      } else {
        // TODO
        localDate = LocalDate.now();
      }

      for (int e = 0; e < events.length(); e++) {
        JSONObject obj = events.getJSONObject(e);

        LocalDateTime start = null, end = null;

        if (obj.has("start")) {
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

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime midnightTomorrow = localDate
            .atTime(LocalTime.MIDNIGHT);

        if (end != null && end.isBefore(start) && (end.isEqual(midnightTomorrow) || end.isAfter(midnightTomorrow))) {
          setOpenPastMidnight(true);

          end = end.plusDays(1);
        }

        if (start != null && end != null) {
          dailyHours.add(new Interval(start, end));
        }
      }
      setHours(localDate, dailyHours);
    }
  }

  @Override
  public LocalDateTime getNextOpening(LocalDateTime time) {
    return null;
  }

  public List<String> getMealItems() {
    return getCafeMenu();
  }

  @Override
  public Status getCurrentStatus() {
    ZonedDateTime now = ZonedDateTime.now();

    List<Interval> hours = getHours(now.toLocalDate());

    if (hours != null) {
      for (Interval openPeriod : hours) {
        ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();

        ZonedDateTime startTime = openPeriod.getStart().atZone(cornell);
        ZonedDateTime endTime = openPeriod.getEnd().atZone(cornell);

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

  public static CafeModel fromJSONObject(Context context, boolean hardcoded, JSONObject cafe)
      throws JSONException {
    CafeModel model = new CafeModel();
    model.parseJSONObject(context, hardcoded, cafe);
    return model;
  }
}
