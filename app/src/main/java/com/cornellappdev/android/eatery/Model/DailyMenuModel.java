package com.cornellappdev.android.eatery.model;


import android.support.annotation.NonNull;

import com.cornellappdev.android.eatery.model.enums.MealType;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DailyMenuModel implements Iterable<MealType>, Serializable {
  private Map<MealType, MealModel> mMenu = new HashMap<>();

  public MealModel getMeal(MealType type) {
    return mMenu.get(type);
  }

  public Collection<MealModel> getAllMeals(){
    return mMenu.values();
  }

  public void addMeal(MealType type, MealModel model) {
    mMenu.put(type, model);
  }

  @NonNull
  @Override
  public Iterator<MealType> iterator() {
    return mMenu.keySet().iterator();
  }

}
