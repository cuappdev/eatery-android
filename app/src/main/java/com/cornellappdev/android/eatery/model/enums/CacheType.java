package com.cornellappdev.android.eatery.model.enums;

// Enum represents possible keys that can be stored with data in internal storage
public enum CacheType {
    BRB("brb"),
    CAMPUS_EATERY("campus_eatery"),
    CTOWN_EATERY("ctown_eatery");
    private final String key;

    CacheType(String key) {
        this.key = key;
    }

    public String getString() {
        return key;
    }

}
