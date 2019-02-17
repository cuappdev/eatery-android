package com.cornellappdev.android.eatery.presenter;

import android.support.v7.widget.SearchView;
import android.view.View;

import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainPresenter implements SearchView.OnQueryTextListener {

	private View view;
	public Repository rInstance = Repository.getInstance();

	public MainPresenter(View view) {
		this.view = view;
	}

	public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
		rInstance.setEateryList(eateryList);
	}

	public interface View{
		void showProgressBar();
		void hideProgressBar();
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

	@Override
	public boolean onQueryTextSubmit(String query) {
		this.query = query;
		ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
		if (query.length() == 0) {
			rInstance.setSearchList(rInstance.getEateryList());
			rInstance.setIsSearchPressed(false);
			for (EateryBaseModel cm : rInstance.getSearchList()) {
				if (cm.matchesFilter()) {
					cafesToDisplay.add(cm);
				}
			}
		} else {
			rInstance.setIsSearchPressed(true);
			searchList(query);
			for (EateryBaseModel cm : rInstance.getSearchList()) {
				if (cm.matchesFilter() && cm.matchesSearch()) {
					cafesToDisplay.add(cm);
				}
			}
		}
		Collections.sort(cafesToDisplay);
//		listAdapter.setList(
//				cafesToDisplay, cafesToDisplay.size(), query.length() == 0 ? null : query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return onQueryTextSubmit(newText);
	}
}


