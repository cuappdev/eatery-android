package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** Created by abdullahislam on 3/2/18. */
public class CafeModel implements Serializable {

  ArrayList<String> cafeMenu = new ArrayList();
  HashMap<Date, ArrayList<Date>> hours;
  HashMap<Integer, ArrayList<Date>> hoursH;

  public ArrayList<String> getCafeMenu() {
    return cafeMenu;
  }

  public void setCafeMenu(ArrayList<String> cafeMenu) {
    this.cafeMenu = cafeMenu;
  }

  public HashMap<Date, ArrayList<Date>> getHours() {
    return hours;
  }

  // This method assumes that a cafe has only one opening and closing time window per day
  public void setHours(HashMap<Date, ArrayList<Date>> hours) {
    this.hours = hours;
  }

  public HashMap<Integer, ArrayList<Date>> getHoursH() {
    return hoursH;
  }

  public void setHoursH(HashMap<Integer, ArrayList<Date>> hoursH) {
    this.hoursH = hoursH;
  }
}
