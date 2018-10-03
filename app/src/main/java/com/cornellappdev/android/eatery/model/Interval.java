package com.cornellappdev.android.eatery.model;

import android.support.annotation.NonNull;
import java.io.Serializable;
import org.threeten.bp.LocalDateTime;


/**
 * Created by Evan Welsh on 10/2/18.
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

    return 0; // TODO double check this
  }
}
