package com.cornellappdev.android.eatery;

import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class Repository {
	private ArrayList<EateryBaseModel> eateryList = new ArrayList<>();
	private ArrayList<EateryBaseModel> searchList = new ArrayList<>();
	private boolean isSearchPressed = false;
	static final Repository repoInstance = new Repository();

	public static Repository getInstance() {
		return repoInstance;
	}

	public void setEateryList(ArrayList<EateryBaseModel> eateryList) {
		this.eateryList = eateryList;
	}

	public ArrayList<EateryBaseModel> getEateryList() {
		return eateryList;
	}

	public void setSearchList(ArrayList<EateryBaseModel> searchList) {
		this.searchList = searchList;
	}

	public ArrayList<EateryBaseModel> getSearchList() {
		return searchList;
	}

	public void setIsSearchPressed(boolean bool) {isSearchPressed = bool; }

	public boolean isSearchPressed() {
		return isSearchPressed;
	}


}
