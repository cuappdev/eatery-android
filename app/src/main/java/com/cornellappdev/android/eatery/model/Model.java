package com.cornellappdev.android.eatery.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public interface Model {

  void parseJSONObject(Context context, boolean hardcoded, JSONObject obj) throws JSONException;
}
