package com.cornellappdev.android.eatery.model;

public enum MealType {

  BREAKFAST,
  LUNCH,
  DINNER,
  LITE_LUNCH,
  BRUNCH;

  public static MealType fromDescription(String descr) {
    switch (descr.toLowerCase()) {
      case "breakfast":
        return BREAKFAST;
      case "lunch":
        return LUNCH;
      case "dinner":
        return DINNER;
      case "lite lunch":
        return LITE_LUNCH;
      case "brunch":
        return BRUNCH;
    }

    return null;
  }
}