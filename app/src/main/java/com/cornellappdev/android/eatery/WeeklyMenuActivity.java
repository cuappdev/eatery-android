package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cornellappdev.android.eatery.Model.CafeteriaModel;
import com.cornellappdev.android.eatery.Model.MealModel;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeeklyMenuActivity extends AppCompatActivity {
    public BottomNavigationView bnv;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<CafeteriaModel> cafeData;
    ArrayList<CafeteriaModel> diningHall = new ArrayList<>();
    String mealType;
    int selectedDate;
    TextView breakfastText;
    TextView lunchText;
    TextView dinnerText;
    LinearLayout linDate;
    ArrayList<TextView> dateList = new ArrayList<>();
    ArrayList<ArrayList<HashMap<CafeteriaModel, MealModel>>> weeklyMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_menu);
        bnv = findViewById(R.id.bottom_navigation);
        breakfastText = findViewById(R.id.breakfast);
        lunchText = findViewById(R.id.lunch);
        dinnerText = findViewById(R.id.dinner);
        expListView = findViewById(R.id.expandablelistview);
        linDate = findViewById(R.id.lin_date);

        Intent intent = getIntent();
        cafeData = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("cafeData");

        //layout for menu list
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        expListView.setIndicatorBounds(width-250, width);

        //populate list of date textviews
        dateList.add((TextView) findViewById(R.id.date0));
        dateList.add((TextView) findViewById(R.id.date1));
        dateList.add((TextView) findViewById(R.id.date2));
        dateList.add((TextView) findViewById(R.id.date3));
        dateList.add((TextView) findViewById(R.id.date4));
        dateList.add((TextView) findViewById(R.id.date5));
        dateList.add((TextView) findViewById(R.id.date6));

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        for (int i = 0; i < 7; i++) {
            //formatting for day
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");
            dateFormat.setCalendar(cal);
            SpannableString ssDay = new SpannableString(dateFormat.format(cal.getTime()));
            ssDay.setSpan(new RelativeSizeSpan(0.25f), 0,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //formatting for date
            int date = cal.get(Calendar.DAY_OF_MONTH);
            SpannableString ssDate = new SpannableString(Integer.toString(date));
            ssDate.setSpan(new RelativeSizeSpan(1f), 0,ssDate.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView tv = dateList.get(i);
            tv.setText(ssDay + "\n" + ssDate);

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        //create list of dining halls
        for (CafeteriaModel m : cafeData) {
            if (m.getIs_diningHall()) {
                diningHall.add(m);
            }
        }

        //parsing the weekly menu when the user starts this activity
        weeklyMenu = parseWeeklyMenu(dateList);

        breakfastText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakfastText.setTextColor(Color.parseColor("#000000"));
                lunchText.setTextColor(Color.parseColor("#cdcdcd"));
                dinnerText.setTextColor(Color.parseColor("#cdcdcd"));

                mealType = "breakfast";
                changeListAdapter(mealType, selectedDate);
            }
        });

        lunchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
                lunchText.setTextColor(Color.parseColor("#000000"));
                dinnerText.setTextColor(Color.parseColor("#cdcdcd"));

                mealType = "lunch";
                changeListAdapter(mealType, selectedDate);
            }
        });

        dinnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakfastText.setTextColor(Color.parseColor("#cdcdcd"));
                lunchText.setTextColor(Color.parseColor("#cdcdcd"));
                dinnerText.setTextColor(Color.parseColor("#000000"));

                mealType = "dinner";
                changeListAdapter(mealType, selectedDate);
            }
        });

        //if no buttons are selected, the default menu is set to the current day's breakfast
        changeListAdapter("breakfast", 0);

        //adds functionality to bottom nav bar
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast toast;
                Intent intent;
                switch(item.getItemId()) {
                    case R.id.action_home:
                        finish();

                        break;
                    case R.id.action_week:
                        break;
                    case R.id.action_brb:
                        toast = Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Changes the text color to black when selected
     */
    public void dateFilterClick (View v) {
        TextView tv = (TextView) v;

        tv.setTextColor(Color.parseColor("#000000"));
        changeDateColor(tv);

        int id = tv.getId();
        switch (id){
            case R.id.date0:
                selectedDate = 0;
                break;
            case R.id.date1:
                selectedDate = 1;
                break;
            case R.id.date2:
                selectedDate = 2;
                break;
            case R.id.date3:
                selectedDate = 3;
                break;
            case R.id.date4:
                selectedDate = 4;
                break;
            case R.id.date5:
                selectedDate = 5;
                break;
            case R.id.date6:
                selectedDate = 6;
                break;
        }
        changeListAdapter(mealType, selectedDate);
    }


    /**
     * Changes the text color to grey if the date is not selected
     */
    public void changeDateColor(TextView v) {
        for (int i=0; i< 7; i++) {
            if (!dateList.get(i).equals(v)) {
                dateList.get(i).setTextColor(Color.parseColor("#cdcdcd"));
            }
        }
    }


    /**
     * Updates the list of dining halls and menus that is displayed
     */
    public void changeListAdapter(String mealType, int dateOffset) {
        int mealIndex = 0;
        switch (mealType){
            case "breakfast":
                mealIndex = 0;
                break;
            case "lunch":
                mealIndex = 1;
                break;
            case "dinner":
                mealIndex = 2;
                break;
        }
        listAdapter = new ExpandableListAdapter(getApplicationContext(), diningHall, generateFinalList(weeklyMenu.get(dateOffset).get(mealIndex)));
        expListView.setAdapter(listAdapter);
    }


    /**
     * Generates a list of for breakfast, lunch, and dinner for a particular day.
     * Day is determined by the dateOffset from the current time.
     */
    public ArrayList<HashMap<CafeteriaModel, MealModel>> generateMealLists(int dateOffset) {
        //initialization
        ArrayList<HashMap<CafeteriaModel, MealModel>> finalList = new ArrayList<>();
        HashMap<CafeteriaModel, MealModel> breakfastList = new HashMap<>();
        HashMap<CafeteriaModel, MealModel> lunchList = new HashMap<>();
        HashMap<CafeteriaModel, MealModel> dinnerList = new HashMap<>();

        for (CafeteriaModel m : diningHall) {
            //if dining hall is opened
            if (m.indexOfCurrentDay() != -1) {
                //get MealModel for the day and split into three hashmaps
                ArrayList<MealModel> meals = m.getWeeklyMenu().get(m.indexOfCurrentDay() + dateOffset);
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
        finalList.add(breakfastList);
        finalList.add(lunchList);
        finalList.add(dinnerList);
        return finalList;
    }


    /**
     * Converts the MealModel object of the map into an Arraylist
     */
    private HashMap<CafeteriaModel, ArrayList<String>> generateFinalList(HashMap<CafeteriaModel, MealModel> listToParse) {
        HashMap<CafeteriaModel, ArrayList<String>> listFinal = new HashMap<CafeteriaModel, ArrayList<String>>();
        for (Map.Entry<CafeteriaModel, MealModel> cafe : listToParse.entrySet()) {
            ArrayList<String> mealToList = new ArrayList<String>();

            //gets menu of dining hall
            MealModel m = cafe.getValue();
            HashMap<String, ArrayList<String>> entrySet = m.getMenu();

            //add both category + meal items into an arraylist
            for (Map.Entry<String, ArrayList<String>> entry : entrySet.entrySet()) {
                String key = "1" + entry.getKey();    //'1' in front to note category, for parsing purposes
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

    
    /**
     * Assume that dateList is not null.
     * Returns an Arraylist of the set(breakfastlist, lunchlist, dinnerlist) for each day in the dateList.
     * Each of the meal lists is a hashmap of CafeteriaModels that have that specific meal and the menu
     */
    public ArrayList<ArrayList<HashMap<CafeteriaModel, MealModel>>> parseWeeklyMenu(ArrayList<TextView> dateList) {
        ArrayList<ArrayList<HashMap<CafeteriaModel, MealModel>>> mainList = new ArrayList<>();

        for (int i = 0; i < dateList.size(); i++) {
            //list contains set(breakfastlist, lunchlist, dinnerlist) for the day
            ArrayList<HashMap<CafeteriaModel, MealModel>> dailyList = new ArrayList<>();
            dailyList = generateMealLists(i);
            mainList.add(dailyList);
        }

        return mainList;
    }
}
