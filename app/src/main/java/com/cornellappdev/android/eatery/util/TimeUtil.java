package com.cornellappdev.android.eatery.util;

import com.cornellappdev.android.eatery.model.EateryBaseModel.Status;
import com.cornellappdev.android.eatery.model.Interval;
import com.cornellappdev.android.eatery.model.enums.MealType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    private final static DateTimeFormatter ONLY_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");
    private final static DateTimeFormatter DATE_AND_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "EEE M/d h:mm a");
    private static TimeUtil instance;
    private ZoneId cornellTimeZone;

    public static TimeUtil getInstance() {
        if (instance == null) {
            instance = new TimeUtil();
        }
        return instance;
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

    public static String format(Status status, LocalDateTime changeTime) {
        if (changeTime == null) {
            return "";
        }
        if (status == Status.CLOSED) {
            if (changeTime.toLocalDate().equals(LocalDate.now())) {
                String updatedTime = changeTime.format(ONLY_TIME_FORMAT);
                return String.format("until %s", updatedTime);
            } else if (changeTime.toLocalDate().equals(LocalDate.now().plusDays(1l))) {
                String updatedTime = changeTime.format(ONLY_TIME_FORMAT);
                return String.format("until tomorrow at %s", updatedTime);
            } else {
                String updatedTime = changeTime.format(DATE_AND_TIME_FORMAT);
                return String.format("until %s", updatedTime);

            }
        }
        else if (status == Status.CLOSINGSOON) {
            return String.format("at %s", changeTime.format(ONLY_TIME_FORMAT));

        }
        return String.format("until %s", changeTime.format(ONLY_TIME_FORMAT));
    }

    public static String format(Status status, Interval interval, LocalDateTime changeTime) {
        LocalDateTime current = LocalDateTime.now();
        if (interval.containsTime(current)) {
            return format(status, changeTime);
        }
        return String.format("Open from %s to %s", interval.getStart().format(ONLY_TIME_FORMAT),
                interval.getEnd().format(ONLY_TIME_FORMAT));
    }

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