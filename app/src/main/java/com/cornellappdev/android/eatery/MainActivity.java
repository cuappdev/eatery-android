package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cornellappdev.android.eatery.Data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.Model.CafeteriaModel;
import com.cornellappdev.android.eatery.NetworkUtils.ConnectionUtilities;
import com.cornellappdev.android.eatery.NetworkUtils.JsonUtilities;
import com.cornellappdev.android.eatery.NetworkUtils.NetworkUtilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements MainListAdapter.ListAdapterOnClickHandler {

    public static boolean searchPressed = false;

    public ArrayList<CafeteriaModel> cafeList = new ArrayList<>(); //holds all cafes
    public ArrayList<CafeteriaModel> currentList = new ArrayList<>(); //button filter list
    public ArrayList<CafeteriaModel> searchList = new ArrayList<>(); // searchbar filter list
    public BottomNavigationView bnv;
    public Button northButton;
    public Button westButton;
    public Button centralButton;
    public Button swipesButton;
    public Button brbButton;
    public CafeteriaDbHelper dbHelper;
    public MainListAdapter listAdapter;
    public ProgressBar progressBar;
    public RecyclerView mRecyclerView;
    public RelativeLayout splash;
    public boolean northPressed = false;
    public boolean centralPressed = false;
    public boolean westPressed = false;
    public boolean swipesPressed = false;
    public boolean brbPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Eatery");
        setContentView(R.layout.activity_main);
        dbHelper = new CafeteriaDbHelper(this);
        mRecyclerView = findViewById(R.id.cafe_list);
        northButton = findViewById(R.id.northButton);
        westButton = findViewById(R.id.westButton);
        centralButton = findViewById(R.id.centralButton);
        swipesButton = findViewById(R.id.swipes);
        brbButton = findViewById(R.id.brb);
        progressBar = findViewById(R.id.progress_bar);
        bnv = findViewById(R.id.bottom_navigation);
        splash = findViewById(R.id.relative_layout_splash);
        bnv.setVisibility(View.GONE);
        getSupportActionBar().hide();

        ConnectionUtilities con = new ConnectionUtilities(this);
        if (!con.isNetworkAvailable()) {
            cafeList = new ArrayList<>();
            if (JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext()) != null) {
                cafeList = JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext());
            }
            Collections.sort(cafeList);
            currentList = cafeList;
            searchList = cafeList;

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);

            listAdapter =
                    new MainListAdapter(getApplicationContext(),
                            MainActivity.this,
                            cafeList.size(),
                            cafeList);
            mRecyclerView.setAdapter(listAdapter);
        } else {
            new ProcessJson().execute("");
        }

        // Add functionality to bottom nav bar
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        break;
                    case R.id.action_week:
                        intent = new Intent(getApplicationContext(), WeeklyMenuActivity.class);
                        intent.putExtra("cafeData", cafeList);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    case R.id.action_brb:
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_activity), "If you would like" +
                                        " to see this feature, consider joining our Android dev team!",
                                Snackbar.LENGTH_LONG);
                        snackbar.setAction("Apply", new SnackBarListener());
                        snackbar.show();

                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        bnv.setSelectedItemId(R.id.action_home);
    }

    // Change button and background color
    public void changeButtonColor(String textColor, String backgroundColor, Button button) {
        button.setTextColor(Color.parseColor(textColor));
        GradientDrawable bgShape = (GradientDrawable) button.getBackground();
        bgShape.setColor(Color.parseColor(backgroundColor));
    }

    // TODO(lesley): reduce redundancy in this method, please
    public void filterClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.northButton:
                if (!northPressed) {
                    // Change color of north button + set boolean(clicked)
                    changeButtonColor("#FFFFFF", "#4B7FBE", northButton);
                    northPressed = true;

                    // Change color of west+ central + set booleans(unclicked)
                    centralPressed = false;
                    westPressed = false;
                    changeButtonColor("#4B7FBE", "#F2F2F2", westButton);
                    changeButtonColor("#4B7FBE", "#F2F2F2", centralButton);

                    // Go through searchList(search view list)
                    ArrayList<CafeteriaModel> northList = new ArrayList<>();
                    for (CafeteriaModel model : searchList) {
                        if (model.getArea() == CafeteriaModel.CafeteriaArea.NORTH) {
                            northList.add(model);
                        }
                    }
                    // Set currentList to northList(filtered)
                    currentList = northList;
                    break;
                }
                // North button is not pressed or unclicked
                else {
                    changeButtonColor("#4B7FBE", "#F2F2F2", northButton);
                    northPressed = false;
                    currentList = searchList;
                    break;
                }
            case R.id.centralButton:
                if (!centralPressed) {
                    // Change color of central button + set boolean(clicked)
                    changeButtonColor("#FFFFFF", "#4B7FBE", centralButton);
                    centralPressed = true;

                    // Change color of north+ west + set booleans(unclicked)
                    northPressed = false;
                    westPressed = false;
                    changeButtonColor("#4B7FBE", "#F2F2F2", westButton);
                    changeButtonColor("#4B7FBE", "#F2F2F2", northButton);

                    ArrayList<CafeteriaModel> centralList = new ArrayList<>();
                    for (CafeteriaModel model : searchList) {
                        if (model.getArea() == CafeteriaModel.CafeteriaArea.CENTRAL) {
                            centralList.add(model);
                        }
                    }
                    currentList = centralList;
                    break;
                }
                // Central button is (not pressed) or unclicked
                else {
                    changeButtonColor("#4B7FBE", "#F2F2F2", centralButton);
                    centralPressed = false;
                    currentList = searchList;
                    break;
                }
            case R.id.westButton:
                if (!westPressed) {
                    // Change color of west button + set boolean(clicked)
                    changeButtonColor("#FFFFFF", "#4B7FBE", westButton);
                    westPressed = true;

                    // Change color of north and central button + set booleans(unclicked)
                    centralPressed = false;
                    northPressed = false;
                    changeButtonColor("#4B7FBE", "#F2F2F2", northButton);
                    changeButtonColor("#4B7FBE", "#F2F2F2", centralButton);

                    ArrayList<CafeteriaModel> westList = new ArrayList<>();
                    for (CafeteriaModel model : searchList) {
                        if (model.getArea() == CafeteriaModel.CafeteriaArea.WEST) {
                            westList.add(model);
                        }
                    }
                    currentList = westList;
                    break;
                }
                // West button is not pressed or unclicked
                else {
                    changeButtonColor("#4B7FBE", "#F2F2F2", westButton);
                    westPressed = false;
                    currentList = searchList;
                    break;
                }
            case R.id.swipes:
                // Swipe button is pressed
                if (!swipesPressed) {
                    // Set Swipe button color + boolean(clicked)
                    changeButtonColor("#FFFFFF", "#4B7FBE", swipesButton);
                    swipesPressed = true;
                    // Set brb button color + boolean(unclicked)
                    brbPressed = false;
                    changeButtonColor("#4B7FBE", "#F2F2F2", brbButton);

                    // If north is also pressed, north + swipe
                    ArrayList<CafeteriaModel> swipeList = new ArrayList<>();
                    if (northPressed) {
                        for (CafeteriaModel model : searchList) {
                            if (model.getArea() == CafeteriaModel.CafeteriaArea.NORTH && model.getPay_methods().contains("Meal Plan - Swipe")) {
                                swipeList.add(model);
                            }
                        }
                    }
                    // If west is also pressed , west+swipe
                    else if (westPressed) {
                        for (CafeteriaModel model : searchList) {
                            if (model.getArea() == CafeteriaModel.CafeteriaArea.WEST && model.getPay_methods().contains("Meal Plan - Swipe")) {
                                swipeList.add(model);
                            }
                        }
                    }
                    // If central is also pressed, central + swipe
                    else if (centralPressed) {
                        for (CafeteriaModel model : searchList) {
                            if (model.getArea() == CafeteriaModel.CafeteriaArea.CENTRAL && model.getPay_methods().contains("Meal Plan - Swipe")) {
                                swipeList.add(model);
                            }
                        }
                    }
                    // If no area button pressed, swipe
                    else {
                        for (CafeteriaModel model : searchList) {
                            if (model.getPay_methods().contains("Meal Plan - Swipe")) {
                                swipeList.add(model);
                            }
                        }
                    }
                    currentList = swipeList;
                    break;
                }
                // Swipe not pressed or unclicked
                else {
                    changeButtonColor("#4B7FBE", "#F2F2F2", swipesButton);
                    swipesPressed = false;
                    currentList = searchList;
                    break;

                }
            case R.id.brb:
                // Brb pressed
                if (!brbPressed) {
                    // Set brb button color + boolean(clicked)
                    changeButtonColor("#FFFFFF", "#4B7FBE", brbButton);
                    brbPressed = true;

                    // Set swipe button color + boolean(unclicked)
                    swipesPressed = false;
                    changeButtonColor("#4B7FBE", "#F2F2F2", swipesButton);

                    ArrayList<CafeteriaModel> brbList = new ArrayList<>();

                    // North + brb
                    if (northPressed) {
                        for (CafeteriaModel model : searchList) {
                            if (model.getArea() == CafeteriaModel.CafeteriaArea.NORTH && model.getPay_methods().contains("Cornell Card")) {
                                brbList.add(model);
                            }
                        }
                    }
                    // West + brb
                    else if (westPressed) {
                        for (CafeteriaModel model : searchList) {
                            if (model.getArea() == CafeteriaModel.CafeteriaArea.WEST && model.getPay_methods().contains("Cornell Card")) {
                                brbList.add(model);
                            }
                        }
                    }
                    // Central + brb
                    else if (centralPressed) {
                        for (CafeteriaModel model : searchList) {
                            if (model.getArea() == CafeteriaModel.CafeteriaArea.CENTRAL && model.getPay_methods().contains("Cornell Card")) {
                                brbList.add(model);
                            }
                        }
                    }
                    // Brb
                    else {
                        for (CafeteriaModel model : searchList) {
                            if (model.getPay_methods().contains("Cornell Card")) {
                                brbList.add(model);
                            }
                        }
                    }
                    currentList = brbList;
                    break;
                } else {
                    // Brb unclicked
                    changeButtonColor("#4B7FBE", "#F2F2F2", brbButton);
                    brbPressed = false;
                    currentList = searchList;
                    break;
                }
        }
        Collections.sort(currentList);
        listAdapter.setList(currentList, currentList.size(), null);
    }

    @Override
    public void onClick(int position, ArrayList<CafeteriaModel> list) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("testData", list);
        intent.putExtra("cafeInfo", list.get(position));
        intent.putExtra("locName", list.get(position).getNickName());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("cafeData", cafeList);
                startActivity(intent);
                return true;
            default:
                // The user's action was not recognized, and invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        setTitle("Eateries");
        AutoCompleteTextView searchTextView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        int max = searchView.getMaxWidth();
        searchView.setMaxWidth(2000);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            // Don't do anything
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // No query given, set searchList to cafeList
                if (query.length() == 0) {
                    searchList = cafeList;
                    searchPressed = false;
                }
                // Query given
                else {
                    query = query.trim();
                    ArrayList<CafeteriaModel> filteredList = new ArrayList<>();
                    searchPressed = true;
                    // If none of the buttons clicked, loop through cafeList
                    if (!northPressed && !centralPressed && !westPressed && !swipesPressed && !brbPressed) {
                        for (CafeteriaModel model : cafeList) {
                            HashSet<String> mealSet = model.getMealItems();

                            //check the nickname of the cafe and if it's not already in the filtered list add it to the list
                            if (model.getNickName().toLowerCase().contains((query.toLowerCase())) && !filteredList.contains(model)) {
                                filteredList.add(model);
                            }

                            for (String item : mealSet) {
                                if (item.toLowerCase().contains(query.toLowerCase())) {
                                    if (!filteredList.contains(model))
                                        filteredList.add(model);
                                }
                            }

                        }
                        searchList = filteredList;
                    }
                    // If any of the buttons clicked, loop through currentList
                    else {
                        for (CafeteriaModel model : currentList) {
                            HashSet<String> mealSet = model.getMealItems();

                            //check the nickname of the cafe and if it's not already in the filtered list add it to the list
                            if (model.getNickName().toLowerCase().contains((query.toLowerCase())) && !filteredList.contains(model)) {
                                filteredList.add(model);
                            }

                            for (String item : mealSet) {
                                if (item.toLowerCase().contains(query.toLowerCase())) {
                                    if (!filteredList.contains(model))
                                        filteredList.add(model);
                                }
                            }
                        }
                        searchList = filteredList;
                    }
                }
                Collections.sort(searchList);
                listAdapter.setList(searchList, searchList.size(), query.length() == 0 ? null : query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // No text given
                if (newText.length() == 0) {
                    searchList = cafeList;
                    searchPressed = false;
                }
                // Some text given
                else {
                    newText = newText.trim();
                    ArrayList<CafeteriaModel> filteredList = new ArrayList<>();
                    searchPressed = true;
                    // If no buttons clicked, loop through cafelist
                    if (!northPressed && !centralPressed && !westPressed && !swipesPressed && !brbPressed) {
                        for (CafeteriaModel model : cafeList) {

                            HashSet<String> mealSet = model.getMealItems();
                            ArrayList<String> matchedItems = new ArrayList<>();
                            ArrayList<String> full_items = new ArrayList<>();

                            boolean found_item = false;
                            for (String item : mealSet) {
                                if (item.toLowerCase().contains(newText.toLowerCase())) {
                                    matchedItems.add(item);
                                    found_item = true;
                                }
                                full_items.add(item);

                            }
                            if (found_item) {
                                model.setSearchedItems(matchedItems);
                                if (!filteredList.contains(model))
                                    filteredList.add(model);
                            }

                            //check the nickname of the cafe and if it's not already in the filtered list add it to the list
                            if (model.getNickName().toLowerCase().contains((newText.toLowerCase())) && !filteredList.contains(model) && model.isOpen().equals("Open")) {
                                model.setSearchedItems(full_items);
                                filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }
                    // If any button clicked, loop through currentList
                    else {
                        for (CafeteriaModel model : currentList) {
                            HashSet<String> mealSet = model.getMealItems();
                            ArrayList<String> matchedItems = new ArrayList<>();
                            boolean found_item = false;
                            for (String item : mealSet) {
                                if (item.toLowerCase().contains(newText.toLowerCase())) {
                                    matchedItems.add(item);
                                    found_item = true;
                                }
                            }
                            if (found_item) {
                                model.setSearchedItems(matchedItems);
                                if (!filteredList.contains(model))
                                    filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }
                }
                Collections.sort(searchList);
                listAdapter.setList(searchList, searchList.size(), newText.length() == 0 ? null : newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public class SnackBarListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent browser = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.cornellappdev.com/apply/"));
            startActivity(browser);
        }
    }

    public class ProcessJson extends AsyncTask<String, Void, ArrayList<CafeteriaModel>>{

        @Override
        protected ArrayList<CafeteriaModel> doInBackground(String... params) {
            String json = NetworkUtilities.getJson();
            dbHelper.addData(json);

            cafeList = JsonUtilities.parseJson(json, getApplicationContext());
            currentList = cafeList;
            searchList = cafeList;
            Collections.sort(cafeList);
            return cafeList;
        }

        @Override
        protected void onPostExecute(ArrayList<CafeteriaModel> result) {
            super.onPostExecute(result);

            splash.setVisibility(View.GONE);
            bnv.setVisibility(View.VISIBLE);
            getSupportActionBar().show();

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);

            listAdapter = new MainListAdapter(getApplicationContext(), MainActivity.this, result.size(), cafeList);
            mRecyclerView.setAdapter(listAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);


        }
    }
}
