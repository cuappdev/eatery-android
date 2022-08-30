package com.cornellappdev.android.eatery.model.enums;

import com.cornellappdev.android.eatery.R;

public enum OnboardingPageType {
    MENUS,
    TRANSACTIONS,
    LOGIN;

    public int getAnimationRaw() {
        if (this == OnboardingPageType.TRANSACTIONS) {
            return R.raw.transactions;
        }
        return R.raw.menus;
    }

    public String getTitle() {
        switch (this) {
            case MENUS:
                return "Menus";
            case TRANSACTIONS:
                return "Transactions";
            case LOGIN:
                return "Login";
            default:
                return "";
        }
    }

    public String getDescription() {
        switch (this) {
            case MENUS:
                return "See what’s being served at any campus eatery.";
            case TRANSACTIONS:
                return "Track your swipes, BRBs, meal history, and more.";
            case LOGIN:
                return "To get the most out of Eatery, log in with your netID.";
            default:
                return "";
        }
    }
}
