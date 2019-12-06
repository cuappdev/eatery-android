package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cornellappdev.android.eatery.onboarding.OnboardingActivity;

// This activity is a transparent one that decides whether to transfer flow to Onboarding
// or to MainActivity
public class InitActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        preferences.edit().putBoolean("onboarding_complete",false).apply();
        if(!preferences.getBoolean("onboarding_complete",false)) {
            startOnboarding();
        }
        else {
            startMain();
        }
        finish();
    }


    public void startOnboarding() {
        Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
        startActivity(intent);
    }

    public void startMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
