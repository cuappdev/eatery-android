package com.cornellappdev.android.eatery.model;


import com.cornellappdev.android.eatery.BrbInfoQuery;

public class HistoryObjectModel{
    private String mName;
    private String mTimestamp;

    public HistoryObjectModel(String name, String timestamp){
        this.mName = name;
        this.mTimestamp = timestamp;
    }

    public static HistoryObjectModel parseHistoryObject(BrbInfoQuery.History historyInfo){
        String name = historyInfo.name();
        String timestamp = historyInfo.timestamp();
        return new HistoryObjectModel(name, timestamp);
    }

    public String getName(){
        return mName;
    }
    public String getTimestamp(){
        return mTimestamp;
    }
}