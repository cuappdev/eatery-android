package com.cornellappdev.android.eatery.model;

import androidx.annotation.NonNull;
import com.cornellappdev.android.eatery.util.TimeUtil;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

public class DiningHallMenuModel implements Iterable<MealModel>, Serializable {
  public static final DiningHallMenuModel EMPTY;

  static {
    EMPTY = new DiningHallMenuModel() {
      @Override
      public void setMeal(MealType type, MealModel model) {
      }
    };
    EMPTY.mMenu = Collections.unmodifiableMap(EMPTY.mMenu);
  }

  private Map<MealType, MealModel> mMenu = new HashMap<>();

  public void setMeal(MealType type, MealModel model) {
    mMenu.put(type, model);
  }

  public MealModel getMeal(MealType type) {
    return mMenu.get(type);
  }

  @NonNull
  @Override
  public Iterator<MealModel> iterator() {
    return mMenu.values().iterator();
  }

  public MealModel getMeal(int mealOrder) {
    MealModel meal;
    switch (mealOrder) {
      case 0:
        meal = getMeal(MealType.BREAKFAST);
        if (meal != null) {
          break;
        }
      case 1:
        meal = getMeal(MealType.BRUNCH);
        if (meal != null) {
          break;
        }
      case 2:
        meal = getMeal(MealType.LITE_LUNCH);
        if (meal != null) {
          break;
        }
      case 3:
        meal = getMeal(MealType.LITE_LUNCH);
        if (meal != null) {
          break;
        }
      case 4:
      default:
        meal = getMeal(MealType.DINNER);
        break;
    }
    return meal;
  }

  public MealModel getCurrentMeal() {
    ZonedDateTime now = ZonedDateTime.now();
    for (MealModel meal : this) {
      ZoneId cornell = TimeUtil.getInstance().getCornellTimeZone();
      ZonedDateTime startTime = meal.getStart().atZone(cornell);
      ZonedDateTime endTime = meal.getEnd().atZone(cornell);
      if (now.isAfter(startTime) && now.isBefore(endTime)) {
        return meal;
      }
    }
    return null;
  }

  public MealModel getLastMeal() {
    MealType[] types = new MealType[]{MealType.DINNER, MealType.LITE_LUNCH, MealType.LUNCH,
        MealType.BRUNCH, MealType.BREAKFAST};
    for (MealType type : types) {
      MealModel meal = mMenu.get(type);
      if (meal != null) {
        return meal;
      }
    }
    return null;
  }

  public int numberOfMeals() {
    return mMenu.size();
  }

}
