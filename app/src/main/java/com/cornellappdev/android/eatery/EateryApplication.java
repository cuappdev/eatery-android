package com.cornellappdev.android.eatery;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jakewharton.threetenabp.AndroidThreeTen;


public class EateryApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Fresco.initialize(this);
		AndroidThreeTen.init(this);
	}
}
