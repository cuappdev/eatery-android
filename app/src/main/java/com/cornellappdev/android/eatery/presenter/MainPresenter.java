package com.cornellappdev.android.eatery.presenter;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class MainPresenter {

    private Repository rInstance = Repository.getInstance();
    private String query = "";

    public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
        rInstance.setEateryList(eateryList);
        rInstance.setSearchList(rInstance.getEateryList());
    }
}


