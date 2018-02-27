package com.example.jc.eatery_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jc.eatery_android.Model.CafeteriaModel;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        Intent intent = getIntent();
        ArrayList<CafeteriaModel> test = null;

        test = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("testData");

        Log.i("test",""+test.size());

    }
}
