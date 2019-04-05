package com.cornellappdev.android.eatery;

import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class Repository {
    private ArrayList<EateryBaseModel> mEateryList = new ArrayList<>();
    private ArrayList<EateryBaseModel> mCollegetownEateryList = new ArrayList<>();
    private ArrayList<EateryBaseModel> mSearchList = new ArrayList<>();
    private static final Repository sRepoInstance = new Repository();
    private boolean isSearchPressed = false;
    private boolean mDisplayingCTown;

    public static Repository getInstance() {
        return sRepoInstance;
    }

    public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
        this.mEateryList = eateryList;
    }

    public ArrayList<EateryBaseModel> getEateryList() {
        return mEateryList;
    }

    public void setCtEateryList(ArrayList<EateryBaseModel> ctEateryList) {
        this.mCollegetownEateryList = ctEateryList;
    }

    public ArrayList<EateryBaseModel> getCtEateryList() {
        return mCollegetownEateryList;
    }

    public void setSearchList(ArrayList<EateryBaseModel> searchList) {
        this.mSearchList = searchList;
    }

    public void setDisplayCTown(boolean displayCTown) {
        this.mDisplayingCTown = displayCTown;
    }

    public ArrayList<EateryBaseModel> getSearchList() {
        return mSearchList;
    }

    public boolean getDisplayCTown() {
        return mDisplayingCTown;
    }

    public boolean getIsSearchPressed() {
        return isSearchPressed;
    }

    public void setIsSearchPressed(boolean isPressed) {
        isSearchPressed = isPressed;
    }

}
