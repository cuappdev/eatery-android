package com.cornellappdev.android.eatery.network;

import android.content.Context;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.MealModel;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtilities {
  public final static int MILLISECONDS_PER_DAY = 86400000;

  public final static HashSet<Integer> DINGING_HALL_IDS =
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
      JSONObject parentObject = new JSONObject(json);
      JSONObject data = parentObject.getJSONObject("data");
      JSONArray eateries = data.getJSONArray("eateries");
      for (int i = 0; i < eateries.length(); i++) {
        JSONObject obj = eateries.getJSONObject(i);
        EateryBaseModel model = null;
        if (DINGING_HALL_IDS.contains(obj.getInt("id"))) {
          model = DiningHallModel.fromJSON(mainContext, false, obj);
        }
//        else {
//          model = CafeModel.fromJSONObject(mainContext, false, obj);
//        }
        if (model != null) {
          eateryList.add(model);
        }
      }
    } catch (
        JSONException e)
    {
      e.printStackTrace();
    }
    return eateryList;
  }
}