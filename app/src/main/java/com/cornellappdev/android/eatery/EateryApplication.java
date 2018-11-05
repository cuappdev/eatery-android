package com.cornellappdev.android.eatery;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class EateryApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		AndroidThreeTen.init(this);
	}
}
