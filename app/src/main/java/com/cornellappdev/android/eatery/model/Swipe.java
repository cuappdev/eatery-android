package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.time.LocalTime;

import androidx.annotation.NonNull;

/**
 * Helper class to represent swipe data for each dining hall
 */

public class Swipe implements Serializable {
    public final double swipeDensity;
    public final int waitTimeLow, waitTimeHigh;
    private final LocalTime start, end;

    // Constructor for an Empty swipe, used when there is no data for a particular hour
    public Swipe() {
        this.start = null;
        this.end = null;
        this.swipeDensity = 0;
        this.waitTimeLow = 0;
        this.waitTimeHigh = 0;
    }

    public Swipe(@NonNull LocalTime start, @NonNull LocalTime end, double swipeDensity,
                 int waitTimeLow, int waitTimeHigh) {
        this.start = start;
        this.end = end;
        this.swipeDensity = swipeDensity;
        this.waitTimeLow = waitTimeLow;
        this.waitTimeHigh = waitTimeHigh;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}
