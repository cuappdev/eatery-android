package com.cornellappdev.android.eatery.model;

import org.threeten.bp.LocalDateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by abdullahislam on 2/18/18.
 */

public class MealModel implements Serializable {

  private MealMenuModel menu;
  private MealType type;
  private LocalDateTime start, end;

  // toString() but better
  public String stringTo() {
    StringBuilder info = new StringBuilder(
        type + " on " + " from: " + start + " to: " + end + "\n");
    for (String category : menu.getCategories()) {
      List<String> value = menu.getItems(category);

      info.append(" ").append(category).append(": ").append(value.toString()).append("\n");
    }

    return info.toString();
  }

  public LocalDateTime getStart() {
    return start;
  }

  public void setStart(LocalDateTime start) {
    this.start = start;
  }

  public LocalDateTime getEnd() {
    return end;
  }

  public void setEnd(LocalDateTime end) {
    this.end = end;
  }

  public MealMenuModel getMenu() {
    return menu;
  }

  public void setMenu(MealMenuModel menu) {
    this.menu = menu;
  }

  public MealType getType() {
    return type;
  }

  public void setType(MealType type) {
    this.type = type;
  }

}
