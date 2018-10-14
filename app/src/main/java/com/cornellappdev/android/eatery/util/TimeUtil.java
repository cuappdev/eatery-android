package com.cornellappdev.android.eatery.util;

import org.threeten.bp.ZoneId;

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


}