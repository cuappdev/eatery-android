package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.AllEateriesQuery;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Parent class of DiningHallModel and CafeModel
public abstract class CampusModel extends EateryBaseModel implements Serializable {
    private List<Swipe> mSwipeDataList;

    protected static List<Swipe> parseSwipeData(List<AllEateriesQuery.SwipeDatum> swipeData) {
        List<Swipe> swipes = new ArrayList<Swipe>();
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mma")
                .toFormatter(Locale.US);
        for (AllEateriesQuery.SwipeDatum swipeDatum : swipeData) {
            LocalTime start = null, end = null;
            start = LocalTime.parse(swipeDatum.startTime().toUpperCase().substring(11),
                    timeFormatter);
            end = LocalTime.parse(swipeDatum.endTime().toUpperCase().substring(11),
                    timeFormatter);
            swipes.add(new Swipe(start, end, swipeDatum.swipeDensity(), swipeDatum.waitTimeLow(),
                    swipeDatum.waitTimeHigh()));
        }
        return swipes;
    }

    public void parseEatery(Context context, boolean hardcoded, AllEateriesQuery.Eatery eatery) {
        super.parseEatery(context, hardcoded, eatery);
        mSwipeDataList = parseSwipeData(eatery.swipeData());
    }

    public List<Swipe> getSwipeData() {
        return mSwipeDataList;
    }
}
