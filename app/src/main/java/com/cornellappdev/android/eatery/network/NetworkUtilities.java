package com.cornellappdev.android.eatery.network;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.MainListFragment;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.presenter.MainPresenter;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;

public final class NetworkUtilities {
    private static final String DINING_URI = "https://now.dining.cornell.edu/api/1.0/dining/eateries.json";
    private static final String GRAPHQL_URL = "http://eatery-backend.cornellappdev.com/";
    private static final String TAG = "NetworkUtilities";
    private static List<AllEateriesQuery.Eatery> eateries;
    private static boolean mEvaluatedJS;
    private static String loginJS;

    public static String getJSON() {
        try {
            URL url = new URL(DINING_URI);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream stream = connection.getInputStream();
            InputStreamReader isw = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(isw);
            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String json = buffer.toString();
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getEateries(MainPresenter presenter, Activity activity) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(GRAPHQL_URL)
                .okHttpClient(okHttpClient)
                .build();

        final AllEateriesQuery eateriesQuery = AllEateriesQuery.builder().build();
        ApolloCall<AllEateriesQuery.Data> eateryCall = apolloClient.query(eateriesQuery);
        eateryCall.enqueue(new ApolloCall.Callback<AllEateriesQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<AllEateriesQuery.Data> response) {
                eateries = response.data().eateries();
                ArrayList<EateryBaseModel> eateryList = JsonUtilities.parseEateries(eateries, activity);
                Collections.sort(eateryList);
                presenter.setEateryList(eateryList);

                // Runs on MainActivity's UI Thread
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((MainActivity) activity).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frame_fragment_holder, new MainListFragment())
                                    .commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                MainActivity.JSON_FALLBACK = true;
            }
        });
    }


    //BRB callback
    public interface BRBAccountCallback {
        //parameters can be of any types, depending on the event defined
        void retrievedAccountInfo(BrbInfoQuery.AccountInfo accountInfo);
    }

    public static void getBrbInfo(String session_id, BRBAccountCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl(GRAPHQL_URL)
                .okHttpClient(okHttpClient)
                .build();
        final BrbInfoQuery brbInfoQuery = BrbInfoQuery.builder().accountId(session_id).build();
        ApolloCall<BrbInfoQuery.Data> brbCall = apolloClient.query(brbInfoQuery);
        brbCall.enqueue(new ApolloCall.Callback<BrbInfoQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<BrbInfoQuery.Data> response) {
                callback.retrievedAccountInfo(response.data().accountInfo());
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                MainActivity.JSON_FALLBACK = true;
            }
        });
    }
}
