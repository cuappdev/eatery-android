package com.cornellappdev.android.eatery.presenter;

import android.view.View;
import android.widget.Button;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;

import java.util.ArrayList;
import java.util.HashSet;

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
