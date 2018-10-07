package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** Created by abdullahislam on 2/18/18. */
public class MealModel implements Serializable {
  private Date start;
  private Date end;
  private String type;
  private HashMap<String, ArrayList<String>> menu;

  // debug version of toString()
  public String stringTo() {
    String info = String.format("%s on from %s to %s\n", type, start.toString(), end.toString());
    for (HashMap.Entry<String, ArrayList<String>> entry : menu.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      info += String.format(" key: %s\n", value.toString());
    }
    return info;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public HashMap<String, ArrayList<String>> getMenu() {
    return menu;
  }

  public void setMenu(HashMap<String, ArrayList<String>> menu) {
    this.menu = menu;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
