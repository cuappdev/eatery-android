package com.example.jc.eatery_android;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<CafeteriaModel> cafeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        cafeData = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("cafeData");

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng cornell = new LatLng(42.4471,-76.4832);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        for(int i=0; i<cafeData.size(); i++){
            CafeteriaModel cafe = cafeData.get(i);
            Double lat = cafe.getLat();
            Double lng = cafe.getLng();
            LatLng temp = new LatLng(lat,lng);
            String name = cafe.getName();
            mMap.addMarker(new MarkerOptions().position(temp).title(name));

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cornell));
    }


}
