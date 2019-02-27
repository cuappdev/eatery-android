package com.cornellappdev.android.eatery.presenter;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;

import java.util.ArrayList;
import java.util.HashSet;

public class MainListPresenter {

  private ArrayList<EateryBaseModel> mEateryList;
  private ArrayList<EateryBaseModel> mCurrentList;
  private HashSet<PaymentMethod> mPaymentSet;
  private HashSet<CampusArea> mAreaSet;

  private Repository rInstance = Repository.getInstance();

  public MainListPresenter() {
    mEateryList = rInstance.getEateryList();
    mCurrentList = mEateryList;
    mPaymentSet = new HashSet<>();
    mAreaSet = new HashSet<>();
  }

  public ArrayList<EateryBaseModel> getEateryList() {
    return mEateryList;
  }

  public void setPaymentSet(HashSet<PaymentMethod> paymentSet) {
    mPaymentSet = paymentSet;
  }

  public void setAreaSet(HashSet<CampusArea> areaSet) {
    mAreaSet = areaSet;
  }

  private boolean hasPaymentMethod(EateryBaseModel model) {
    for (PaymentMethod method : mPaymentSet) {
      if (model.hasPaymentMethod(method)) {
        return true;
      }
    }
    return false;
  }

  public ArrayList<EateryBaseModel> getCafesToDisplay() {
    ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
    for (EateryBaseModel em : mCurrentList) {
      if (em.matchesFilter() && em.matchesSearch()) {
        cafesToDisplay.add(em);
      }
    }
    return cafesToDisplay;
  }

  public ArrayList<EateryBaseModel> filterCurrentList() {
    for (EateryBaseModel model : mCurrentList) {
      boolean areaFuzzyMatches =
          mAreaSet.isEmpty() || mAreaSet.contains(model.getArea());
      boolean paymentFuzzyMatches =
          mPaymentSet.isEmpty() || hasPaymentMethod(model);
      if (areaFuzzyMatches && paymentFuzzyMatches) {
        model.setMatchesFilter(true);
      } else {
        model.setMatchesFilter(false);
      }
    }
    return mCurrentList;
  }
}
