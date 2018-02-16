package com.example.jc.eatery_android;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.jc.eatery_android.NetworkUtils.NetworkUtilities;

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
        protected String doInBackground(String... strings) {


            NetworkUtilities.getJson();


                return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }
    }
}
