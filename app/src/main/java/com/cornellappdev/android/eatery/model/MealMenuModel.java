package com.cornellappdev.android.eatery.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class to get menu for each meal
 */

public class MealMenuModel implements Serializable {
	private Map<String, List<String>> menu = new LinkedHashMap<>();

	public void addItem(String category, String item) {
		if (menu.get(category) == null) {
			menu.put(category, new ArrayList<String>());
		}
		menu.get(category).add(item);
	}

	public List<String> getItems(String category) {
		if (menu.get(category) == null) {
			return new ArrayList<>();
		}
		return new ArrayList<>(menu.get(category));
	}

	public String getCategory(int i) {
		if (menu.isEmpty() || getNumberOfCategories() < i) {
			return "";
		}
		return getCategories().get(i);
	}

	public boolean containsCategory(String category) {
		HashSet<String> categories = new HashSet<>(getCategories());
		return categories.contains(category);
	}

	public List<String> getCategories() {
		if (menu.isEmpty()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(menu.keySet());
	}

	public List<String> getMenuAsList() {
		List<String> flatMenu = new ArrayList<>();
		for (String c : getCategories()) {
			flatMenu.add(c);
			for (String item : getItems(c)) {
				flatMenu.add(item);
			}
		}
		return flatMenu;
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
