package com.cornellappdev.android.eatery.Model;

import java.util.Comparator;

/**
 * Enum class to represent 4 different types of meal
 */

public enum MealType implements Comparable<MealType>{
    BREAKFAST(1),
    BRUNCH(2),
    LUNCH(3),
    DINNER(4);

    private int index;

    MealType(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static MealType fromDescription(String type) {
        switch (type.toLowerCase()) {
            case "breakfast":
                return BREAKFAST;
            case "brunch":
                return BRUNCH;
            case "lunch":
                return LUNCH;
            case "dinner":
                return DINNER;
        }
        return null;
    }
}

/**
 * Sorts MealType in following order: Breakfast > Brunch > Lunch > Dinner
 */

class MealComparator implements Comparator<MealType> {

    @Override
    public int compare(final MealType t1, final MealType t2){
        if(t1.getIndex() > t2.getIndex()) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
