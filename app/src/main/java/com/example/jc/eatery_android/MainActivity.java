package com.example.jc.eatery_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;

import com.example.jc.eatery_android.Data.CafeteriaDbHelper;
import com.example.jc.eatery_android.ListAdapter.MainListAdapter;
import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.NetworkUtils.ConnectionUtilities;
import com.example.jc.eatery_android.NetworkUtils.JsonUtilities;
import com.example.jc.eatery_android.NetworkUtils.NetworkUtilities;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainListAdapter.ListAdapterOnClickHandler{

    public RecyclerView mRecyclerView;
    public ArrayList<CafeteriaModel> cafeList;
    public ArrayList<CafeteriaModel> currentList;
    public CafeteriaDbHelper dbHelper;
    public MainListAdapter listAdapter;
    public boolean northPressed = false;
    public boolean centralPressed = false;
    public boolean westPressed = false;
    public boolean swipesPressed = false;
    public boolean brbPressed = false;
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
            cafeList = JsonUtilities.parseJson(dbHelper.getLastRow());
            currentList = cafeList;

            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
            mRecyclerView.setLayoutManager(layoutManager);

            listAdapter = new MainListAdapter(getApplicationContext(), MainActivity.this,cafeList.size(), cafeList);
            mRecyclerView.setAdapter(listAdapter);
        }

        else {
            new ProcessJson().execute("");
        }



    }

    public void filterClick(View view){

        int id = view.getId();

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);

        switch(id){
            case R.id.northButton:
                //north button is pressed
                if(!northPressed) {
                    northButton.setTextColor(Color.BLACK);
                    northPressed = true;
                    centralPressed = false;
                    westPressed = false;
                    westButton.setTextColor(Color.parseColor("#CACCCC"));
                    centralButton.setTextColor(Color.parseColor("#CACCCC"));
                    ArrayList<CafeteriaModel> northList = new ArrayList<>();
                    for(CafeteriaModel model : cafeList){
                        if(model.getArea()== CafeteriaModel.CafeteriaArea.NORTH){
                            northList.add(model);
                        }
                    }
                    currentList = northList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();

                    break;
                }
                //north button is not pressed
                else{
                    northButton.setTextColor(Color.parseColor("#CACCCC"));
                    northPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }


            case R.id.centralButton:
                //central button is pressed
                if(!centralPressed){
                    centralButton.setTextColor(Color.BLACK);
                    centralPressed = true;
                    northPressed = false;
                    westPressed = false;
                    westButton.setTextColor(Color.parseColor("#CACCCC"));
                    northButton.setTextColor(Color.parseColor("#CACCCC"));
                    ArrayList<CafeteriaModel> centralList = new ArrayList<>();
                    for(CafeteriaModel model : cafeList){
                        if(model.getArea()== CafeteriaModel.CafeteriaArea.CENTRAL){
                            centralList.add(model);
                        }
                    }
                    currentList = centralList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;

                }
                //central button is not pressed
                else{
                    centralButton.setTextColor(Color.parseColor("#CACCCC"));
                    centralPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }

            case R.id.westButton:
                //west button is pressed
                if(!westPressed){
                    westButton.setTextColor(Color.BLACK);
                    westPressed = true;
                    centralPressed = false;
                    northPressed = false;
                    northButton.setTextColor(Color.parseColor("#CACCCC"));
                    centralButton.setTextColor(Color.parseColor("#CACCCC"));
                    ArrayList<CafeteriaModel> westList = new ArrayList<>();
                    for(CafeteriaModel model : cafeList){
                        if(model.getArea()== CafeteriaModel.CafeteriaArea.WEST){
                            westList.add(model);
                        }
                    }
                    currentList = westList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }
                //west button is not pressed
                else{
                    westButton.setTextColor(Color.parseColor("#CACCCC"));
                    westPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }

            case R.id.swipes:
                //swipe button is pressed
                if(!swipesPressed){
                    swipesButton.setTextColor(Color.BLACK);
                    swipesPressed = true;
                    brbPressed = false;
                    brbButton.setTextColor(Color.parseColor("#CACCCC"));
                    //if north is also pressed, north + swipe
                    ArrayList<CafeteriaModel> swipeList = new ArrayList<>();
                    if(northPressed){
                        for(CafeteriaModel model: cafeList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.NORTH && model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    else if(westPressed){
                        for(CafeteriaModel model: cafeList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.WEST && model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    else if(centralPressed){
                        for(CafeteriaModel model: cafeList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.CENTRAL && model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    else{
                        for(CafeteriaModel model: cafeList){
                            if(model.getPay_methods().contains("Meal Plan - Swipe")){
                                swipeList.add(model);
                            }
                        }
                    }
                    currentList = swipeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }
                else{
                    swipesButton.setTextColor(Color.parseColor("#CACCCC"));
                    swipesPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;

                }
            case R.id.brb:
                //brb pressed
                if(!brbPressed){
                    brbButton.setTextColor(Color.BLACK);
                    brbPressed = true;
                    swipesPressed = false;
                    swipesButton.setTextColor(Color.parseColor("#CACCCC"));

                    ArrayList<CafeteriaModel> brbList = new ArrayList<>();

                    if(northPressed){
                        for(CafeteriaModel model: cafeList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.NORTH && model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    else if(westPressed){
                        for(CafeteriaModel model: cafeList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.WEST && model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    else if(centralPressed){
                        for(CafeteriaModel model: cafeList){
                            if(model.getArea()==CafeteriaModel.CafeteriaArea.CENTRAL && model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    else{
                        for(CafeteriaModel model: cafeList){
                            if(model.getPay_methods().contains("Cornell Card")){
                                brbList.add(model);
                            }
                        }
                    }
                    currentList = brbList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }
                else{
                    brbButton.setTextColor(Color.parseColor("#CACCCC"));
                    brbPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;

                }
        }

    }

    @Override
    public void onClick(int position) {

        Intent intent = new Intent(this,MenuActivity.class);

        intent.putExtra("testData", currentList);
        intent.putExtra("cafeInfo", currentList.get(position));
        intent.putExtra("locName", currentList.get(position).getNickName());

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
                listAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
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
            cafeList = JsonUtilities.parseJson(json);
            currentList = cafeList;
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
        }
    }
}
