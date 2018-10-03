package com.cornellappdev.android.eatery;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class EateryApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
  }
}
