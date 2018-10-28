package com.cornellappdev.android.eatery.util;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.EateryBaseModel.Status;

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

  public static String format(Status status, LocalDateTime time){
    DateTimeFormatter ONLY_TIME_FORMAT =  DateTimeFormatter.ofPattern("h:mm a");
    DateTimeFormatter DATE_AND_TIME_FORMAT =  DateTimeFormatter.ofPattern("M/d 'at' h:mm a");
    String closeStr = "Closes at";
    String openAt = "Opens at";
    String openOn = "Opens on";
    if(time == null){
      return "";
    }
    if(status == Status.CLOSED){
      if(time.toLocalDate().equals(LocalDate.now())){
        String changeTime = time.format(ONLY_TIME_FORMAT);
        return  openAt + " " + changeTime;
      }
      else{
        String changeTime = time.format(DATE_AND_TIME_FORMAT);
        return  openOn + " " + changeTime;

      }
    }
    return closeStr + " " + time.format(ONLY_TIME_FORMAT);
  }


}