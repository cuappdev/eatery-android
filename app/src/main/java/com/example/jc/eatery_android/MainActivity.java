package com.example.jc.eatery_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.NetworkUtils.NetworkUtilities;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ProcessJson().execute("");


    }

    public class ProcessJson extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {


            ArrayList<CafeteriaModel> test = NetworkUtilities.getJson();
            Log.i("tag",""+test.size());

            return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }
}
