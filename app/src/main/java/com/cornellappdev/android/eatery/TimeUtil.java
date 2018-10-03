package com.cornellappdev.android.eatery;

import org.threeten.bp.ZoneId;

/**
 * Created by Evan Welsh on 10/2/18.
 */
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
