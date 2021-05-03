package com.cornellappdev.android.eatery.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.cornellappdev.android.eatery.AllEateriesQuery;

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

public class CafeModel extends CampusModel implements Serializable  {
    private List<String> mCafeMenu;
    private List<String> mExpandedMenuItems;
    private List<String> mExpandedMenuPrices;
    private List<String> mExpandedMenuStations;
    private List<Integer> mStationSizes;
    private Map<LocalDate, List<Interval>> mHours;
    private List<LocalDate> mSortedDates;

    private CafeModel() {
        this.mCafeMenu = new ArrayList<>();
        this.mExpandedMenuItems = new ArrayList<>();
        this.mExpandedMenuPrices = new ArrayList<>();
        this.mExpandedMenuStations = new ArrayList<>();
        this.mStationSizes = new ArrayList<>();
        this.mHours = new HashMap<>();
        this.mSortedDates = new ArrayList<>();
    }

    public static CafeModel fromEatery(Context context, boolean hardcoded,
                                       AllEateriesQuery.Eatery cafe)  {
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
                if (intervalList != null) {
                    for (Interval interval : intervalList) {
                        if (interval.afterTime(LocalDateTime.now())) {
                            return interval.getStart();
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public LocalDateTime getChangeTime() {
        if (getCurrentStatus() == Status.OPEN) {
            List<Interval> intervalList = getCurrentIntervalList();
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

    public List<String> getExpandedMenuItems() {
        return mExpandedMenuItems;
    }

    public List<String> getExpandedMenuPrices() { return mExpandedMenuPrices;}

    public List<String> getExpandedMenuStations() {
        return mExpandedMenuStations;
    }

    public List<Integer> getStationSizes() {return mStationSizes;}

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

    private void setHours(LocalDate date, List<Interval> hours) {
        List<Interval> sortedHours = new ArrayList<>(hours);
        Collections.sort(sortedHours, Interval::compareTo);
        this.mHours.put(date, sortedHours);
    }

    public void parseEatery(Context context, boolean hardcoded, AllEateriesQuery.Eatery cafe) {
        super.parseEatery(context, hardcoded, cafe);
        List<String> cafeItems = new ArrayList<>();
        List<String> expandedItems = new ArrayList<>();
        List<String> expandedPrices = new ArrayList<>();
        List<String> expandedStations = new ArrayList<>();
        List<Integer> stationSizes = new ArrayList<>();

        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mma")
                .toFormatter(Locale.US);
        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy-MM-dd")
                .toFormatter(Locale.US);

        for (AllEateriesQuery.ExpandedMenu expanded : cafe.expandedMenu()) {
            for (AllEateriesQuery.Station station : expanded.stations()) {
                int stationSize = 0;
                for (AllEateriesQuery.Item items : station.items()) {
                    if (!expandedItems.contains(items.item())){
                        expandedItems.add(items.item());
                        expandedPrices.add(items.price());
                        stationSize ++;
                    }
                }
                // add station name and size
                expandedStations.add(expanded.category());
                stationSizes.add(stationSize);
            }
        }

        for (AllEateriesQuery.OperatingHour operatingHour : cafe.operatingHours()) {
            LocalDate localDate = LocalDate.parse(operatingHour.date(), dateFormatter);
            for (AllEateriesQuery.Event event : operatingHour.events()) {
                for (AllEateriesQuery.Menu menu : event.menu()) {
                    for (AllEateriesQuery.Item1 item : menu.items()) {
                        if (!cafeItems.contains(item.item())) cafeItems.add(item.item());
                    }
                }
                List<Interval> dailyHours = new ArrayList<>();
                LocalDateTime start, end;
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
        mExpandedMenuItems = expandedItems;
        mExpandedMenuPrices = expandedPrices;
        mExpandedMenuStations = expandedStations;
        mStationSizes = stationSizes;
        mCafeMenu = cafeItems;
    }

}
