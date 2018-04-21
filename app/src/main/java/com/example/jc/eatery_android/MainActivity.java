package com.example.jc.eatery_android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jc.eatery_android.Data.CafeteriaDbHelper;
import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.NetworkUtils.ConnectionUtilities;
import com.example.jc.eatery_android.NetworkUtils.JsonUtilities;
import com.example.jc.eatery_android.NetworkUtils.NetworkUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new CafeteriaDbHelper(this);
        mRecyclerView = findViewById(R.id.cafe_list);
        northButton = findViewById(R.id.northButton);
        westButton = findViewById(R.id.westButton);
        centralButton = findViewById(R.id.centralButton);
        swipesButton = findViewById(R.id.swipes);
        brbButton = findViewById(R.id.brb);

        ConnectionUtilities con = new ConnectionUtilities(this);
        if(!con.isNetworkAvailable()){
            cafeList = JsonUtilities.parseJson(dbHelper.getLastRow(),getApplicationContext());
            currentList = cafeList;
            searchList = cafeList;

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
            mRecyclerView.setLayoutManager(layoutManager);
            cafeList = openCloseSort(cafeList);
            listAdapter = new MainListAdapter(getApplicationContext(), MainActivity.this,cafeList.size(), cafeList);
            mRecyclerView.setAdapter(listAdapter);
        }
        else {
            new ProcessJson().execute("");
        }
    }

    //change button's color, background color
    public void changeButtonColor(String textColor, String backgroundColor, Button button){
        button.setTextColor(Color.parseColor(textColor));
        GradientDrawable bgShape = (GradientDrawable) button.getBackground();
        bgShape.setColor(Color.parseColor(backgroundColor));
    }

    public ArrayList<CafeteriaModel> openCloseSort(ArrayList<CafeteriaModel> cafeList){
        Collections.sort(cafeList, new Comparator<CafeteriaModel>() {
            public int compare(CafeteriaModel v1, CafeteriaModel v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });

        ArrayList<CafeteriaModel> res = new ArrayList<>();
        for(CafeteriaModel model : cafeList){
            if(model.isOpen().equals("Open")){
                res.add(model);
            }
        }
        for(CafeteriaModel model : cafeList){
            if(model.isOpen().equals("Closed")){
                res.add(model);
            }
        }
        return res;
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
                    currentList = openCloseSort(northList);
                    break;
                }
                //north button is not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",northButton);
                    northPressed = false;
                    currentList = openCloseSort(searchList);
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
                    currentList = openCloseSort(centralList);
                    break;

                }
                //central button is not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",centralButton);

                    centralPressed = false;
                    currentList = openCloseSort(searchList);
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
                    currentList = openCloseSort(westList);
                    break;
                }
                //west button is not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",westButton);

                    westPressed = false;
                    currentList = openCloseSort(searchList);
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
                    currentList = openCloseSort(swipeList);
                    break;
                }
                //swipe not pressed or unclicked
                else{
                    changeButtonColor("#FFFFFF","#6FB2E0",swipesButton);
                    swipesPressed = false;
                    currentList = openCloseSort(searchList);
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
        currentList = openCloseSort(currentList);
        for(CafeteriaModel g : currentList){
            Log.i("testie",g.getName());
        }
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

                            if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                                filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }
                    //if any of the buttons clicked, loop through currentList
                    else {
                        for (CafeteriaModel model : currentList) {

                            if (model.getName().toLowerCase().contains(query.toLowerCase())) {
                                filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }

                }
                listAdapter.setList(openCloseSort(searchList), searchList.size());
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

                            if (model.getName().toLowerCase().contains(newText.toLowerCase())) {
                                filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }
                    //if any button clicked, loop through currentList
                    else {
                        for (CafeteriaModel model : currentList) {

                            if (model.getName().toLowerCase().contains(newText.toLowerCase())) {
                                filteredList.add(model);
                            }
                        }
                        searchList = filteredList;
                    }

                }

                listAdapter.setList(openCloseSort(searchList), searchList.size());
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

            return openCloseSort(cafeList);
        }

        @Override
        protected void onPostExecute(ArrayList<CafeteriaModel> result) {
            super.onPostExecute(result);

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
            mRecyclerView.setLayoutManager(layoutManager);

            listAdapter = new MainListAdapter(getApplicationContext(), MainActivity.this,result.size(), cafeList);
            mRecyclerView.setAdapter(listAdapter);
        }
    }
}
