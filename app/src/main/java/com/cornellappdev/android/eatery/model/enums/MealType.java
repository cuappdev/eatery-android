package com.cornellappdev.android.eatery.model.enums;

/**
 * Enum class to represent 4 different types of meal
 */

public enum MealType implements Comparable<MealType> {
    BREAKFAST(1),
    BRUNCH(2),
    LUNCH(3),
    DINNER(4);

    private int index;

    MealType(int index) {
        this.index = index;
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

    public int getIndex() {
        return index;
    }
}