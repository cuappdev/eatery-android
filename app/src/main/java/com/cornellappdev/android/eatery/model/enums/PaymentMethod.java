package com.cornellappdev.android.eatery.model.enums;

public enum PaymentMethod {
	BRB,
	CREDIT,
	SWIPES;

	public static PaymentMethod fromShortDescription(String descr) {
		if (descr.toLowerCase().contains("debit")) {
			return BRB;
		} else if (descr.toLowerCase().contains("credit")) {
			return CREDIT;
		} else if (descr.toLowerCase().contains("swipe")) {
			return SWIPES;
		}
		return null;
	}
}

