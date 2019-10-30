package com.cornellappdev.android.eatery.model;

import com.cornellappdev.android.eatery.model.enums.MealType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.NonNull;

public class DailyMenuModel implements Iterable<MealType>, Serializable {
    private Map<MealType, MealModel> mMenu = new HashMap<>();

    public MealModel getMeal(MealType type) {
        return mMenu.get(type);
    }

    public ArrayList<MealType> getAllMealTypes() {
        ArrayList<MealType> mealTypes = new ArrayList<>(mMenu.keySet());
        Collections.sort(mealTypes);
        return mealTypes;
    }

    public ArrayList<MealModel> getAllMeals() {
        ArrayList<MealType> mealTypes = new ArrayList<>(mMenu.keySet());
        Collections.sort(mealTypes);
        ArrayList<MealModel> sortedAllMeals = new ArrayList<MealModel>();
        for (MealType mtype : mealTypes) {
            sortedAllMeals.add(mMenu.get(mtype));
        }
        return sortedAllMeals;
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
