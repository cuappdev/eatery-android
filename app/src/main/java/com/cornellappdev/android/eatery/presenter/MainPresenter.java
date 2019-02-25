package com.cornellappdev.android.eatery.presenter;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.MainListAdapter;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainPresenter{

	public Repository rInstance = Repository.getInstance();

	public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
		rInstance.setEateryList(eateryList);
		rInstance.setSearchList(rInstance.getEateryList());
	}

	public String query = "";

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

    public void setQuery(String query){
	    this.query = query;
    }

    public void filterCurrentList() {
        searchList(query);
    }
	public ArrayList<EateryBaseModel> getCurrentList(){
        ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
        for (EateryBaseModel em : rInstance.getEateryList()) {
            if (em.matchesFilter() && em.matchesSearch()) {
                cafesToDisplay.add(em);
            }
        }
        return cafesToDisplay;
    }
}


