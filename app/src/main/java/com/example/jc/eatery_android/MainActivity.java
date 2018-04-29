package com.example.jc.eatery_android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.jc.eatery_android.Data.CafeteriaDbHelper;
import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.NetworkUtils.ConnectionUtilities;
import com.example.jc.eatery_android.NetworkUtils.JsonUtilities;
import com.example.jc.eatery_android.NetworkUtils.NetworkUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements MainListAdapter.ListAdapterOnClickHandler{

    public RecyclerView mRecyclerView;
    public ArrayList<CafeteriaModel> cafeList; //holds all cafes
    public ArrayList<CafeteriaModel> currentList; //button filter list
    public ArrayList<CafeteriaModel> searchList; // searchbar filter list
    public CafeteriaDbHelper dbHelper;
    public MainListAdapter listAdapter;
    public boolean northPressed = false;
    public boolean centralPressed = false;
    public boolean westPressed = false;
    public boolean swipesPressed = false;
    public boolean brbPressed = false;
    public static boolean searchPressed = false;
    public Button northButton;
    public Button westButton;
    public Button centralButton;
    public Button swipesButton;
    public Button brbButton;
    public ProgressBar progressBar;
    public BottomNavigationView bnv;


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


        ConnectionUtilities con = new ConnectionUtilities(this);
        if(!con.isNetworkAvailable()){
            cafeList = JsonUtilities.parseJson(dbHelper.getLastRow(),getApplicationContext());
            currentList = cafeList;
            searchList = cafeList;
            Collections.sort(currentList);
            Collections.sort(searchList);

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
            mRecyclerView.setLayoutManager(layoutManager);
            cafeList = cafeList;
            Collections.sort(cafeList);
            listAdapter = new MainListAdapter(getApplicationContext(), MainActivity.this,cafeList.size(), cafeList);
            mRecyclerView.setAdapter(listAdapter);
        }
        else {
            new ProcessJson().execute("");
        }

        //adds functionality to bottom nav bar
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()) {
                    case R.id.action_home:

                        break;
                    case R.id.action_week:
                        intent = new Intent(getApplicationContext(), WeeklyMenuActivity.class);
                        intent.putExtra("cafeData", cafeList);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
    }

    //change button's color, background color
    public void changeButtonColor(String textColor, String backgroundColor, Button button){
        button.setTextColor(Color.parseColor(textColor));
        GradientDrawable bgShape = (GradientDrawable) button.getBackground();
        bgShape.setColor(Color.parseColor(backgroundColor));
    }


    public void filterClick(View view){

        int id = view.getId();

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);

        switch(id){
            case R.id.northButton:
                if(!northPressed) {
                    //change color of north button + set boolean(clicked)
                    changeButtonColor("#6FB2E0","#E7ECF0",northButton);
                    northPressed = true;
                    //change color of west+ central + set booleans(unclicked)
                    centralPressed = false;
                    westPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",westButton);
                    changeButtonColor("#FFFFFF","#6FB2E0",centralButton);

                    ArrayList<CafeteriaModel> northList = new ArrayList<>();
                    //go through searchList(search view list)
                    for(CafeteriaModel model : searchList){
                        if(model.getArea()== CafeteriaModel.CafeteriaArea.NORTH){
                            northList.add(model);
                        }
                    }
                    //set currentList to northList(filtered)
                    currentList = northList;
                    break;
                }
                //north button is not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",northButton);
                    northPressed = false;
                    currentList = searchList;
                    break;
                }

            case R.id.centralButton:
                if(!centralPressed){
                    //change color of central button + set boolean(clicked)
                    changeButtonColor("#6FB2E0","#E7ECF0",centralButton);

                    centralPressed = true;

                    //change color of north+ west + set booleans(unclicked)
                    northPressed = false;
                    westPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",westButton);
                    changeButtonColor("#FFFFFF","#6FB2E0",northButton);

                    ArrayList<CafeteriaModel> centralList = new ArrayList<>();
                    for(CafeteriaModel model : searchList){
                        if(model.getArea()== CafeteriaModel.CafeteriaArea.CENTRAL){
                            centralList.add(model);
                        }
                    }
                    currentList = centralList;
                    break;

                }
                //central button is not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",centralButton);

                    centralPressed = false;
                    currentList = searchList;
                    break;
                }

            case R.id.westButton:
                if(!westPressed){
                    //change color of west button + set boolean(clicked)
                    changeButtonColor("#6FB2E0","#E7ECF0",westButton);

                    westPressed = true;

                    //change color of north and central button + set booleans(unclicked)
                    centralPressed = false;
                    northPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",northButton);
                    changeButtonColor("#FFFFFF","#6FB2E0",centralButton);

                    ArrayList<CafeteriaModel> westList = new ArrayList<>();
                    for(CafeteriaModel model : searchList){
                        if(model.getArea()== CafeteriaModel.CafeteriaArea.WEST){
                            westList.add(model);
                        }
                    }
                    currentList = westList;
                    break;
                }
                //west button is not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",westButton);

                    westPressed = false;
                    currentList = searchList;
                    break;
                }

            case R.id.swipes:
                //swipe button is pressed
                if(!swipesPressed){
                    //set Swipe button color + boolean(clicked)
                    changeButtonColor("#6FB2E0","#E7ECF0",swipesButton);
                    swipesPressed = true;

                    //set brb button color + boolean(unclicked)
                    brbPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",brbButton);

                    //if north is also pressed, north + swipe
                    ArrayList<CafeteriaModel> swipeList = new ArrayList<>();
                    if(northPressed){
                        for(CafeteriaModel model: searchList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.NORTH && model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    //if west is also pressed , west+swipe
                    else if(westPressed){
                        for(CafeteriaModel model: searchList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.WEST && model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    //if central is also pressed, central + swipe
                    else if(centralPressed){
                        for(CafeteriaModel model: searchList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.CENTRAL && model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    //if no area button pressed, swipe
                    else{
                        for(CafeteriaModel model: searchList){
                            if(model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    currentList = swipeList;
                    break;
                }
                //swipe not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",swipesButton);
                    swipesPressed = false;
                    currentList = searchList;
                    break;

                }
            case R.id.brb:
                //brb pressed
                if(!brbPressed){
                    //set brb button color + boolean(clicked)
                    changeButtonColor("#6FB2E0","#E7ECF0",brbButton);
                    brbPressed = true;

                    //set swipe button color + boolean(unclicked)
                    swipesPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",swipesButton);

                    ArrayList<CafeteriaModel> brbList = new ArrayList<>();

                    //north + brb
                    if(northPressed){
                        for(CafeteriaModel model: searchList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.NORTH && model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    //west + brb
                    else if(westPressed){
                        for(CafeteriaModel model: searchList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.WEST && model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    //central + brb
                    else if(centralPressed){
                        for(CafeteriaModel model: searchList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.CENTRAL && model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    //brb
                    else{
                        for(CafeteriaModel model: searchList){
                            if(model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    currentList = brbList;
                    break;
                }
                else{
                    //brb unclicked
                    changeButtonColor("#FFFFFF","#6FB2E0",brbButton);
                    brbPressed = false;
                    currentList = searchList;
                    break;
                }
        }
        Collections.sort(currentList);
        listAdapter.setList(currentList,currentList.size());


    }

    @Override
    public void onClick(int position,ArrayList<CafeteriaModel> list) {


        Intent intent = new Intent(this,MenuActivity.class);

        intent.putExtra("testData", list);
        intent.putExtra("cafeInfo", list.get(position));
        intent.putExtra("locName", list.get(position).getNickName());

        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //no query given, set searchList to cafeList
                if(query.length()==0){
                    searchList = cafeList;
                    searchPressed = false;
                }
                //query given
                else {
                    ArrayList<CafeteriaModel> filteredList = new ArrayList<>();
                    searchPressed = true;
                    //if none of the buttons clicked, loop through cafeList
                    if(!northPressed&&!centralPressed&&!westPressed&&!swipesPressed&&!brbPressed){
                        for (CafeteriaModel model : cafeList) {
                            HashSet<String> mealSet = model.getMealItems();

                            for(String item : mealSet){
                                if(item.toLowerCase().contains(query.toLowerCase())){
                                    if(!filteredList.contains(model))
                                        filteredList.add(model);
                                }
                            }

                        }
                        searchList = filteredList;
                    }
                    //if any of the buttons clicked, loop through currentList
                    else {
                        for (CafeteriaModel model : currentList) {
                            HashSet<String> mealSet = model.getMealItems();

                            for(String item : mealSet){
                                if(item.toLowerCase().contains(query.toLowerCase())){
                                    if(!filteredList.contains(model))
                                        filteredList.add(model);
                                }
                            }
                        }
                        searchList = filteredList;
                    }

                }
                Collections.sort(searchList);
                listAdapter.setList(searchList, searchList.size());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //no text given
                if (newText.length()==0) {
                    searchList = cafeList;
                    searchPressed = false;
                }
                //some text given
                else {
                    ArrayList<CafeteriaModel> filteredList = new ArrayList<>();
                    searchPressed = true;
                    //if no buttons clicked, loop through cafelist
                    if(!northPressed&&!centralPressed&&!westPressed&&!swipesPressed&&!brbPressed){
                        for (CafeteriaModel model : cafeList) {
                            HashSet<String> mealSet = model.getMealItems();
                            ArrayList<String> matchedItems= new ArrayList<String>();
                            boolean found_item = false;
                            for(String item : mealSet){
                                if(item.toLowerCase().contains(newText.toLowerCase())){
                                    matchedItems.add(item);
                                    found_item = true;

                                }
                            }
                            if(found_item){
                                model.setSearchedItems(matchedItems);
                                if(!filteredList.contains(model))
                                    filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }
                    //if any button clicked, loop through currentList
                    else {
                        for (CafeteriaModel model : currentList) {
                            HashSet<String> mealSet = model.getMealItems();
                            ArrayList<String> matchedItems= new ArrayList<String>();
                            boolean found_item = false;
                            for(String item : mealSet){
                                if(item.toLowerCase().contains(newText.toLowerCase())){
                                    matchedItems.add(item);
                                    found_item = true;

                                }
                            }
                            if(found_item){
                                model.setSearchedItems(matchedItems);
                                if(!filteredList.contains(model))
                                    filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }

                }
                Collections.sort(searchList);
                listAdapter.setList(searchList, searchList.size());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
            mRecyclerView.setLayoutManager(layoutManager);

            listAdapter = new MainListAdapter(getApplicationContext(), MainActivity.this,result.size(), cafeList);
            mRecyclerView.setAdapter(listAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        }
    }
}
