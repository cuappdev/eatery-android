package com.cornellappdev.android.eatery;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class Repository {
    private static final Repository sRepoInstance = new Repository();
    private ArrayList<EateryBaseModel> mEateryList = new ArrayList<>();
    private ArrayList<EateryBaseModel> mCollegetownEateryList = new ArrayList<>();
    private BrbInfoModel mCurrentModel;
    private boolean mIsSearchPressed = false;

    public static Repository getInstance() {
        return sRepoInstance;
    }

    public ArrayList<EateryBaseModel> getEateryList() {
        return mEateryList;
    }

    public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
        this.mEateryList = eateryList;
    }

    public void setCtEateryList(ArrayList<EateryBaseModel> ctEateryList) {
        this.mCollegetownEateryList = ctEateryList;
    }

    public ArrayList<EateryBaseModel> getCtEateryList() {
        return mCollegetownEateryList;
    }

    boolean getIsSearchPressed() {
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
