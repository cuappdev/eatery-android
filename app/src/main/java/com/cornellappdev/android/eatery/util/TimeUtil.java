package com.cornellappdev.android.eatery.util;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.EateryBaseModel.Status;
import com.cornellappdev.android.eatery.model.Interval;
import com.cornellappdev.android.eatery.model.enums.MealType;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

public class TimeUtil {
  private static TimeUtil instance;

  public static TimeUtil getInstance() {
    if (instance == null) {
      instance = new TimeUtil();
    }
    return instance;
  }

  private ZoneId cornellTimeZone;

  public ZoneId getLocalTimeZone() {
    return ZoneId.systemDefault();
  }

  public ZoneId getCornellTimeZone() {
    if (cornellTimeZone == null) {
      cornellTimeZone = ZoneId.of("America/New_York");
    }
    if (cornellTimeZone == null) {
      cornellTimeZone = ZoneId.of("EDT");
    }
    return cornellTimeZone;
  }

  public static MealType getMealType(LocalDateTime time) {
    int hour = time.getHour();
    if (hour < 10) {
      return MealType.BREAKFAST;
    } else if (hour < 16) {
      return MealType.LUNCH;
    } else if (hour < 22) {
      return MealType.DINNER;
    } else {
      return MealType.BREAKFAST;
    }
  }

  public static String format(Status status, LocalDateTime changeTime){
    DateTimeFormatter ONLY_TIME_FORMAT =  DateTimeFormatter.ofPattern("h:mm a");
    DateTimeFormatter DATE_AND_TIME_FORMAT =  DateTimeFormatter.ofPattern("EEE M/d h:mm a");
    if(changeTime == null){
      return "";
    }
    if(status == Status.CLOSED){
      if(changeTime.toLocalDate().equals(LocalDate.now())){
        String cTime = changeTime.format(ONLY_TIME_FORMAT);
        return  String.format("Opens %s",cTime);
      }
      else if(changeTime.toLocalDate().equals(LocalDate.now().plusDays(1l))){
        String cTime = changeTime.format(ONLY_TIME_FORMAT);
        return  String.format("Opens Tomorrow %s",cTime);
      }
      else{
        String cTime = changeTime.format(DATE_AND_TIME_FORMAT);
        return  String.format("Opens on %s",cTime);

      }
    }
     return  String.format("Closes %s", changeTime.format(ONLY_TIME_FORMAT));
  }

  public static String format(Status status, Interval interval, LocalDateTime changeTime){
    DateTimeFormatter ONLY_TIME_FORMAT =  DateTimeFormatter.ofPattern("h:mm a");
    String openAt = "Opens";
    LocalDateTime current = LocalDateTime.now();
    if(interval.containsTime(current)){
      return format(status,changeTime);
    }
    return String.format("Open from %s to %s",interval.getStart().format(ONLY_TIME_FORMAT),
        interval.getEnd().format(ONLY_TIME_FORMAT));
  }


}