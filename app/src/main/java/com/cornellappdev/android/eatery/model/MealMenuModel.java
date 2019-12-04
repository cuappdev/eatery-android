package com.cornellappdev.android.eatery.model;

import com.cornellappdev.android.eatery.AllEateriesQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class to get menu for each meal
 */

public class MealMenuModel implements Serializable {
    private Map<String, List<String>> menu = new LinkedHashMap<>();

    static MealMenuModel fromGraphQL(List<AllEateriesQuery.Menu> menuList) {
        MealMenuModel menuModel = new MealMenuModel();
        for (AllEateriesQuery.Menu menu : menuList) {
            for (AllEateriesQuery.Item item : menu.items()) {
                menuModel.addItem(menu.category(), item.item());
            }
        }
        return menuModel;
    }

    private void addItem(String category, String item) {
        if (menu.get(category) == null) {
            menu.put(category, new ArrayList<>());
        }
        menu.get(category).add(item);
    }

    public List<String> getItems(String category) {
        if (menu.get(category) == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(menu.get(category));
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
            flatMenu.addAll(getItems(c));
        }
        return flatMenu;
    }

    public int getNumberOfCategories() {
        return menu.keySet().size();
    }

    List<String> getAllItems() {
        List<String> items = new ArrayList<>();
        for (List<String> val : menu.values()) {
            items.addAll(val);
        }
        return items;
    }
}
