package com.cornellappdev.android.eatery.network;

import android.app.Activity;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.BrbInfoQuery;
import com.cornellappdev.android.eatery.MainListFragment;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CacheType;
import com.cornellappdev.android.eatery.util.InternalStorage;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;

public final class NetworkUtilities {
    private static final String GRAPHQL_URL = "https://eatery-backend.cornellappdev.com/";
    private static List<AllEateriesQuery.Eatery> eateries;
    private static ApolloClient apolloClient;
    private static final Repository rInstance = Repository.getInstance();

    private static void buildApolloClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        apolloClient = ApolloClient.builder()
                .serverUrl(GRAPHQL_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

    public static void getEateries(Activity activity, MainListFragment mainFragment) {
        buildApolloClient();

        final AllEateriesQuery eateriesQuery = AllEateriesQuery.builder().build();
        ApolloCall<AllEateriesQuery.Data> eateryCall = apolloClient.query(eateriesQuery);
        eateryCall.enqueue(new ApolloCall.Callback<AllEateriesQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<AllEateriesQuery.Data> response) {
                if (response.getData() != null) {
                    eateries = response.getData().eateries();
                    ArrayList<EateryBaseModel> eateryList = QueryUtilities.parseEateries(
                            eateries,
                            activity);
                    Collections.sort(eateryList);
                    try {
                        InternalStorage.writeObject(activity.getApplicationContext(),
                                CacheType.CAMPUS_EATERY, eateryList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    rInstance.setEateryList(eateryList);
                    activity.runOnUiThread(() -> {
                        try {
                            mainFragment.initializeEateries();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
            }
        });
    }

    static void getBrbInfo(String session_id, BRBAccountCallback callback) {
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
                if (response.getData() != null) {
                    callback.retrievedAccountInfo(response.getData().accountInfo());
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
            }
        });
    }

    // BRB callback
    public interface BRBAccountCallback {
        // parameters can be of any types, depending on the event defined
        void retrievedAccountInfo(BrbInfoQuery.AccountInfo accountInfo);
    }
}
