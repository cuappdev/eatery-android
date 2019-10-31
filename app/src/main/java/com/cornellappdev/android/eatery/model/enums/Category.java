package com.cornellappdev.android.eatery.model.enums;

public enum Category {
    Coffee,
    American,
    Chinese,
    Mediterranean,
    Japanese,
    Indian,
    Korean,
    Italian,
    Vietnamese,
    Thai,
    Grocery,
    Desserts;

    public static Category fromShortDescription(String category) {
        if (category.toLowerCase().contains("coffee")) {
            return Coffee;
        } else if (category.toLowerCase().contains("american")) {
            return American;
        } else {
            switch (category.toLowerCase()) {
                case "burgers":
                    return American;
                case "sandwiches":
                    return American;
                case "chinese":
                    return Chinese;
                case "dim sum":
                    return Chinese;
                case "middle eastern":
                    return Mediterranean;
                case "mediterranean":
                    return Mediterranean;
                case "japanese":
                    return Japanese;
                case "poke":
                    return Japanese;
                case "indian":
                    return Indian;
                case "korean":
                    return Korean;
                case "italian":
                    return Italian;
                case "vietnamese":
                    return Vietnamese;
                case "thai":
                    return Thai;
                case "grocery":
                    return Grocery;
                case "convenience stores":
                    return Grocery;
                case "desserts":
                    return Desserts;
                default:
                    return null;
            }
        }

    }
}