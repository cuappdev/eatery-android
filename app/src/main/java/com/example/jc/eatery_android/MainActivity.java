package com.example.jc.eatery_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jc.eatery_android.Data.CafeteriaDbHelper;
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
    public Button northButton;
    public Button westButton;
    public Button centralButton;

    //TODO: saves version of cafeList for when there's no wifi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new CafeteriaDbHelper(this);
        mRecyclerView = findViewById(R.id.cafe_list);
        northButton = findViewById(R.id.northButton);
        westButton = findViewById(R.id.westButton);
        centralButton = findViewById(R.id.centralButton);


        ConnectionUtilities con = new ConnectionUtilities(this);
        if(!con.isNetworkAvailable()){
            cafeList = JsonUtilities.parseJson(dbHelper.getLastRow(),getApplicationContext());
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
                if(!northPressed) {
                    northButton.setTextColor(Color.RED);
                    northPressed = true;
                    centralPressed = false;
                    westPressed = false;
                    westButton.setTextColor(Color.parseColor("#26C5FF"));
                    centralButton.setTextColor(Color.parseColor("#26C5FF"));
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
                else{
                    northButton.setTextColor(Color.parseColor("#26C5FF"));
                    northPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }

            case R.id.centralButton:
                if(!centralPressed){
                    centralButton.setTextColor(Color.RED);
                    centralPressed = true;
                    northPressed = false;
                    westPressed = false;
                    westButton.setTextColor(Color.parseColor("#26C5FF"));
                    northButton.setTextColor(Color.parseColor("#26C5FF"));
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

                }else{
                    centralButton.setTextColor(Color.parseColor("#26C5FF"));
                    centralPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                    break;
                }

            case R.id.westButton:
                if(!westPressed){
                    westButton.setTextColor(Color.RED);
                    westPressed = true;
                    centralPressed = false;
                    northPressed = false;
                    northButton.setTextColor(Color.parseColor("#26C5FF"));
                    centralButton.setTextColor(Color.parseColor("#26C5FF"));
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
                }else{
                    westButton.setTextColor(Color.parseColor("#26C5FF"));
                    westPressed = false;
                    currentList = cafeList;
                    listAdapter.setList(currentList,currentList.size());
                    listAdapter.notifyDataSetChanged();
                }
                
                break;
        }

    }

    @Override
    public void onClick(int position) {
        //Toast.makeText(this,""+cafeList.size(),Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,MenuActivity.class);

        intent.putExtra("testData", currentList);
        intent.putExtra("cafeInfo", currentList.get(position));
        intent.putExtra("locName", currentList.get(position).getNickName());

        startActivity(intent);
    }

    public class ProcessJson extends AsyncTask<String, Void, ArrayList<CafeteriaModel>>{

        @Override
        protected ArrayList<CafeteriaModel> doInBackground(String... params) {
            String json = NetworkUtilities.getJson();
            boolean hey = dbHelper.addData(json);

            cafeList = JsonUtilities.parseJson(json, getApplicationContext());
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
