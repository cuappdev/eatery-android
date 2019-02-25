package com.cornellappdev.android.eatery.presenter;

import android.view.View;
import android.widget.Button;

import com.cornellappdev.android.eatery.MainListFragment;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainListPresenter {

	private ArrayList<EateryBaseModel> eateryList;
	private ArrayList<EateryBaseModel> currentList;
	private Set<Button> areaButtonsPressed;
	private Set<Button> paymentButtonsPressed;
	private HashSet<PaymentMethod> paymentSet;
	private HashSet<CampusArea> areaSet;

	public Repository rInstance = Repository.getInstance();

	public MainListPresenter() {
		eateryList = rInstance.getEateryList();
		currentList = eateryList;
		areaButtonsPressed = new HashSet<>();
		paymentButtonsPressed = new HashSet<>();
		paymentSet = new HashSet<>();
		areaSet = new HashSet<>();
	}

	public ArrayList<EateryBaseModel> getEateryList(){
		return eateryList;
	}

	public void setAreaButtonPressed(Set<Button> set) {
		areaButtonsPressed = set;
	}

	public void setPaymentButtonPressed(Set<Button> set) {
		paymentButtonsPressed = set;
	}

	public void setPaymentSet(HashSet<PaymentMethod> set) {
		paymentSet = set;
	}

	public void setAreaSet(HashSet<CampusArea> set) {
		areaSet = set;
	}

	public ArrayList<EateryBaseModel> getCurrentList() {
		return currentList;
	}

	private boolean hasPaymentMethod(EateryBaseModel model) {
		for (PaymentMethod method : paymentSet) {
			if (model.hasPaymentMethod(method)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<EateryBaseModel> getCafesToDisplay() {
		ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
		for (EateryBaseModel em : currentList) {
			if (em.matchesFilter() && em.matchesSearch()) {
				cafesToDisplay.add(em);
			}
		}
		return cafesToDisplay;
	}

	public ArrayList<EateryBaseModel> filterCurrentList() {
		for (EateryBaseModel model : currentList) {
			boolean areaFuzzyMatches =
					areaSet.isEmpty() || areaSet.contains(model.getArea());
			boolean paymentFuzzyMatches =
					paymentSet.isEmpty() || hasPaymentMethod(model);
			if (areaFuzzyMatches && paymentFuzzyMatches) {
				model.setMatchesFilter(true);
			} else {
				model.setMatchesFilter(false);
			}
		}
		return currentList;
	}
}
