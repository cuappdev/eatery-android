package com.cornellappdev.android.eatery.presenter;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.cornellappdev.android.eatery.MainListFragment;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.CollegeTownModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.Category;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainListPresenter {

    private ArrayList<EateryBaseModel> mCurrentList;
    private HashSet<PaymentMethod> mPaymentSet;
    private HashSet<CampusArea> mAreaSet;
    private HashSet<Category> mCategorySet;
    private String mQuery;
    private Location mLocation;
    private LocationListener mLocationListener;

    private Repository rInstance = Repository.getInstance();

    public MainListPresenter() {
        mCurrentList = rInstance.getEateryList();
        mPaymentSet = new HashSet<>();
        mAreaSet = new HashSet<>();
        mCategorySet = new HashSet<>();
        mQuery = "";
    }

    public void initializeLocationListener(Context c) {
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        LocationManager locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,
                mLocationListener);
    }

    public ArrayList<EateryBaseModel> getEateryList() {
        return rInstance.getEateryList();
    }

    public ArrayList<EateryBaseModel> getCtEateryList() {
        return rInstance.getCtEateryList();
    }

    public void setPaymentSet(HashSet<PaymentMethod> paymentSet) {
        mPaymentSet = paymentSet;
    }

    public void setAreaSet(HashSet<CampusArea> areaSet) {
        mAreaSet = areaSet;
    }

    public void setCategorySet(HashSet<Category> categorySet) {
        mCategorySet = categorySet;
    }

    private boolean hasPaymentMethod(EateryBaseModel model) {
        for (PaymentMethod method : mPaymentSet) {
            if (model.hasPaymentMethod(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUnderCategory(EateryBaseModel model) {
        for (Category category : mCategorySet) {
            if (((CollegeTownModel) model).isUnderCategory(category)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<EateryBaseModel> getCafesToDisplay() {
        ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
        for (EateryBaseModel em : mCurrentList) {
            if (em.matchesFilter() && em.matchesSearch()) {
                cafesToDisplay.add(em);
            }
        }
        return cafesToDisplay;
    }
    // Returns whether sorting by nearest first was successful or not
    public boolean sortNearestFirst(Context context, MainListFragment mainListFragment) {
        // It's fine to have this code in presenter, right?
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mainListFragment.requestLocationPermissions();
            return false;
        }
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (mLocation == null) {
            mLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        if (mLocation != null) {
            mCurrentList.sort((EateryBaseModel o1, EateryBaseModel o2) -> {
                // Ensure all open eateries come before all closed eateries
                if (o1.isOpen() && !o2.isOpen()) {
                    return -1;
                }
                if (!o1.isOpen() && o2.isOpen()) {
                    return 1;
                }

                // If they are either both open or both closed, sort based on distance
                double dist1 = Math.pow(o1.getLatitude() - mLocation.getLatitude(), 2) +
                        Math.pow(o1.getLongitude() - mLocation.getLongitude(), 2);
                double dist2 = Math.pow(o2.getLatitude() - mLocation.getLatitude(), 2) +
                        Math.pow(o2.getLongitude() - mLocation.getLongitude(), 2);
                return Double.compare(dist1, dist2);
            });
            return true;
        }
        else {
            return false;
        }
    }

    public void sortAlphabetical() {
        Collections.sort(mCurrentList);
    }


    public void filterImageList() {
        for (EateryBaseModel model : mCurrentList) {
            boolean areaFuzzyMatches =
                    mAreaSet.isEmpty() || mAreaSet.contains(model.getArea());
            boolean paymentFuzzyMatches =
                    mPaymentSet.isEmpty() || hasPaymentMethod(model);

            // only used for ctown eateries
            boolean categoryFuzzyMatches = mCategorySet.isEmpty()
                    || (model instanceof CollegeTownModel && isUnderCategory(model));

            if (!model.isCtEatery() && areaFuzzyMatches && paymentFuzzyMatches) {
                model.setMatchesFilter(true);
            } else if (model.isCtEatery() && categoryFuzzyMatches) {
                model.setMatchesFilter(true);
            } else {
                model.setMatchesFilter(false);
            }
        }
    }

    public void setIsSearchPressed(boolean isPressed) {
        rInstance.setIsSearchPressed(isPressed);
    }

    private void searchList(String query) {
        final String lowercaseQuery = query.toLowerCase();
        for (EateryBaseModel model : mCurrentList) {
            final HashSet<String> mealSet = model.getMealItems();

            boolean foundNickName = false;
            if (model.getNickName().toLowerCase().contains(lowercaseQuery)) {
                foundNickName = true;
            }

            ArrayList<String> matchedItems = new ArrayList<>();
            boolean foundItem = false;

            for (String item : mealSet) {
                if (item.toLowerCase().contains(lowercaseQuery)) {
                    foundItem = true;
                    matchedItems.add(item);
                }
            }

            if (foundItem || foundNickName) {
                if (foundNickName) {
                    model.setSearchedItems(new ArrayList<>(mealSet));
                } else {
                    model.setSearchedItems(matchedItems);
                }
                model.setMatchesSearch(true);
            } else {
                model.setMatchesSearch(false);
            }
        }
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        this.mQuery = query;
    }

    // Updates the eatery models to matchSearch for this specific query
    public void filterSearchList() {
        searchList(mQuery);
    }

    // Returns all eateries that matchsearch and filter
    public ArrayList<EateryBaseModel> getCurrentList() {
        ArrayList<EateryBaseModel> cafesToDisplay = new ArrayList<>();
        for (EateryBaseModel em : mCurrentList) {
            if (em.matchesFilter() && em.matchesSearch()) {
                cafesToDisplay.add(em);
            }
        }
        return cafesToDisplay;
    }

    public void setCurrentList(ArrayList<EateryBaseModel> eateryList) {
        mCurrentList = eateryList;
    }
}
