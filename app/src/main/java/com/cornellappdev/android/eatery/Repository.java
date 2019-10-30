package com.cornellappdev.android.eatery;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class Repository {
    private static final Repository sRepoInstance = new Repository();
    private ArrayList<EateryBaseModel> mEateryList = new ArrayList<>();
    private ArrayList<EateryBaseModel> mCollegetownEateryList = new ArrayList<>();
    private ArrayList<EateryBaseModel> mSearchList = new ArrayList<>();
    private BrbInfoModel mCurrentModel;
    private boolean mIsSearchPressed = false;
    private boolean mDisplayingCTown;

    public static Repository getInstance() {
        return sRepoInstance;
    }

    public ArrayList<EateryBaseModel> getEateryList() {
        return mEateryList;
    }

    public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
        this.mEateryList = eateryList;
    }

    public ArrayList<EateryBaseModel> getCtEateryList() {
        return mCollegetownEateryList;
    }

    public void setCtEateryList(ArrayList<EateryBaseModel> ctEateryList) {
        this.mCollegetownEateryList = ctEateryList;
    }

    public ArrayList<EateryBaseModel> getSearchList() {
        return mSearchList;
    }

    public void setSearchList(ArrayList<EateryBaseModel> searchList) {
        this.mSearchList = searchList;
    }

    public boolean getDisplayCTown() {
        return mDisplayingCTown;
    }

    public void setDisplayCTown(boolean displayCTown) {
        this.mDisplayingCTown = displayCTown;
    }

    public boolean getIsSearchPressed() {
        return mIsSearchPressed;
    }

    public void setIsSearchPressed(boolean isPressed) {
        mIsSearchPressed = isPressed;
    }

    public BrbInfoModel getBrbInfoModel() {
        return this.mCurrentModel;
    }

    public void setBrbInfoModel(BrbInfoModel m) {
        this.mCurrentModel = m;
    }

}
