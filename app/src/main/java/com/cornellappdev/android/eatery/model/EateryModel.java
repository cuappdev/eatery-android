package com.cornellappdev.android.eatery.model;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

/**
 * Created by JC on 2/15/18. This represents a single Cafeteria (either a cafe or a dining hall)
 */

public abstract class EateryModel implements Model, Cloneable, Serializable,
    Comparable<EateryModel> {

  private boolean isHardCoded;
  protected int id;
  private double lat, lng;
  protected String mName;
  protected String mNickName;
  private String mCloseTime;
  protected CampusArea mArea;
  private boolean mMatchesFilter = true;
  private boolean mMatchesSearch = true;
  private String mBuildingLocation;
  protected List<String> mPayMethods;
  private List<String> mSearchedItems;
  private boolean openPastMidnight;

  public List<String> getSearchedItems() {
    return mSearchedItems;
  }

  public void setSearchedItems(List<String> searchedItems) {
    this.mSearchedItems = searchedItems;
  }

  public abstract LocalDateTime getNextOpening(LocalDateTime time);

  public String getCloseTime() {
    return mCloseTime;
  }

  public void setCloseTime(String closeTime) {
    this.mCloseTime = closeTime;
  }

  public String stringTo() {
    String info = "Name/mNickName: " + mName + "/" + mNickName;
    String locationString = "Location: " + ", Area: " + mArea;
    String payMethodsString = "Pay Methods: " + mPayMethods.toString();
    String menuString = "";

    return info + "\n" + locationString + "\n" + payMethodsString + "\n" + "Menu" + "\n"
        + menuString;
  }

  public abstract List<String> getMealItems();

  public int indexOfCurrentDay() {
    return 0;
  }

  public boolean isOpen() {
    return getCurrentStatus() == Status.OPEN;
  }

  public abstract Status getCurrentStatus();

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    this.mName = name;
  }

  public String getNickName() {
    return mNickName;
  }

  public void setNickName(String nickName) {
    this.mNickName = nickName;
  }

  public boolean matchesFilter() {
    return mMatchesFilter;
  }

  public void setMatchesFilter(boolean yesOrNo) {
    mMatchesFilter = yesOrNo;
  }

  public boolean matchesSearch() {
    return mMatchesSearch;
  }

  public void setMatchesSearch(boolean yesOrNo) {
    mMatchesSearch = yesOrNo;
  }

  public List<String> getPayMethods() {
    return mPayMethods;
  }

  public void setPayMethods(List<String> paymentMethods) {
    this.mPayMethods = paymentMethods;
  }

  public String getBuildingLocation() {
    return mBuildingLocation;
  }

  public void setBuildingLocation(String buildingLocation) {
    this.mBuildingLocation = buildingLocation;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isHardCoded() {
    return isHardCoded;
  }

  public void setHardCoded(boolean hardCoded) {
    isHardCoded = hardCoded;
  }

  public LatLng getLatLng() {
    return new LatLng(lat, lng);
  }

  public void setLatLng(LatLng latlng) {
    this.lat = latlng.latitude;
    this.lng = latlng.longitude;
  }

  public boolean isOpenPastMidnight() {
    return openPastMidnight;
  }

  public void setOpenPastMidnight(boolean openPastMidnight) {
    this.openPastMidnight = openPastMidnight;
  }

  public CampusArea getArea() {
    return mArea;
  }

  public void setArea(CampusArea area) {
    this.mArea = area;
  }


  public enum Status {
    OPEN,
    CLOSING_SOON,
    CLOSED;

    public boolean isOpen() {
      return this == OPEN || this == CLOSING_SOON;
    }

  }

  /**
   * Compared the time of two EateryModel
   **/
  public int compareTo(@NonNull EateryModel cm) {
    if (cm.getCurrentStatus() == getCurrentStatus()) {
      return this.getNickName().compareTo(cm.getNickName());
    } else if (isOpen() && !cm.isOpen()) {
      return -1;
    } else {
      return 1;
    }
  }

  /*Comparator for sorting the list by EateryModel's nickname*/
  public static Comparator<EateryModel> cafeNameComparator = new Comparator<EateryModel>() {
    @Override
    public int compare(EateryModel s1, EateryModel s2) {
      String str1 = s1.getNickName();
      String str2 = s2.getNickName();

      // TODO Why?

      if (str1.startsWith("1")) {
        return -1;
      }

      if (str2.startsWith("1")) {
        return 1;
      }

      //ascending order

      return str1.compareToIgnoreCase(str2);
    }
  };


  @Override
  public void parseJSONObject(Context context, boolean hardcoded, JSONObject eatery)
      throws JSONException {
    isHardCoded = hardcoded;
    mName = eatery.getString("name");
    mBuildingLocation = eatery.getString("location");
    mNickName = eatery.getString("nameshort");

    this.lat = eatery.getDouble("latitude");
    this.lng = eatery.getDouble("longitude");
    // Parse payment methods available at eatery
    JSONArray methods = eatery.getJSONArray("payMethods");
    List<String> payMethods = new ArrayList<>();
    for (int j = 0; j < methods.length(); j++) {
      JSONObject method = methods.getJSONObject(j);
      payMethods.add(method.getString("descrshort"));
    }
    mPayMethods = payMethods;

    // Find geographical area for eatery
    String area = eatery.getJSONObject("campusArea").getString("descrshort");

    mArea = CampusArea.fromShortDescription(area);
  }


}



