package com.cornellappdev.android.eatery.model;

import androidx.annotation.NonNull;

import com.cornellappdev.android.eatery.model.enums.MealType;

import java.time.LocalDateTime;

import java.io.Serializable;

/**
 * Model class to represent each meal
 */

public class MealModel extends Interval implements Serializable {
    private MealMenuModel menu;
    private MealType type;

    MealModel(@NonNull LocalDateTime start, @NonNull LocalDateTime end) {
        super(start, end);
    }

    public Interval getInterval() {
        return new Interval(this.getStart(), this.getEnd());
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