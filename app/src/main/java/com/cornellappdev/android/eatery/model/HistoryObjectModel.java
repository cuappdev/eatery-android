package com.cornellappdev.android.eatery.model;


import com.cornellappdev.android.eatery.BrbInfoQuery;

public class HistoryObjectModel {
    private String mName;
    private String mTimestamp;
    private float mAmount;

    public HistoryObjectModel(String name, String timestamp, float amount) {
        this.mName = name;
        this.mTimestamp = timestamp;
        this.mAmount = amount;
    }

    public static HistoryObjectModel parseHistoryObject(BrbInfoQuery.History historyInfo) {
        String name = historyInfo.name();
        String timestamp = historyInfo.timestamp();
        float amount = Float.parseFloat(historyInfo.amount());
        return new HistoryObjectModel(name, timestamp, amount);
    }

    public String getName() {
        return mName;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public float getAmount() {
        return mAmount;
    }
}