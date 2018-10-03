package com.cornellappdev.android.eatery;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * Created by Evan Welsh on 10/2/18.
 */
public class EateryApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    AndroidThreeTen.init(this);
  }
}
