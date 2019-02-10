package com.cornellappdev.android.eatery;

import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class Repository {
	private ArrayList<EateryBaseModel> eateryList;
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

}
