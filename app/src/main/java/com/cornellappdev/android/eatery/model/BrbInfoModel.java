package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.BrbInfoQuery;

import java.io.Serializable;
import java.util.ArrayList;

public class BrbInfoModel implements Serializable {
    private float mCityBucks;
    private float mLaundry;
    private float mBRBs;
    private int mMealSwipes;
    private ArrayList<HistoryObjectModel> mHistory;

    public BrbInfoModel(float cityBucks, float laundry, float brbs,
                        int mealSwipes, ArrayList<HistoryObjectModel> history){
        this.mCityBucks = cityBucks;
        this.mLaundry = laundry;
        this.mBRBs = brbs;
        this.mMealSwipes = mealSwipes;
        this.mHistory = history;
    }

    public static BrbInfoModel loadFromInfo(BrbInfoQuery.AccountInfo brbInfo){
        float cityBucks = Float.parseFloat(brbInfo.cityBucks());
        float laundry = Float.parseFloat(brbInfo.laundry());
        float brbs = Float.parseFloat(brbInfo.brbs());
        int mealSwipes = 0;
        try {
            mealSwipes = Integer.parseInt(brbInfo.swipes());
        }catch(NumberFormatException ex){

        }
        ArrayList<HistoryObjectModel> history = new ArrayList<HistoryObjectModel>();
        for(BrbInfoQuery.History hist : brbInfo.history()) {
            HistoryObjectModel to_add = HistoryObjectModel.parseHistoryObject(hist);
            history.add(to_add);
            //Parses all the history objects within brbInfo
        }
        return new BrbInfoModel(cityBucks, laundry, brbs, mealSwipes, history);
    }

    public float getBRBs(){
        return this.mBRBs;
    }
    public float getLaundry(){
        return this.mLaundry;
    }
    public int getSwipes(){
        return this.mMealSwipes;
    }
    public float getCityBucks(){
        return this.mCityBucks;
    }
    public ArrayList<HistoryObjectModel> getHistory(){
        return this.mHistory;
    }
}
