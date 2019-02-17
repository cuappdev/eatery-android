package com.cornellappdev.android.eatery.network;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;

public final class NetworkUtilities {
    private static final String DINING_URI = "https://now.dining.cornell.edu/api/1.0/dining/eateries.json";
    private final static String GRAPHQL_URL = "http://eatery-backend.cornellappdev.com/";
    private static List<AllEateriesQuery.Eatery> eateries;
    public static boolean EATERIES_LOADED = false;

    public static String getJSON() {
        try {
            URL url = new URL(DINING_URI);
            BufferedReader reader = null;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream stream = connection.getInputStream();
            InputStreamReader isw = new InputStreamReader(stream);
            reader = new BufferedReader(isw);
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

    public static List<AllEateriesQuery.Eatery> getEateries() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

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
                EATERIES_LOADED = true;
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("network-util", "apollo failed");
                MainActivity.JSON_FALLBACK = true;
            }
        });
        return eateries;
    }
}
