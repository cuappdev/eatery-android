package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloClient;
import com.cornellappdev.android.eatery.data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;

import static com.cornellappdev.android.eatery.model.enums.CampusArea.CENTRAL;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.NORTH;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.WEST;

public class MainActivity extends AppCompatActivity
        implements MainListAdapter.ListAdapterOnClickHandler {

    public static final String PAYMENT_SWIPE = "Meal Plan - Swipe";
    public static final String PAYMENT_CARD = "Cornell Card";

    public static boolean searchPressed = false;

    public ArrayList<EateryBaseModel> cafeList = new ArrayList<>(); // holds all cafes
    public ArrayList<EateryBaseModel> currentList = new ArrayList<>(); // button filter list
    public ArrayList<EateryBaseModel> searchList = new ArrayList<>(); // searchbar filter list
    public BottomNavigationView bnv;
    public Button northButton;
    public Button westButton;
    public Button centralButton;
    public Button swipesButton;
    public Button brbButton;
    public Set<Button> areaButtonsPressed;
    public Set<Button> paymentButtonsPressed;
    public CafeteriaDbHelper dbHelper;
    public MainListAdapter listAdapter;
    public ProgressBar progressBar;
    final QueryListener queryListener = new QueryListener();
    public RecyclerView mRecyclerView;
    public RelativeLayout splash;

    public static ApolloClient apolloClient;
    public static boolean JSON_FALLBACK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Eatery");
        setContentView(R.layout.activity_main);

        // Preload Eateries
        NetworkUtilities.getEateries();

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
        areaButtonsPressed = new HashSet<>();
        paymentButtonsPressed = new HashSet<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        new ProcessJson().execute("");

        // Add functionality to bottom nav bar
        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
                                sv.smoothScrollTo(0, 0);
                                break;
                            case R.id.action_week:
                                intent = new Intent(getApplicationContext(), WeeklyMenuActivity.class);
                                intent.putExtra("cafeData", cafeList);
                                startActivity(intent);
                                break;
                            case R.id.action_brb:
                                intent = new Intent(getApplicationContext(), InfoActivity.class);
                                intent.putExtra("cafeData", cafeList);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        bnv.setSelectedItemId(R.id.action_home);
    }

    public void changeButtonColor(int textColor, int backgroundColor, Button button) {
        button.setTextColor(ContextCompat.getColor(getApplicationContext(), textColor));
        GradientDrawable bgShape = (GradientDrawable) button.getBackground();
        bgShape.setColor(ContextCompat.getColor(getApplicationContext(), backgroundColor));
    }

    private boolean hasPaymentMethod(EateryBaseModel model) {
        for (PaymentMethod method : getCurrentPaymentTypes()) {
            if (model.hasPaymentMethod(method)) {
                return true;
            }
        }
        return false;
    }

    private void filterCurrentList() {
        for (EateryBaseModel model : currentList) {
            final boolean areaFuzzyMatches =
                    getCurrentAreas().isEmpty() || getCurrentAreas().contains(model.getArea());
            final boolean paymentFuzzyMatches =
                    getCurrentPaymentTypes().isEmpty() || hasPaymentMethod(model);
            if (areaFuzzyMatches && paymentFuzzyMatches) {
                model.setMatchesFilter(true);
            } else {
                model.setMatchesFilter(false);
            }
        }
    }

    private HashSet<CampusArea> getCurrentAreas() {
        HashSet<CampusArea> areaSet = new HashSet<>();
        for (Button button : areaButtonsPressed) {
            if (northButton.equals(button)) {
                areaSet.add(NORTH);
            } else if (westButton.equals(button)) {
                areaSet.add(WEST);
            } else if (centralButton.equals(button)) {
                areaSet.add(CENTRAL);
            }
        }
        return areaSet;
    }

    private HashSet<PaymentMethod> getCurrentPaymentTypes() {
        HashSet<PaymentMethod> paymentSet = new HashSet<>();
        for (Button button : paymentButtonsPressed) {
            if (brbButton.equals(button)) {
                paymentSet.add(PaymentMethod.BRB);
            } else if (swipesButton.equals(button)) {
                paymentSet.add(PaymentMethod.SWIPES);
            }
        }
        return paymentSet;
    }

    private void handleAreaButtonPress(Button button, CampusArea area) {
        if (areaButtonsPressed.contains(button)) {
            changeButtonColor(R.color.blue, R.color.wash, button);
            areaButtonsPressed.remove(button);
        } else {
            changeButtonColor(R.color.white, R.color.blue, button);
            areaButtonsPressed.add(button);
        }
        filterCurrentList();
    }

    private void handlePaymentButtonPress(Button button, String payment) {
        if (paymentButtonsPressed.contains(button)) {
            changeButtonColor(R.color.blue, R.color.wash, button);
            paymentButtonsPressed.remove(button);
        } else {
            changeButtonColor(R.color.white, R.color.blue, button);
            paymentButtonsPressed.add(button);
        }
        filterCurrentList();
    }

    public void filterClick(View view) {
        switch (view.getId()) {
            case R.id.northButton:
                handleAreaButtonPress(northButton, NORTH);
                break;
            case R.id.centralButton:
                handleAreaButtonPress(centralButton, CENTRAL);
                break;
            case R.id.westButton:
                handleAreaButtonPress(westButton, WEST);
                break;
            case R.id.swipes:
                handlePaymentButtonPress(swipesButton, PAYMENT_SWIPE);
                break;
            case R.id.brb:
                handlePaymentButtonPress(brbButton, PAYMENT_CARD);
                break;
        }
        ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
        for (EateryBaseModel em : currentList) {
            if (em.matchesFilter()) {
                cafesToDisplay.add(em);
            }
        }
        searchList = cafesToDisplay;
        if (searchPressed) {
            queryListener.onQueryTextChange(queryListener.query);
        } else {
            Collections.sort(cafesToDisplay);
            listAdapter.setList(cafesToDisplay, cafesToDisplay.size(), null);
        }
    }

    @Override
    public void onClick(int position, ArrayList<EateryBaseModel> list) {
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
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        setTitle("Eateries");
        AutoCompleteTextView searchTextView =
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchView.setMaxWidth(2000);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(
                    searchTextView,
                    R.drawable
                            .cursor); // This sets the cursor resource ID to 0 or @null which will make it visible
            // on white background
        } catch (Exception e) {
            // Don't do anything
        }

        searchView.setOnQueryTextListener(queryListener);
        return super.onCreateOptionsMenu(menu);
    }

    public class QueryListener implements SearchView.OnQueryTextListener {

        public String query = "";

        private void searchList(String query) {
            final String lowercaseQuery = query.toLowerCase();
            for (EateryBaseModel model : searchList) {
                final HashSet<String> mealSet = model.getMealItems();

                boolean foundNickName = false;
                if (model.getNickName().toLowerCase().contains(lowercaseQuery)) {
                    foundNickName = true;
                }

                ArrayList<String> matchedItems = new ArrayList<>();
                boolean foundItem = false;
                for (String item : mealSet) {
                    if (item.toLowerCase().contains(lowercaseQuery)) {
                        foundItem = true;
                        matchedItems.add(item);
                    }
                }

                if (model.matchesFilter() && (foundItem || foundNickName)) {
                    if (foundNickName) {
                        model.setSearchedItems(new ArrayList<>(mealSet));
                    } else {
                        model.setSearchedItems(matchedItems);
                    }
                    model.setMatchesSearch(true);
                } else {
                    model.setMatchesSearch(false);
                }
            }
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            this.query = query;
            ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
            if (query.length() == 0) {
                searchList = currentList;
                searchPressed = false;
                for (EateryBaseModel cm : searchList) {
                    if (cm.matchesFilter()) {
                        cafesToDisplay.add(cm);
                    }
                }
            } else {
                searchPressed = true;
                searchList(query);
                for (EateryBaseModel cm : searchList) {
                    if (cm.matchesFilter() && cm.matchesSearch()) {
                        cafesToDisplay.add(cm);
                    }
                }
            }
            Collections.sort(cafesToDisplay);
            listAdapter.setList(
                    cafesToDisplay, cafesToDisplay.size(), query.length() == 0 ? null : query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return onQueryTextSubmit(newText);
        }
    }

    public class ProcessJson extends AsyncTask<String, Void, ArrayList<EateryBaseModel>> {

        @Override
        protected ArrayList<EateryBaseModel> doInBackground(String... params) {
            cafeList = new ArrayList<>();
            ConnectivityManager cm =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (!isConnected) {
                if (JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext()) != null) {
                    cafeList = JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext());
                }
                currentList = cafeList;
                searchList = cafeList;
                Collections.sort(cafeList);

            } else {
                if (JSON_FALLBACK) {
                    String json = NetworkUtilities.getJSON();
                    dbHelper.addData(json);
                    cafeList = JsonUtilities.parseJson(json, getApplicationContext());
                } else {
                    while (!NetworkUtilities.EATERIES_LOADED) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    List<AllEateriesQuery.Eatery> eateries = NetworkUtilities.getEateries();
                    cafeList = JsonUtilities.parseEateries(eateries, getApplicationContext());
                }
                currentList = cafeList;
                searchList = cafeList;
                Collections.sort(cafeList);
            }
            return cafeList;
        }

        @Override
        protected void onPostExecute(ArrayList<EateryBaseModel> result) {
            super.onPostExecute(result);
            splash.setVisibility(View.GONE);
            bnv.setVisibility(View.VISIBLE);
            getSupportActionBar().show();

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(MainActivity.this, LinearLayout.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);

            listAdapter =
                    new MainListAdapter(MainActivity.this, MainActivity.this, result.size(), cafeList);
            mRecyclerView.setAdapter(listAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }
    }
}
