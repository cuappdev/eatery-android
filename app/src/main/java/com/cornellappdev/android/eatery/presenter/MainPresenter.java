package com.cornellappdev.android.eatery.presenter;

import android.view.View;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;

public class MainPresenter {

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
}


