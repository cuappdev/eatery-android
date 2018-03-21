package com.example.jc.eatery_android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
                //north button is pressed
                if(!northPressed) {
                    changeButtonColor("#6FB2E0","#E7ECF0",northButton);
                    northPressed = true;
                    centralPressed = false;
                    westPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",westButton);
                    changeButtonColor("#FFFFFF","#6FB2E0",centralButton);
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
                    changeButtonColor("#FFFFFF","#6FB2E0",northButton);
                    northPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }


            case R.id.centralButton:
                //central button is pressed
                if(!centralPressed){
                    changeButtonColor("#6FB2E0","#E7ECF0",centralButton);
                    centralPressed = true;
                    northPressed = false;
                    westPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",westButton);
                    changeButtonColor("#FFFFFF","#6FB2E0",northButton);
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
                    changeButtonColor("#FFFFFF","#6FB2E0",centralButton);
                    centralPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }

            case R.id.westButton:
                //west button is pressed
                if(!westPressed){
                    changeButtonColor("#6FB2E0","#E7ECF0",westButton);
                    westPressed = true;
                    centralPressed = false;
                    northPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",northButton);
                    changeButtonColor("#FFFFFF","#6FB2E0",centralButton);
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
                    changeButtonColor("#FFFFFF","#6FB2E0",westButton);
                    westPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }

            case R.id.swipes:
                //swipe button is pressed
                if(!swipesPressed){
                    changeButtonColor("#6FB2E0","#E7ECF0",swipesButton);
                    swipesPressed = true;
                    brbPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",brbButton);
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
                    changeButtonColor("#FFFFFF","#6FB2E0",swipesButton);
                    swipesPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;

                }
            case R.id.brb:
                //brb pressed
                if(!brbPressed){
                    changeButtonColor("#6FB2E0","#E7ECF0",brbButton);
                    brbPressed = true;
                    swipesPressed = false;
                    changeButtonColor("#FFFFFF","#6FB2E0",swipesButton);

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
                    changeButtonColor("#FFFFFF","#6FB2E0",brbButton);
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
