package com.cornellappdev.android.eatery.model;

/**
 * Created by Evan Welsh on 10/2/18.
 */
public enum CampusArea {
  NORTH,
  WEST,
  CENTRAL;

  public static CampusArea fromShortDescription(String area) {
    switch (area.toLowerCase()) {
      case "north":
        return NORTH;
      case "west":
        return WEST;
      case "central":
      default:
        return CENTRAL;
    }
  }
}

