package com.cornellappdev.android.eatery.model;

import android.support.annotation.NonNull;

import java.time.LocalTime;

import java.io.Serializable;

/**
 * Helper class to represent swipe data for each dining hall
 */

public class Swipe implements Serializable, Comparable<Swipe> {
    @NonNull
    private final LocalTime start, end;
    public final double swipeDensity;
    public final int waitTimeLow, waitTimeHigh;

    public Swipe(@NonNull LocalTime start, @NonNull LocalTime end, double swipeDensity,
            int waitTimeLow, int waitTimeHigh) {
        this.start = start;
        this.end = end;
        this.swipeDensity = swipeDensity;
        this.waitTimeLow = waitTimeLow;
        this.waitTimeHigh = waitTimeHigh;
    }

    @NonNull
    public LocalTime getStart() {
        return start;
    }

    @NonNull
    public LocalTime getEnd() {
        return end;
    }

    // Sort by time
    @Override
    public int compareTo(@NonNull Swipe swipe) {
        if (swipe.getEnd().isAfter(this.end)) {
            return -1;
        }
        if (swipe.getEnd().isBefore(this.end)) {
            return 1;
        }
        if (swipe.getStart().isAfter(this.start)) {
            return -1;
        }
        if (swipe.getStart().isBefore(this.start)) {
            return 1;
        }
        return 0;
    }
}
