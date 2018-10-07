package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.util.Date;

public class DiningHallModel extends EateryBaseModel implements Serializable{

    private HashMap<Date, HashMap<MealType,MealModel>> fullMenu;


    // Methods required to be implemented by parent class
    public Status getStatus(){
        return Status.CLOSED;
    }

    public







}
