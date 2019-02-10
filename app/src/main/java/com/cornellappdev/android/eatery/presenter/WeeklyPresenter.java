package com.cornellappdev.android.eatery.presenter;

import android.view.View;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.MealType;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

public class WeeklyPresenter {

	private View view;
	private ArrayList<EateryBaseModel> eateryList;
	private LocalDate selectedDate;
	public Repository rInstance = Repository.getInstance();

	public WeeklyPresenter(View view) {
		this.view = view;
		eateryList = rInstance.getEateryList();
		selectedDate = LocalDate.now();
	}

	public LocalDate resetSelectedDate() {
		selectedDate = LocalDate.now();
		return selectedDate;
	}

	public LocalDate getDayInWeek (int offset) {
		return selectedDate.plusDays(offset);
	}

	public LocalDate getSelectedDate() {
		return selectedDate;
	}

	public ArrayList<DiningHallModel> getDiningHallList() {
		ArrayList<DiningHallModel> diningHallList = new ArrayList<>();
		for (EateryBaseModel m : eateryList) {
			if (m instanceof DiningHallModel) {
				diningHallList.add((DiningHallModel) m);
			}
		}
		return diningHallList;
	}

	public HashMap<String, ArrayList<DiningHallModel>> generateAreaLists(MealType mealType, LocalDate date) {
		ArrayList<DiningHallModel> westList = new ArrayList<>();
		ArrayList<DiningHallModel> northList = new ArrayList<>();
		ArrayList<DiningHallModel> centralList = new ArrayList<>();
		for (DiningHallModel dhm : getDiningHallList()) {
			if (dhm.getMealByDateAndType(date, mealType) != null ||
					(mealType == MealType.LUNCH && dhm.getMealByDateAndType(date, MealType.BRUNCH) != null)) {
				switch (dhm.getArea()) {
					case NORTH:
						northList.add(dhm);
						break;
					case WEST:
						westList.add(dhm);
						break;
					case CENTRAL:
						centralList.add(dhm);
						break;
				}
			}
		}
		HashMap<String, ArrayList<DiningHallModel>> finalList = new HashMap<>();
		finalList.put("West", westList);
		finalList.put("North", northList);
		finalList.put("Central", centralList);
		return finalList;
	}

}
