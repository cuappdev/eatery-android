package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAdjusters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CafeModel extends CampusModel implements Serializable {
    private static Set<String> HARDCODED_CAFE_ITEMS = new HashSet<>(
            Arrays.asList("Starbucks Coffees",
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
    private List<LocalDate> mSortedDates;

    public CafeModel() {
        this.mCafeMenu = new ArrayList<>();
        this.mHours = new HashMap<>();
        this.mSortedDates = new ArrayList<>();
    }

    public static CafeModel fromJSONObject(Context context, boolean hardcoded, JSONObject cafe)
            throws JSONException {
        CafeModel model = new CafeModel();
        model.parseJSONObject(context, hardcoded, cafe);
        return model;
    }

    public static CafeModel fromEatery(Context context, boolean hardcoded,
            AllEateriesQuery.Eatery cafe) {
        CafeModel model = new CafeModel();
        model.parseEatery(context, hardcoded, cafe);
        return model;
    }

    private List<Interval> getCurrentIntervalList() {
        LocalDate today = LocalDate.now();
        if (mOpenPastMidnight && LocalDateTime.now().getHour() <= 3) {
            today = today.minusDays(1);
        }
        return mHours.get(today);
    }

    private void sortDates() {
        mSortedDates.clear();
        mSortedDates.addAll(mHours.keySet());
        Collections.sort(mSortedDates);
    }

    private LocalDateTime findNextOpen() {
        sortDates();
        for (LocalDate date : mSortedDates) {
            if (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) {
                List<Interval> intervalList = mHours.get(date);
                for (Interval interval : intervalList) {
                    if (interval.afterTime(LocalDateTime.now())) {
                        return interval.getStart();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public LocalDateTime getChangeTime() {
        if (getCurrentStatus() == Status.OPEN) {
            List<Interval> intervalList = mHours.get(LocalDate.now());
            if (intervalList != null) {
                for (Interval interval : intervalList) {
                    if (interval.containsTime(LocalDateTime.now())) {
                        return interval.getEnd();
                    }
                }
            }
        } else {
            return findNextOpen();
        }
        return null;
    }

    @Override
    public HashSet<String> getMealItems() {
            return new HashSet<>(mCafeMenu);

    }

    public List<String> getCafeMenu() {
        return mCafeMenu;
    }

    @Override
    public Status getCurrentStatus() {
        List<Interval> intervalList = getCurrentIntervalList();
        if (intervalList != null) {
            for (Interval interval : intervalList) {
                if (interval.containsTime(LocalDateTime.now())) {
                    return Status.OPEN;
                }
            }
        }
        return Status.CLOSED;
    }

    public void setHours(LocalDate date, List<Interval> hours) {
        List<Interval> sortedHours = new ArrayList<>(hours);
        Collections.sort(sortedHours, Interval::compareTo);
        this.mHours.put(date, sortedHours);
    }

    public void parseEatery(Context context, boolean hardcoded, AllEateriesQuery.Eatery cafe) {
        super.parseEatery(context, hardcoded, cafe);
        List<String> cafeItems = new ArrayList<>();

        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mma")
                .toFormatter(Locale.US);
        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy-MM-dd")
                .toFormatter(Locale.US);

        parseSwipeData(cafe.swipeData());

        for (AllEateriesQuery.OperatingHour operatingHour : cafe.operatingHours()) {
            List<LocalDate> localDates = new ArrayList<>();
            LocalDate localDate = LocalDate.parse(operatingHour.date(), dateFormatter);
            localDates.add(localDate);
            for (AllEateriesQuery.Event event : operatingHour.events()) {
                List<Interval> dailyHours = new ArrayList<>();
                for (AllEateriesQuery.Menu menu : event.menu()) {
                    for (AllEateriesQuery.Item item : menu.items()) {
                        if (!cafeItems.contains(item.item())) cafeItems.add(item.item());
                    }
                }
                LocalDateTime start = null, end = null;
                start = LocalTime.parse(event.startTime().toUpperCase().substring(11),
                        timeFormatter).atDate(localDate);
                end = LocalTime.parse(event.endTime().toUpperCase().substring(11),
                        timeFormatter).atDate(localDate);
                LocalDateTime midnightTomorrow = localDate.atTime(LocalTime.MIDNIGHT);
                if (start != null && end != null) {
                    if (end.isBefore(start) && (end.isEqual(midnightTomorrow) || end
                            .isAfter(midnightTomorrow))) {
                        mOpenPastMidnight = true;
                        end = end.plusDays(1);
                    }
                    dailyHours.add(new Interval(start, end));
                }
                setHours(localDate, dailyHours);
            }
        }
        mCafeMenu = cafeItems;
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
                LocalDate localDate = LocalDate.parse(rawDate, new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("yyyy-MM-dd")
                        .toFormatter());
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
                    for (DayOfWeek current = start; !current.equals(endPlusOne);
                            current = current.plus(1)) {
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
                    if (obj.has("start")) {
                        String rawStart = obj.getString("start").toUpperCase();
                        LocalTime localTime = LocalTime.parse(rawStart,
                                new DateTimeFormatterBuilder()
                                        .parseCaseInsensitive()
                                        .appendPattern("h:mma")
                                        .toFormatter());
                        start = localTime.atDate(localDate);
                    }
                    if (obj.has("end")) {
                        String rawEnd = obj.getString("end").toUpperCase();
                        LocalTime localTime = LocalTime.parse(rawEnd, new DateTimeFormatterBuilder()
                                .parseCaseInsensitive()
                                .appendPattern("h:mma")
                                .toFormatter());
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
