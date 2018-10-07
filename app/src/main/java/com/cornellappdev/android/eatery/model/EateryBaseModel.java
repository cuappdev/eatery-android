package com.cornellappdev.android.eatery.model;

import java.io.Serializable;

public abstract class EateryBaseModel implements Serializable {

    public enum CafeteriaArea {
        NORTH,
        CENTRAL,
        WEST;
    }

    public enum Status {
        OPEN,
        CLOSINGSOON,
        CLOSED;
    }

    private boolean matchesFilter = true;
    private boolean matchesSearch = true;
    private boolean openPastMidnight = false;
    private CafeteriaArea mArea;
    private Double mLattitude, mLongitude;
    private String mBuildingLocation;
    private String mName;
    private String mNickName;
    private int mId;


    // Implemented Getters
    public String getName(){
        return mName;
    }

    public String getNickName(){
        return mNickName;
    }

    public CafeteriaArea getArea(){
        return mArea;
    }

    public double getLattitude(){
        return mLattitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public String getBuildingLocation(){
        return mBuildingLocation;
    }

    // Abstract Getters
    abstract Status getStatus();
    abstract String getStatusMessage();
    abstract MealModel getMenu();














}
