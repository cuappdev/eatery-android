package com.cornellappdev.android.eatery.model;

import com.cornellappdev.android.eatery.AllEateriesQuery;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

// Parent class of DiningHallModel and CafeModel
public abstract class CampusModel extends EateryBaseModel implements Serializable {
    private List<Swipe> mSwipeDataList;

    protected void parseSwipeData(List<AllEateriesQuery.SwipeDatum> swipeData) {
        List<Swipe> swipes = new ArrayList<Swipe>();

        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy-MM-dd")
                .toFormatter(Locale.US);
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
        Collections.sort(swipes);
        mSwipeDataList = swipes;
    }

    public List<Swipe> getSwipeData() {
        return mSwipeDataList;
    }
}
