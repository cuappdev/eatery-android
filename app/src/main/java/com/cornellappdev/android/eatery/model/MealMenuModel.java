package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Evan Welsh on 10/2/18.
 */
public class MealMenuModel implements Serializable {

  private Map<String, List<String>> menu = new HashMap<>();

  public void addItem(String category, String item) {
    if (menu.get(category) == null) {
      menu.put(category, new ArrayList<>());
    }

    menu.get(category).add(item);
  }

  public List<String> getItems(String category) {
    return new ArrayList<>(menu.get(category));
  }

  public List<String> getCategories() {
    return new ArrayList<>(menu.keySet());
  }

  public int getNumberOfCategories() {
    return menu.keySet().size();
  }

  public List<String> getAllItems() {
    List<String> items = new ArrayList<>();

    for (List<String> val : menu.values()) {
      items.addAll(val);
    }

    return items;
  }

  public static MealMenuModel fromJSONArray(JSONArray menu) throws JSONException {
    MealMenuModel menuModel = new MealMenuModel();

    for (int m = 0; m < menu.length(); m++) {
      JSONObject stations = menu.getJSONObject(m);
      JSONArray items = stations.getJSONArray("items");

      String category = stations.getString("category");

      for (int n = 0; n < items.length(); n++) {
        String item = items.getJSONObject(n).getString("item");
        menuModel.addItem(category, item);
      }
    }
    return menuModel;
  }
}
