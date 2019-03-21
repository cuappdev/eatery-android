package com.cornellappdev.android.eatery.model.enums;

public enum CampusArea {
    CENTRAL,
    NORTH,
    WEST;

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


