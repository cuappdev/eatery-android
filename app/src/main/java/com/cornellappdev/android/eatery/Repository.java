package com.cornellappdev.android.eatery;

import android.content.Context;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class Repository {
  private ArrayList<EateryBaseModel> mEateryList = new ArrayList<>();
  private ArrayList<EateryBaseModel> mCollegetownEateryList = new ArrayList<>();
  private ArrayList<EateryBaseModel> mSearchList = new ArrayList<>();
  private static final Repository sRepoInstance = new Repository();
  private BrbInfoModel mCurrentModel;
  private boolean mIsSearchPressed = false;
  private boolean mIsSaveInfoChecked = false;

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

  public ArrayList<EateryBaseModel> getSearchList() {
    return mSearchList;
  }

  public boolean getIsSearchPressed() {
    return mIsSearchPressed;
  }

  public void setIsSearchPressed(boolean isPressed) {
    mIsSearchPressed = isPressed;
  }

  public void setBrbInfoModel(BrbInfoModel m) {
    this.mCurrentModel = m;
  }

  public BrbInfoModel getBrbInfoModel() {
    return this.mCurrentModel;
  }

  public void setSaveCredentials(boolean isChecked) {
    this.mIsSaveInfoChecked = isChecked;
  }

  public boolean getSaveCredentials() {
      return this.mIsSaveInfoChecked;
  }

}
