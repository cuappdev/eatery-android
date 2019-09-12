package com.cornellappdev.android.eatery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.CollegeTownModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.util.TimeUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class CtownMenuActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView mCafeDirections;
    TextView mCafeIsOpen;
    TextView mCafeLoc;
    TextView mCafeText;
    TextView mCafeWebsite;
    TextView mCafePhoneNumber;
    TextView mDollarSignOne;
    TextView mDollarSignTwo;
    TextView mDollarSignThree;
    RatingBar mCafeRating;
    EateryBaseModel mCafeData;
    GoogleMap mMap;
    MapView mMapView;
    Toolbar mToolbar;
    AppBarLayout mAppbar;
    CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctown_eatery);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        Intent intent = getIntent();
        mCafeData = (CollegeTownModel) intent.getSerializableExtra("cafeInfo");
        String cafeName = mCafeData.getNickName();
        String imageUrl = ((CollegeTownModel) mCafeData).getImageUrl();

        // Load image animation
        Picasso.get()
                .load(imageUrl)
                .noFade()
                .into((ImageView)findViewById(R.id.ind_image));

        mCafeText = findViewById(R.id.ind_cafe_name);
        mCafeText.setText(cafeName);
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle(" ");
        mCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayout);

        // Shows/hides title depending on scroll offset
        mAppbar = findViewById(R.id.appbar);
        mAppbar.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = true;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            mCollapsingToolbar.setTitle(cafeName);
                            isShow = true;
                        } else if (isShow) {
                            mCollapsingToolbar.setTitle(" ");
                            isShow = false;
                        }
                    }
                });

        // Format string for opening/closing time
        mCafeIsOpen = findViewById(R.id.ind_open);
        CollegeTownModel.Status currentStatus = mCafeData.getCurrentStatus();
        mCafeIsOpen.setText(currentStatus.toString());
        if (currentStatus == EateryBaseModel.Status.OPEN) {
            mCafeIsOpen.setTextColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.green));
        } else if (currentStatus == EateryBaseModel.Status.CLOSINGSOON) {
            mCafeIsOpen.setTextColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.yellow));
        } else {
            mCafeIsOpen.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
        mCafeText = findViewById(R.id.ind_time);
        mCafeText.setText(TimeUtil.format(mCafeData.getCurrentStatus(), mCafeData.getChangeTime()));
        mCafeLoc = findViewById(R.id.ind_loc);
        mCafeLoc.setText(mCafeData.getBuildingLocation());
        mDollarSignOne = findViewById(R.id.dollar_sign_1);
        mDollarSignTwo = findViewById(R.id.dollar_sign_2);
        mDollarSignThree = findViewById(R.id.dollar_sign_3);
        String price = ((CollegeTownModel) mCafeData).getPrice();
        // price is either $, $$, or $$$. Adjust the corresponding texts
        if (price.equals("$")) {
            mDollarSignTwo.setTextColor(ContextCompat.getColor(this, R.color.inactive));
            mDollarSignThree.setTextColor(ContextCompat.getColor(this, R.color.inactive));
        } else if (price.equals("$$")) {
            mDollarSignThree.setTextColor(ContextCompat.getColor(this, R.color.inactive));
        }
        mCafeRating = findViewById(R.id.cafe_rating);
        LayerDrawable stars = (LayerDrawable) mCafeRating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.blue),
                PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.blue),
                PorterDuff.Mode.SRC_ATOP);
        mCafeRating.setRating(Float.parseFloat(((CollegeTownModel) mCafeData).getRating()));

        mCafeDirections = findViewById(R.id.cafe_directions);
        mCafeDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opening up google maps with the latitude and longitude on click
                String uri = String.format(Locale.ENGLISH,
                        "http://maps.google.com/maps?daddr=%f,%f",
                        mCafeData.getLatitude(), mCafeData.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        mCafePhoneNumber = findViewById(R.id.cafe_phone);
        String displayPhoneNumber = mCafeData.getPhoneNumber().
                substring(mCafeData.getPhoneNumber().length() - 10);
        mCafePhoneNumber.setText(String.format("Call (%s)-%s-%s",
                displayPhoneNumber.substring(0, 3),
                displayPhoneNumber.substring(3, 6),
                displayPhoneNumber.substring(6, 10)));
        mCafePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calling the number on click
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(String.format("tel: %s", mCafeData.getPhoneNumber())));
                startActivity(callIntent);
            }
        });

        mCafeWebsite = findViewById(R.id.cafe_website);
        mCafeWebsite.setText(String.format("Visit %s", mCafeData.getNickName()));
        mCafeWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opening up the website on click
                Uri uri = Uri.parse(((CollegeTownModel) mCafeData).getYelpUrl());
                // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mMapView = findViewById(R.id.cafe_map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
            mMapView.setClickable(true);
            mMapView.setFocusable(true);
            mMapView.setDuplicateParentStateEnabled(false);
        }
    }

    private Location getMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // If the user has not given permission for maps, request permission
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    1);
        }
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }

        return myLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MarkerOptions cafeMarker;
        mMap = googleMap;
        // Add the marker for the cafe's location (color of marker is red)
        cafeMarker = new MarkerOptions().position(new LatLng(mCafeData.getLatitude(),
                mCafeData.getLongitude())).title(mCafeData.getName());
        mMap.addMarker(cafeMarker);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // If the user has not given permission for maps, request permission
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    1);
        } else if (mMap != null) {
            // Make the blue dot appear
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            CameraPosition position = CameraPosition.builder().target(
                    new LatLng(mCafeData.getLatitude(), mCafeData.getLongitude()))
                    .zoom(16).bearing(0).build();
            // Move the camera to be directly over the cafe
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            Location myLocation = getMyLocation();
            builder.include(cafeMarker.getPosition());
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                // When map loads, zoom out to see user position on the map as well
                @Override
                public void onMapLoaded() {
                    int padding = 230; // offset from edges of the map in pixels
                    if (myLocation != null) {
                        // Only display current location if within a specified range of the eatery
                        builder.include(
                                new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);

                    }
                }
            });
        }
    }
}
