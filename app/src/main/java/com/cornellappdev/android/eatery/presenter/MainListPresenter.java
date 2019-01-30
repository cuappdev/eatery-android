package com.cornellappdev.android.eatery.presenter;

import android.view.View;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class MainListPresenter {

	private View view;
	private ArrayList<EateryBaseModel> eateryList;
	public Repository rInstance = Repository.getInstance();

	public MainListPresenter(View view) {
		this.view = view;
		eateryList = rInstance.getEateryList();
	}

	public ArrayList<EateryBaseModel> getEateryList(){
		return eateryList;
	}
}
