package com.cornellappdev.android.eatery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.cornellappdev.android.eatery.data.CafeteriaDbHelper;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import com.cornellappdev.android.eatery.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

  private MainPresenter presenter;
  public BottomNavigationView bnv;
  public CafeteriaDbHelper dbHelper;

  public static boolean JSON_FALLBACK = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    presenter = new MainPresenter();
    dbHelper = new CafeteriaDbHelper(this);
    bnv = findViewById(R.id.bottom_navigation);
    // Add functionality to bottom nav bar
    bnv.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
              case R.id.action_home:
                transaction
                    .replace(R.id.frame_fragment_holder, new MainListFragment())
                    .commit();
                break;
              case R.id.action_week:
                transaction
                    .replace(R.id.frame_fragment_holder, new WeeklyMenuFragment())
                    .commit();
                break;
              case R.id.action_brb:
                transaction
                    .replace(R.id.frame_fragment_holder, new AboutFragment())
                    .commit();
                break;
            }
            return true;
          }
        });

    // Try pulling data from GraphQL, if not fallback to json from cornell dining
    NetworkUtilities.getEateries(presenter,this);
    if (JSON_FALLBACK) {
      new ProcessJson().execute("");
    }
  }

  public class ProcessJson extends AsyncTask<String, Void, ArrayList<EateryBaseModel>> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected ArrayList<EateryBaseModel> doInBackground(String... params) {
      ArrayList<EateryBaseModel> eateryList = new ArrayList<>();
      ConnectivityManager cm =
          (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

      NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

      boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
      if (!isConnected
          && JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext()) != null) {
        eateryList = JsonUtilities.parseJson(dbHelper.getLastRow(), getApplicationContext());
      } else {
        String json = NetworkUtilities.getJSON();
        dbHelper.addData(json);
        eateryList = JsonUtilities.parseJson(json, getApplicationContext());
      }
      Collections.sort(eateryList);
      return eateryList;
    }

    @Override
    protected void onPostExecute(ArrayList<EateryBaseModel> result) {
      presenter.setEateryList(result);

      try {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frame_fragment_holder, new MainListFragment())
            .commit();
      } catch (Exception e) {
        super.onPostExecute(result);
      }
    }
  }
}
