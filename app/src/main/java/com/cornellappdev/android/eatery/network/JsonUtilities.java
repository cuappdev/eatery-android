package com.cornellappdev.android.eatery.network;

import android.content.Context;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryModel;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtilities {

  public final static int MILLISECONDS_PER_DAY = 86400000;


  private static final Set<Integer> DINING_HALL_IDS = new HashSet<>(
      Arrays.asList(31, 25, 26, 27, 29, 3, 20, 4, 5, 30)
  );

  /**
   * Reads JSON text with meal details from file
   */
  private static String loadJSONFromAsset(Context context, String fileName) {
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
      return null;
    }

    return json;
  }

  /**
   * Returns list of all eateries (dining halls, cafes, and external eateries not in main JSON)
   */
  public static List<EateryModel> parseJson(String json, Context mainContext) {
    DateFormat mealTimeFormat = DateFormat.getDateTimeInstance();
    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mainContext);
    DateFormat mealHourFormat = android.text.format.DateFormat.getTimeFormat(mainContext);

    List<EateryModel> eateryModels = new ArrayList<>();

    // Parse external eateries from hardcoded JSON
    JSONObject hardCodedData = null;
    try {
      hardCodedData = new JSONObject(loadJSONFromAsset(mainContext, "externalEateries.json"));

      JSONArray hardCodedEateries = hardCodedData.getJSONArray("eateries");

      for (int i = 0; i < hardCodedEateries.length(); i++) {
        JSONObject obj = hardCodedEateries.getJSONObject(i);
        EateryModel model;

        if (obj.has("id") && DINING_HALL_IDS.contains(obj.getInt("id"))) {
          model = DiningHallModel.fromJSON(mainContext, true, obj);
        } else {
          model = CafeModel.fromJSONObject(mainContext, true, obj);
        }

        System.out.println(model);

        if (model != null) {
          eateryModels.add(model);
        }
      }

      // Parse eateries from main source
      JSONObject parentObject = new JSONObject(json);
      JSONObject data = parentObject.getJSONObject("data");
      JSONArray eateries = data.getJSONArray("eateries");

      for (int i = 0; i < eateries.length(); i++) {
        JSONObject obj = eateries.getJSONObject(i);
        EateryModel model;

        if (DINING_HALL_IDS.contains(obj.getInt("id"))) {
          model = DiningHallModel.fromJSON(mainContext, false, obj);
        } else {
          model = CafeModel.fromJSONObject(mainContext, false, obj);
        }
        if (model != null) {
          eateryModels.add(model);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();

    }
    return eateryModels;
  }
}
