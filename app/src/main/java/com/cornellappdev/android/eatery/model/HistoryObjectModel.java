package com.cornellappdev.android.eatery.model;


import com.cornellappdev.android.eatery.BrbInfoQuery;

import java.io.Serializable;

public class HistoryObjectModel implements Serializable {
    private String mName;
    private String mTimestamp;
    private float mAmount;
    private boolean mPositive;

    private HistoryObjectModel(String name, String timestamp, float amount, boolean positive) {
        this.mName = name;
        this.mTimestamp = timestamp;
        this.mAmount = amount;
        this.mPositive = positive;
    }

    static HistoryObjectModel parseHistoryObject(BrbInfoQuery.History historyInfo) {
        String name = historyInfo.name();
        String timestamp = historyInfo.timestamp();
        float amount = Float.parseFloat(historyInfo.amount());
        boolean positive = historyInfo.positive();
        return new HistoryObjectModel(name, timestamp, amount, positive);
    }

    public boolean isPositive() {
        return mPositive;
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