package com.cornellappdev.android.eatery.network;

import android.app.Activity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cornellappdev.android.eatery.AllCtEateriesQuery;
import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.MainListFragment;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;

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

    private static final String DINING_URI =
            "https://now.dining.cornell.edu/api/1.0/dining/eateries.json";
    private static final String GRAPHQL_URL = "http://eatery-backend.cornellappdev.com/";
    public static boolean collegetownEateriesLoaded = false;
    private static List<AllEateriesQuery.Eatery> eateries;
    private static List<AllCtEateriesQuery.CollegetownEatery> collegetownEateries;
    private static ApolloClient apolloClient;
    private static Repository rInstance = Repository.getInstance();

    private static void buildApolloClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient = ApolloClient.builder()
                .serverUrl(GRAPHQL_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

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

    public static void getEateries(Activity activity) {
        buildApolloClient();

        final AllEateriesQuery eateriesQuery = AllEateriesQuery.builder().build();
        ApolloCall<AllEateriesQuery.Data> eateryCall = apolloClient.query(eateriesQuery);
        eateryCall.enqueue(new ApolloCall.Callback<AllEateriesQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<AllEateriesQuery.Data> response) {
                eateries = response.data().eateries();
                ArrayList<EateryBaseModel> eateryList = JsonUtilities.parseEateries(eateries,
                        activity);
                Collections.sort(eateryList);
                rInstance.setEateryList(eateryList);

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

    public static void getCtEateries(Activity activity) {
        buildApolloClient();

        final AllCtEateriesQuery ctEateriesQuery = AllCtEateriesQuery.builder().build();
        ApolloCall<AllCtEateriesQuery.Data> ctEateryCall = apolloClient.query(ctEateriesQuery);
        ctEateryCall.enqueue(new ApolloCall.Callback<AllCtEateriesQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<AllCtEateriesQuery.Data> response) {
                collegetownEateriesLoaded = true;
                collegetownEateries = response.data().collegetownEateries();
                ArrayList<EateryBaseModel> collegetownEateryList = JsonUtilities.parseCtEateries(
                        activity, collegetownEateries);
                Collections.sort(collegetownEateryList);
                rInstance.setCtEateryList(collegetownEateryList);
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                collegetownEateriesLoaded = false;
            }
        });
    }

    // BRB callback
    public interface BRBAccountCallback {
        // parameters can be of any types, depending on the event defined
        void retrievedAccountInfo(BrbInfoQuery.AccountInfo accountInfo);
    }
}
