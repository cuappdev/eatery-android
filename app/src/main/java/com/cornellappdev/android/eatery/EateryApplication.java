package com.cornellappdev.android.eatery;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class EateryApplication extends Application {

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
