package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.AllCtEateriesQuery;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CollegeTownModel extends EateryBaseModel implements Serializable{
  private List<String> mCategories;
  private Map<LocalDate, List<Interval>> mHours;
  private List<LocalDate> mSortedDates;
  protected String mImageUrl, mPrice, mRating, mYelpUrl;

  public CollegeTownModel() {
    mCategories = new ArrayList<>();
    mHours = new HashMap<>();
    mSortedDates = new ArrayList<>();
  }

  public static CollegeTownModel fromEatery(Context context, AllCtEateriesQuery.CollegetownEatery ctEatery) {
    CollegeTownModel model = new CollegeTownModel();
    model.parseCtEatery(context, ctEatery);
    return model;
  }

  public List<String> getCategories() {
    return mCategories;
  }

  public String getImageUrl() {
    return mImageUrl;
  }

  public String getPrice() {
    return mPrice;
  }

  public String getRating() {
    return mRating;
  }

  public String getYelpUrl() {
    return mYelpUrl;
  }

  private void setHours(LocalDate date, List<Interval> hours) {
    List<Interval> sortedHours = new ArrayList<>(hours);
    Collections.sort(sortedHours, Interval::compareTo);
    this.mHours.put(date, sortedHours);
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

  @Override
  public HashSet<String> getMealItems() {
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

  public void parseCtEatery(Context context, AllCtEateriesQuery.CollegetownEatery ctEatery) {
    super.parseCtEatery(context, ctEatery);
    mCategories = ctEatery.categories();
    mImageUrl = ctEatery.eateryType();
    mPrice = ctEatery.price();
    mRating = ctEatery.rating();
    mYelpUrl = ctEatery.url();

    DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("h:mma")
        .toFormatter();
    DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("yyyy-MM-dd")
        .toFormatter();

    for (AllCtEateriesQuery.OperatingHour operatingHour : ctEatery.operatingHours()) {
      List<LocalDate> localDates = new ArrayList<>();
      LocalDate localDate = LocalDate.parse(operatingHour.date(), dateFormatter);
      localDates.add(localDate);
      for (AllCtEateriesQuery.Event event : operatingHour.events()) {
        List<Interval> dailyHours = new ArrayList<>();
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
  }
}
