package com.example.jc.eatery_android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class WeeklyMenuActivity extends AppCompatActivity {
    public BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_menu);
        bnv = findViewById(R.id.bottom_navigation);

        //adds functionality to bottom nav bar
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast toast;
                Intent intent;
                switch(item.getItemId()) {
                    case R.id.action_home:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_week:
                        toast = Toast.makeText(getApplicationContext(), "Weekly Menu", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    case R.id.action_brb:
                        toast = Toast.makeText(getApplicationContext(), "BRB", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }
                return true;
            }
        });
    }
}
