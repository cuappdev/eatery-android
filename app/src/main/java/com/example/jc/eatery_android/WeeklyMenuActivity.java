package com.example.jc.eatery_android;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jc.eatery_android.Data.CafeteriaContract;
import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.Model.MealModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeeklyMenuActivity extends AppCompatActivity {
    public BottomNavigationView bnv;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<CafeteriaModel> cafeData;
    ArrayList<CafeteriaModel> diningHall = new ArrayList<>();
    String mealType;
    TextView breakfastText;
    TextView lunchText;
    TextView dinnerText;
    HashMap<CafeteriaModel, MealModel> breakfastList = new HashMap<>();
    HashMap<CafeteriaModel, MealModel> lunchList = new HashMap<>();
    HashMap<CafeteriaModel, MealModel> dinnerList = new HashMap<>();
    HashMap<CafeteriaModel, MealModel> listToParse = new HashMap<CafeteriaModel, MealModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_menu);
        bnv = findViewById(R.id.bottom_navigation);
        Intent intent = getIntent();
        cafeData = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("cafeData");
        breakfastText = findViewById(R.id.breakfast);
        lunchText = findViewById(R.id.lunch);
        dinnerText = findViewById(R.id.dinner);
        expListView = findViewById(R.id.expandablelistview);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        expListView.setIndicatorBounds(width-250, width);

        for (CafeteriaModel m : cafeData) {
            if (m.getIs_diningHall()) {
                diningHall.add(m);
            }
        }

        for (CafeteriaModel m : diningHall) {


            if (m.indexOfCurrentDay() != -1) {
                ArrayList<MealModel> meals = m.getWeeklyMenu().get(m.indexOfCurrentDay());
                for (MealModel n : meals) {
                    if (n.getType().equals("Breakfast")) {
                        breakfastList.put(m, n);
                    }
                    if (n.getType().equals("Lunch") || n.getType().equals("Brunch")) {
                        lunchList.put(m, n);
                    }
                    if (n.getType().equals("Dinner")) {
                        dinnerList.put(m, n);
                    }
                }
            }
        }

        breakfastText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakfastText.setTextColor(Color.parseColor("#000000"));
                lunchText.setTextColor(Color.parseColor("#cdcdcd"));
                dinnerText.setTextColor(Color.parseColor("#cdcdcd"));
                listToParse = breakfastList;
                listAdapter = new ExpandableListAdapter(getApplicationContext(), diningHall, generateFinalList(listToParse));
                expListView.setAdapter(listAdapter);
            }
        });

        lunchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
                lunchText.setTextColor(Color.parseColor("#000000"));
                dinnerText.setTextColor(Color.parseColor("#cdcdcd"));
                listToParse = lunchList;
                listAdapter = new ExpandableListAdapter(getApplicationContext(), diningHall, generateFinalList(listToParse));
                expListView.setAdapter(listAdapter);
            }
        });

        dinnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
                lunchText.setTextColor(Color.parseColor("#cdcdcd"));
                dinnerText.setTextColor(Color.parseColor("#000000"));
                listToParse = dinnerList;
                listAdapter = new ExpandableListAdapter(getApplicationContext(), diningHall, generateFinalList(listToParse));
                expListView.setAdapter(listAdapter);
            }
        });

        if (listToParse.size() == 0) {
            listToParse = breakfastList;
            listAdapter = new ExpandableListAdapter(getApplicationContext(), diningHall, generateFinalList(listToParse));
            expListView.setAdapter(listAdapter);
        }


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

    private HashMap<CafeteriaModel, ArrayList<String>> generateFinalList(HashMap<CafeteriaModel, MealModel> listToParse) {
        HashMap<CafeteriaModel, ArrayList<String>> listFinal = new HashMap<CafeteriaModel, ArrayList<String>>();
        for (Map.Entry<CafeteriaModel, MealModel> cafe : listToParse.entrySet()) {
            ArrayList<String> mealToList = new ArrayList<String>();
            MealModel m = cafe.getValue();
            HashMap<String, ArrayList<String>> entrySet = m.getMenu();
            for (Map.Entry<String, ArrayList<String>> entry : entrySet.entrySet()) {
                String key = "1" + entry.getKey();
                ArrayList<String> values = entry.getValue();

                mealToList.add(key);
                for (String items : values) {
                    mealToList.add(items);
                }
            }
            listFinal.put(cafe.getKey(), mealToList);
        }
        return listFinal;
    }
}
