package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.util.TimeUtil;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAdjusters;
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
}
