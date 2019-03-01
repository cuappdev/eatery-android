package com.cornellappdev.android.eatery.presenter;

import android.util.Log;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainPresenter {

  private Repository rInstance = Repository.getInstance();
  private String query = "";

  public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
    rInstance.setEateryList(eateryList);
    rInstance.setSearchList(rInstance.getEateryList());
  }
}


