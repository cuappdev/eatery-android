package com.cornellappdev.android.eatery.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Evan Welsh on 10/2/18.
 */
public interface Model {

  void parseJSONObject(Context context, boolean hardcoded, JSONObject obj) throws JSONException;
}
