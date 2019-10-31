package com.cornellappdev.android.eatery.network;

import android.content.Context;

import com.cornellappdev.android.eatery.AllCtEateriesQuery;
import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.CollegeTownModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class QueryUtilities {

    public final static HashSet<Integer> DINING_HALL_IDS =
            new HashSet<>(Arrays.asList(31, 25, 26, 27, 29, 3, 20, 4, 5, 30));

    public static ArrayList<EateryBaseModel> parseEateries(List<AllEateriesQuery.Eatery> eateries,
                                                           Context mainContext) {
        ArrayList<EateryBaseModel> eateryList = new ArrayList<EateryBaseModel>();
        for (AllEateriesQuery.Eatery eatery : eateries) {
            EateryBaseModel model = null;
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
        BrbInfoModel model = BrbInfoModel.loadFromInfo(brbInfo);
        return model;
    }

    public static ArrayList<EateryBaseModel> parseCtEateries(Context context,
                                                             List<AllCtEateriesQuery.CollegetownEatery> collegetowntEateries) {
        ArrayList<EateryBaseModel> collegetownEateryList = new ArrayList<>();
        for (AllCtEateriesQuery.CollegetownEatery eatery : collegetowntEateries) {
            EateryBaseModel model = CollegeTownModel.fromEatery(context, eatery);
            if (model != null) {
                collegetownEateryList.add(model);
            }
        }
        return collegetownEateryList;
    }
}