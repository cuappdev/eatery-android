package com.cornellappdev.android.eatery.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public abstract class EateryBaseModel implements Serializable, Comparable<EateryBaseModel> {

  public enum Status {
    OPEN("Open"),
    CLOSINGSOON("Closing Soon"),
    CLOSED("Closed");
    private String display_name;
    private Status(String display_name) {
      this.display_name = display_name;
    }

    @Override
    public String toString(){
      return display_name;
    }
  }
  private ArrayList<String> mSearchedItems;
  private boolean matchesFilter = true;
  private boolean mMatchesSearch = true;
  protected boolean mOpenPastMidnight = false;
  private CampusArea mArea;
  private Double mLatitude, mLongitude;
  protected String mBuildingLocation, mName, mNickName;
  protected int mId;
  private List<PaymentMethod> mPayMethods;

  // Implemented Getters
  public String getName() {
    return mName;
  }

  public String getNickName() {
    return mNickName;
  }

  public CampusArea getArea() {
    return mArea;
  }

  public double getLattitude() {
    return mLatitude;
  }

  public double getLongitude() {
    return mLongitude;
  }

  public String getBuildingLocation() {
    return mBuildingLocation;
  }

  public ArrayList<String> getSearchedItems() {
    return mSearchedItems;
  }


  public void setMatchesSearch(boolean b) {
    mMatchesSearch = b;
  }

  public void setSearchedItems(ArrayList<String> searchedItems) {
    this.mSearchedItems = searchedItems;
  }

  public void setMatchesFilter(boolean b) {
    matchesFilter = b;
  }

  public boolean matchesSearch() {
    return mMatchesSearch;
  }

  public boolean matchesFilter() {
    return matchesFilter;
  }

  // Abstract Getters
  public abstract Status getCurrentStatus();
//  public abstract MealModel getMenu();
  public abstract HashSet<String> getMealItems();

  /**
   * This method returns a LocalDateTime. If the eatery has a current status of open or closing soon
   * then the time returned represents the closing time. If the eatery is closed the time returned
   * represents the opening time. This method can return null in the case when there is no known
   * next time for opening.
   */
  public abstract LocalDateTime getChangeTime();

  public boolean isOpen() {
    Status status = getCurrentStatus();
    return status == Status.OPEN || status == Status.CLOSINGSOON;
  }

  public boolean hasPaymentMethod(PaymentMethod method) {
    return mPayMethods.contains(method);
  }

  public void parseJSONObject(Context context, boolean hardcoded, JSONObject eatery)
      throws JSONException {
    mName = eatery.getString("name");
    mBuildingLocation = eatery.getString("location");
    mNickName = eatery.getString("nameshort");
    mLatitude = eatery.getDouble("latitude");
    mLongitude = eatery.getDouble("longitude");

    JSONArray methods = eatery.getJSONArray("payMethods");
    List<PaymentMethod> payMethods = new ArrayList<>();
    for (int j = 0; j < methods.length(); j++) {
      JSONObject method = methods.getJSONObject(j);
      payMethods.add(PaymentMethod.fromShortDescription(method.getString("descrshort")));
    }
    mPayMethods = payMethods;

    String area = eatery.getJSONObject("campusArea").getString("descrshort");
    mArea = CampusArea.fromShortDescription(area);
  }

  public static Comparator<EateryBaseModel> cafeNameComparator = (s1, s2) -> {
    String str1 = s1.getNickName();
    String str2 = s2.getNickName();
    if (str1.startsWith("1")) {
      return -1;
    }
    if (str2.startsWith("1")) {
      return 1;
    }
    // ascending order
    return str1.compareToIgnoreCase(str2);
  };


  /**
   * Compared the time of two EateryModel
   **/
  public int compareTo(@NonNull EateryBaseModel cm) {
    if (cm.getCurrentStatus() == getCurrentStatus()) {
      return this.getNickName().compareTo(cm.getNickName());
    } else if (isOpen() && !cm.isOpen()) {
      return -1;
    } else {
      return 1;
    }


  }
}
