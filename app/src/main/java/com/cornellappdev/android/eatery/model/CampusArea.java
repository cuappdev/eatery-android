package com.cornellappdev.android.eatery.model;

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

