package com.example.jc.eatery_android;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.Model.MealModel;

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
    TextView breakfastText;
    TextView lunchText;
    TextView dinnerText;
    LinearLayout linDate;
    ArrayList<TextView> dateList = new ArrayList<>();
    HashMap<CafeteriaModel, MealModel> breakfastList = new HashMap<>();
    HashMap<CafeteriaModel, MealModel> lunchList = new HashMap<>();
    HashMap<CafeteriaModel, MealModel> dinnerList = new HashMap<>();
    HashMap<CafeteriaModel, MealModel> listToParse = new HashMap<CafeteriaModel, MealModel>();
    int dateOffset = 0;

    TextView date1;
    TextView date2;
    TextView date3;
    TextView date4;
    TextView date5;
    TextView date6;
    TextView date7;

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
        linDate = findViewById(R.id.lin_date);
        date1 = findViewById(R.id.date1);
        date2 = findViewById(R.id.date2);
        date3 = findViewById(R.id.date3);
        date4 = findViewById(R.id.date4);
        date5 = findViewById(R.id.date5);
        date6 = findViewById(R.id.date6);
        date7 = findViewById(R.id.date7);

        Date now = new Date();
        dateList.add(date1);
        dateList.add(date2);
        dateList.add(date3);
        dateList.add(date4);
        dateList.add(date5);
        dateList.add(date6);
        dateList.add(date7);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        for (int i = 0; i < 7; i++) {
            Log.d("DATE", cal.toString());
            //int month = cal.get(Calendar.MONTH);
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");
            dateFormat.setCalendar(cal);
            SpannableString ssDay = new SpannableString(dateFormat.format(cal.getTime()));
            ssDay.setSpan(new RelativeSizeSpan(0.25f), 0,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int date = cal.get(Calendar.DAY_OF_MONTH);
            SpannableString ssDate = new SpannableString(Integer.toString(date));
            ssDate.setSpan(new RelativeSizeSpan(1f), 0,ssDate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView tv = dateList.get(i);
            tv.setTextColor(Color.parseColor("#cdcdcd"));
            tv.setText(ssDay + "\n" + ssDate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

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

        date1.setTextColor(Color.parseColor("#000000"));

        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOffset = 0;
                date1.setTextColor(Color.parseColor("#000000"));
                date2.setTextColor(Color.parseColor("#cdcdcd"));
                date3.setTextColor(Color.parseColor("#cdcdcd"));
                date4.setTextColor(Color.parseColor("#cdcdcd"));
                date5.setTextColor(Color.parseColor("#cdcdcd"));
                date6.setTextColor(Color.parseColor("#cdcdcd"));
                date7.setTextColor(Color.parseColor("#cdcdcd"));
                for (CafeteriaModel m : diningHall) {
                    if (m.indexOfCurrentDay() != -1) {
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
            }
        });

        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOffset = 1;
                date1.setTextColor(Color.parseColor("#cdcdcd"));
                date2.setTextColor(Color.parseColor("#000000"));
                date3.setTextColor(Color.parseColor("#cdcdcd"));
                date4.setTextColor(Color.parseColor("#cdcdcd"));
                date5.setTextColor(Color.parseColor("#cdcdcd"));
                date6.setTextColor(Color.parseColor("#cdcdcd"));
                date7.setTextColor(Color.parseColor("#cdcdcd"));
                for (CafeteriaModel m : diningHall) {
                    if (m.indexOfCurrentDay() != -1) {
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
            }
        });

        date3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOffset = 2;
                date1.setTextColor(Color.parseColor("#cdcdcd"));
                date2.setTextColor(Color.parseColor("#cdcdcd"));
                date3.setTextColor(Color.parseColor("#000000"));
                date4.setTextColor(Color.parseColor("#cdcdcd"));
                date5.setTextColor(Color.parseColor("#cdcdcd"));
                date6.setTextColor(Color.parseColor("#cdcdcd"));
                date7.setTextColor(Color.parseColor("#cdcdcd"));
                for (CafeteriaModel m : diningHall) {
                    if (m.indexOfCurrentDay() != -1) {
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
            }
        });

        date4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOffset = 3;
                date1.setTextColor(Color.parseColor("#cdcdcd"));
                date2.setTextColor(Color.parseColor("#cdcdcd"));
                date3.setTextColor(Color.parseColor("#cdcdcd"));
                date4.setTextColor(Color.parseColor("#000000"));
                date5.setTextColor(Color.parseColor("#cdcdcd"));
                date6.setTextColor(Color.parseColor("#cdcdcd"));
                date7.setTextColor(Color.parseColor("#cdcdcd"));
                for (CafeteriaModel m : diningHall) {
                    if (m.indexOfCurrentDay() != -1) {
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
            }
        });

        date5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOffset = 4;
                date1.setTextColor(Color.parseColor("#cdcdcd"));
                date2.setTextColor(Color.parseColor("#cdcdcd"));
                date3.setTextColor(Color.parseColor("#cdcdcd"));
                date4.setTextColor(Color.parseColor("#cdcdcd"));
                date5.setTextColor(Color.parseColor("#000000"));
                date6.setTextColor(Color.parseColor("#cdcdcd"));
                date7.setTextColor(Color.parseColor("#cdcdcd"));
                for (CafeteriaModel m : diningHall) {
                    if (m.indexOfCurrentDay() != -1) {
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
            }
        });

        date6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOffset = 5;
                date1.setTextColor(Color.parseColor("#cdcdcd"));
                date2.setTextColor(Color.parseColor("#cdcdcd"));
                date3.setTextColor(Color.parseColor("#cdcdcd"));
                date4.setTextColor(Color.parseColor("#cdcdcd"));
                date5.setTextColor(Color.parseColor("#cdcdcd"));
                date6.setTextColor(Color.parseColor("#000000"));
                date7.setTextColor(Color.parseColor("#cdcdcd"));
                for (CafeteriaModel m : diningHall) {
                    if (m.indexOfCurrentDay() != -1) {
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
            }
        });

        date7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateOffset = 6;
                date1.setTextColor(Color.parseColor("#cdcdcd"));
                date2.setTextColor(Color.parseColor("#cdcdcd"));
                date3.setTextColor(Color.parseColor("#cdcdcd"));
                date4.setTextColor(Color.parseColor("#cdcdcd"));
                date5.setTextColor(Color.parseColor("#cdcdcd"));
                date6.setTextColor(Color.parseColor("#cdcdcd"));
                date7.setTextColor(Color.parseColor("#000000"));
                for (CafeteriaModel m : diningHall) {
                    if (m.indexOfCurrentDay() != -1) {
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
            }
        });

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
                        toast = Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT);
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
