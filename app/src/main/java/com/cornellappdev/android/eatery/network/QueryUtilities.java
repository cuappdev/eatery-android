package com.cornellappdev.android.eatery.network;

import android.content.Context;

import com.cornellappdev.android.eatery.AllCtEateriesQuery;
import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.CollegeTownModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.List;

public final class QueryUtilities {

    public static BrbInfoModel parseBrbInfo(BrbInfoQuery.AccountInfo brbInfo) {
        return BrbInfoModel.loadFromInfo(brbInfo);
    }

    static ArrayList<EateryBaseModel> parseCtEateries(Context context,
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