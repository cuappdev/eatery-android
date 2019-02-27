package com.cornellappdev.android.eatery.presenter;

import android.util.Log;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainPresenter {

  private Repository rInstance = Repository.getInstance();
  private String query = "";

  public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
    rInstance.setEateryList(eateryList);
    rInstance.setSearchList(rInstance.getEateryList());
  }

  private void searchList(String query) {
    final String lowercaseQuery = query.toLowerCase();
    for (EateryBaseModel model : rInstance.getSearchList()) {
      final HashSet<String> mealSet = model.getMealItems();

      boolean foundNickName = false;
      if (model.getNickName().toLowerCase().contains(lowercaseQuery)) {
        foundNickName = true;
      }

      ArrayList<String> matchedItems = new ArrayList<>();
      boolean foundItem = false;
      for (String item : mealSet) {
        if (item.toLowerCase().contains(lowercaseQuery)) {
          foundItem = true;
          matchedItems.add(item);
        }
      }

      if (model.matchesFilter() && (foundItem || foundNickName)) {
        if (foundNickName) {
          model.setSearchedItems(new ArrayList<>(mealSet));
        } else {
          model.setSearchedItems(matchedItems);
        }
        model.setMatchesSearch(true);
      } else {
        model.setMatchesSearch(false);
      }
    }
  }

  public void setQuery(String query) {
    this.query = query;
  }

  // Updates the eatery models to matchSearch for this specific query
  public void filterCurrentList() {
    searchList(query);
  }

  // Returns all eateries that matchsearch and filter
  public ArrayList<EateryBaseModel> getCurrentList() {
    ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
    for (EateryBaseModel em : rInstance.getEateryList()) {
      if (em.matchesFilter() && em.matchesSearch()) {
        cafesToDisplay.add(em);
      }
    }
    return cafesToDisplay;
  }
}


