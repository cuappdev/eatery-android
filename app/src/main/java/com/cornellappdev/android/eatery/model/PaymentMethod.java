package com.cornellappdev.android.eatery.model;

import java.util.HashMap;
import java.util.Map;

public enum PaymentMethod {
  CREDIT_DEBIT("Major Credit Cards"),
  BRB("Meal Plan - Debit"),
  CASH("Cash"),
  MOBILE_PAYMENTS("Mobile Payments"),
  SWIPES("Meal Plan - Swipe");

  private final String shortDescription;

  PaymentMethod(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  private final static Map<String, PaymentMethod> mapping = new HashMap<>();

  public static PaymentMethod fromShortDescription(String shortDescr) {
    String cleanShortDescr = shortDescr.trim().toLowerCase();

    PaymentMethod method = mapping.get(cleanShortDescr);

    if (method != null) {
      return method;
    }

    for (PaymentMethod paymentMethod : values()) {
      if (paymentMethod.shortDescription.contains(cleanShortDescr)) {
        return paymentMethod;
      }
    }

    return null;
  }

  static {
    for (PaymentMethod method : values()) {
      mapping.put(method.shortDescription.toLowerCase(), method);
    }
  }
}

