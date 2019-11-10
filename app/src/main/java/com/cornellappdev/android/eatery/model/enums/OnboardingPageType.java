package com.cornellappdev.android.eatery.model.enums;

public enum OnboardingPageType {
    MENUS,
    COLLEGETOWN,
    TRANSACTIONS,
    LOGIN;

    public String getTitle() {
        switch (this) {
            case MENUS:
                return "Menus";
            case COLLEGETOWN:
                return "Collegetown";
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
            case COLLEGETOWN:
                return "Find info about your favorite Collegetown spots.";
            case TRANSACTIONS:
                return "Track your swipes, BRBs, meal history, and more.";
            case LOGIN:
                return "To get the most out of Eatery, log in with your netID.";
            default:
                return "";
        }
    }
}