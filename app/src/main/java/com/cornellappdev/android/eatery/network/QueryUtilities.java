package com.cornellappdev.android.eatery.network;

import android.content.Context;

import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.List;

public final class QueryUtilities {

    static ArrayList<EateryBaseModel> parseEateries(List<AllEateriesQuery.Eatery> eateries,
                                                    Context mainContext) {
        ArrayList<EateryBaseModel> eateryList = new ArrayList<>();
        for (AllEateriesQuery.Eatery eatery : eateries) {
            EateryBaseModel model;
            if (eatery.eateryType().equals("Dining Room")) {
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