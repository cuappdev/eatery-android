package com.cornellappdev.android.eatery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cornellappdev.android.eatery.Model.CafeteriaModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    ArrayList<CafeteriaModel> cafeData;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        cafeData = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("cafeData");

        // Adds functionality for bottom nav bar
        bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast toast;
                Intent intent;
                switch(item.getItemId()) {
                    case R.id.action_home:
                        finish();
                        break;
                    case R.id.action_week:
                        finish();
                        intent = new Intent(getApplicationContext(), WeeklyMenuActivity.class);
                        intent.putExtra("cafeData", cafeData);
                        startActivity(intent);
                        break;
                    case R.id.action_brb:
                        // TODO(lesley): Add BRB feature
                        toast = Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // (42.4471,-76.4832) is the location for Day Hall
        LatLng cornell = new LatLng(42.451092,-76.482654);
        for (int i=0; i<cafeData.size(); i++){
            CafeteriaModel cafe = cafeData.get(i);
            Double lat = cafe.getLat();
            Double lng = cafe.getLng();
            LatLng latLng = new LatLng(lat,lng);
            String name = cafe.getName();
            String isOpenedStr = cafe.isOpen();
            String loc = cafe.getBuildingLocation();
            Marker cafeMarker =  mMap.addMarker(new MarkerOptions().position(latLng).title(name));
            cafeMarker.setSnippet(isOpenedStr + System.lineSeparator() + loc );

            if(cafe.getCurrentStatus()==CafeteriaModel.Status.CLOSED){
                cafeMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapDescriptorFromVector(this, R.drawable.gray_pin),72,96,false)));
            } else if(cafe.getCurrentStatus()==CafeteriaModel.Status.OPEN){
                cafeMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapDescriptorFromVector(this, R.drawable.blue_pin),72,96,false)));
            } else{
                cafeMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapDescriptorFromVector(this, R.drawable.blue_pin),72,96,false)));
            }
        }

        // Clicking on an eatery icon on the map will take the user to the MenuActivity of that eatery
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);

                String markerName = marker.getTitle();
                int position = 0;
                for(int i=0; i<cafeData.size();i++ ) {
                    if (cafeData.get(i).getName().equalsIgnoreCase(markerName)) {
                        position = i;
                    }
                }
                // TODO(lesley): is testData necessary??? Might have to be removed
                intent.putExtra("testData", cafeData);
                intent.putExtra("cafeInfo", cafeData.get(position));
                intent.putExtra("locName", cafeData.get(position).getNickName());

                startActivity(intent);
            }
        });

        // Create view for map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        enableMyLocationIfPermitted();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(15);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cornell));
    }

    // Gets user permission to use location
    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        // (42.4471,-76.4832) is the location for Day Hall
        LatLng cornell = new LatLng(42.4471,-76.4832);;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cornell));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }
        }
    }
    private Bitmap bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return bitmap;
    }


    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(15);
                    return false;
                }
            };
}
