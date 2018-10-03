package com.cornellappdev.android.eatery.model;

import android.support.annotation.NonNull;
import org.threeten.bp.LocalDateTime;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by abdullahislam on 2/18/18.
 */

public class MealModel extends Interval implements Serializable {

  private MealMenuModel menu;
  private MealType type;

  MealModel(@NonNull LocalDateTime start,
      @NonNull LocalDateTime end) {
    super(start, end);
  }

  // toString() but better
  public String stringTo() {
    StringBuilder info = new StringBuilder(
        type + " on " + " from: " + getStart() + " to: " + getEnd() + "\n");
    for (String category : menu.getCategories()) {
      List<String> value = menu.getItems(category);

      info.append(" ").append(category).append(": ").append(value.toString()).append("\n");
    }

    return info.toString();
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
