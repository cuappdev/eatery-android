package com.cornellappdev.android.eatery.network;

import android.content.Context;

import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class QueryUtilities {

    // These were manually set as the dining hall IDs based on the IDs returned with graphQL
    private final static HashSet<Integer> DINING_HALL_IDS =
            new HashSet<>(Arrays.asList(1, 5, 10, 18, 20, 24, 25, 26, 27, 28));

    static ArrayList<EateryBaseModel> parseEateries(List<AllEateriesQuery.Eatery> eateries,
                                                           Context mainContext) {
        ArrayList<EateryBaseModel> eateryList = new ArrayList<>();
        for (AllEateriesQuery.Eatery eatery : eateries) {
            EateryBaseModel model;
            if (DINING_HALL_IDS.contains(eatery.id())) {
                model = DiningHallModel.fromEatery(mainContext, false, eatery);
            } else {
                model = CafeModel.fromEatery(mainContext, false, eatery);
            }
            if (model != null) {
                eateryList.add(model);
            }
        }

        return eateryList;
    }

    public static BrbInfoModel parseBrbInfo(BrbInfoQuery.AccountInfo brbInfo) {
        return BrbInfoModel.loadFromInfo(brbInfo);
    }

}