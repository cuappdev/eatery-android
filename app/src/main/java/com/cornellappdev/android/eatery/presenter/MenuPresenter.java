package com.cornellappdev.android.eatery.presenter;

import com.cornellappdev.android.eatery.model.CampusModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.Swipe;

import java.util.ArrayList;
import java.util.List;

public class MenuPresenter {

    private EateryBaseModel mModel;

    public MenuPresenter(EateryBaseModel model) {
        this.mModel = model;
    }

    public List<Swipe> getWaitTimes() {
        // Return null if eatery is not a campus eatery / no swipe data available.
        if (!(mModel instanceof CampusModel) || ((CampusModel) mModel).getSwipeData().size() == 0) {
            return null;
        }

        // Data for wait times chart.
        List<Swipe> swipeData = new ArrayList<Swipe>();

        // Wait times chart must have 21 elements for each hour within 6am - 3am.
        // i represents the hour, with 0 being 6am - 7am.
        for (int i = 0; i < 21; i++) {
            // Default swipeDensity, waitTimeLow, waitTimeHigh = 0 for hours without backend swipe data.
            double maxSwipeDensity = -1;
            Swipe maxSwipe = null;
            // Parse through backend swipe data. If multiple swipe data found for the hour, use maximum.
            for (Swipe s : ((CampusModel) mModel).getSwipeData()) {
                int hourFromSwipe = s.getStart().getHour();
                hourFromSwipe = hourFromSwipe < 6 ? (18 + hourFromSwipe) : hourFromSwipe - 6;
                if (hourFromSwipe == i && s.swipeDensity > maxSwipeDensity) {
                    maxSwipeDensity = s.swipeDensity;
                    maxSwipe = s;
                }
            }
            // Each index of swipeData now represents a swipe with index 0 being swipe with start = 6am, end = 7am.
            if (maxSwipe == null) {
                // If no swipes were found, add an empty swipe to the List
                swipeData.add(new Swipe());
            } else {
                swipeData.add(maxSwipe);
            }
        }
        return swipeData;
    }
}
