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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class JsonUtilities {

    public final static HashSet<Integer> DINING_HALL_IDS =
            new HashSet<>(Arrays.asList(31, 25, 26, 27, 29, 3, 20, 4, 5, 30));

    /**
     * Reads JSON text with meal details from file
     */
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static ArrayList<EateryBaseModel> parseJson(String json, Context mainContext) {
        ArrayList<EateryBaseModel> eateryList = new ArrayList<EateryBaseModel>();
        JSONObject hardCodedData = null;
        try {
            hardCodedData = new JSONObject(loadJSONFromAsset(mainContext, "externalEateries.json"));
            JSONArray hardCodedEateries = hardCodedData.getJSONArray("eateries");
            for (int i = 0; i < hardCodedEateries.length(); i++) {
                JSONObject obj = hardCodedEateries.getJSONObject(i);
                EateryBaseModel model;
                if (obj.has("id") && DINING_HALL_IDS.contains(obj.getInt("id"))) {
                    model = DiningHallModel.fromJSON(mainContext, true, obj);
                } else {
                    model = CafeModel.fromJSONObject(mainContext, true, obj);
                }
                if (model != null) {
                    eateryList.add(model);
                }
            }
            JSONObject parentObject = new JSONObject(json);
            JSONObject data = parentObject.getJSONObject("data");
            JSONArray eateries = data.getJSONArray("eateries");
            for (int i = 0; i < eateries.length(); i++) {
                JSONObject obj = eateries.getJSONObject(i);
                EateryBaseModel model = null;
                if (DINING_HALL_IDS.contains(obj.getInt("id"))) {
                    model = DiningHallModel.fromJSON(mainContext, false, obj);
                } else {
                    model = CafeModel.fromJSONObject(mainContext, false, obj);
                }
                if (model != null) {
                    eateryList.add(model);
                }
            }
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return eateryList;
    }

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