package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import androidx.annotation.NonNull;

/**
 * Helper class to represent serving time interval for each meal
 */

public class Interval implements Serializable, Comparable<Interval> {
    @NonNull
    private final LocalDateTime start, end;

    public Interval(@NonNull LocalDateTime start, @NonNull LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    @NonNull
    public LocalDateTime getStart() {
        return start;
    }

    @NonNull
    public LocalDateTime getEnd() {
        return end;
    }

    @NonNull
    public boolean containsTime(LocalDateTime time) {
        return start.isBefore(time) && end.isAfter(time);
    }

    @NonNull
    public boolean afterTime(LocalDateTime time) {
        return start.isAfter(time);
    }

    @Override
    public int compareTo(@NonNull Interval interval) {
        if (interval.getEnd().isAfter(this.end)) {
            return -1;
        }
        if (interval.getEnd().isBefore(this.end)) {
            return 1;
        }
        if (interval.getStart().isAfter(this.start)) {
            return -1;
        }
        if (interval.getStart().isBefore(this.start)) {
            return 1;
        }
        return 0;
    }
}