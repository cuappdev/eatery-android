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
import org.threeten.bp.ZonedDateTime;

/**
 * Created by JC on 2/15/18. This represents a single Cafeteria (either a cafe or a dining hall)
 */

public abstract class EateryModel implements Model, Cloneable, Serializable,
    Comparable<EateryModel> {

  protected int mId;
  private double mLatitude, mLongitude;
  protected boolean mOpenPastMidnight;

  protected String mName, mNickName;
  protected CampusArea mArea;
  private String mBuildingLocation;
  protected List<String> mPayMethods;


  public abstract ZonedDateTime getNextOpening();

  public abstract ZonedDateTime getCloseTime();

  public String stringTo() {
    String info = "Name/mNickName: " + mName + "/" + mNickName;
    String locationString = "Location: " + ", Area: " + mArea;
    String payMethodsString = "Pay Methods: " + mPayMethods.toString();
    String menuString = "";

    return info + "\n" + locationString + "\n" + payMethodsString + "\n" + "Menu" + "\n"
        + menuString;
  }

  public abstract List<String> getMealItems();

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

  public List<String> getPayMethods() {
    return mPayMethods;
  }

  public String getBuildingLocation() {
    return mBuildingLocation;
  }

  public int getId() {
    return mId;
  }

  public void setId(int id) {
    this.mId = id;
  }

  public LatLng getLatLng() {
    return new LatLng(mLatitude, mLongitude);
  }

  public boolean isOpenPastMidnight() {
    return mOpenPastMidnight;
  }

  public CampusArea getArea() {
    return mArea;
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
  public static Comparator<EateryModel> cafeNameComparator = (s1, s2) -> {
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
  };


  @Override
  public void parseJSONObject(Context context, boolean hardcoded, JSONObject eatery)
      throws JSONException {
    mName = eatery.getString("name");
    mBuildingLocation = eatery.getString("location");
    mNickName = eatery.getString("nameshort");

    mLatitude = eatery.getDouble("latitude");
    mLongitude = eatery.getDouble("longitude");

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



