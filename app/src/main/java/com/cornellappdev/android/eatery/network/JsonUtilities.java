package com.cornellappdev.android.eatery.network;

import android.content.Context;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}