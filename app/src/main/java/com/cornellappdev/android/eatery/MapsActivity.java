package com.cornellappdev.android.eatery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.util.TimeUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
	private GoogleMap mMap;
	ArrayList<EateryBaseModel> cafeData;
	public Repository rInstance = Repository.getInstance();
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
	public BottomNavigationView bnv;

	class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

		private View myContentsView;

		MyInfoWindowAdapter() {
			myContentsView = getLayoutInflater().inflate(R.layout.map_info_layout, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			myContentsView = getLayoutInflater().inflate(R.layout.map_info_layout, null);
			TextView cafe_name = ((TextView) myContentsView.findViewById(R.id.info_cafe_name));
			cafe_name.setText(marker.getTitle());
			TextView cafe_open = ((TextView) myContentsView.findViewById(R.id.info_cafe_open));
			TextView cafe_desc = ((TextView) myContentsView.findViewById(R.id.info_cafe_desc));
			String firstword = marker.getSnippet().split(" ")[0];
			if (firstword.equalsIgnoreCase("open")) {
				cafe_open.setText(R.string.open);
				cafe_open.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
				cafe_desc.setText(marker.getSnippet().substring(5));
			} else if (firstword.equalsIgnoreCase("closing")) {
				cafe_open.setText(R.string.closing_soon);
				cafe_open.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
				cafe_desc.setText(marker.getSnippet().substring(13));
			} else {
				cafe_open.setText(firstword);
				cafe_open.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
				cafe_desc.setText(marker.getSnippet().substring(7));
			}
			return myContentsView;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		SupportMapFragment mapFragment =
				(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		Intent intent = getIntent();
		cafeData = rInstance.getEateryList();
		bnv = findViewById(R.id.bottom_navigation);
		bnv.setOnNavigationItemSelectedListener(
				new BottomNavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						Intent intent;
						switch (item.getItemId()) {
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
								finish();
								intent = new Intent(getApplicationContext(), InfoActivity.class);
								intent.putExtra("cafeData", cafeData);
								startActivity(intent);
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
		LatLng cornell = new LatLng(42.451092, -76.482654);
		for (int i = 0; i < cafeData.size(); i++) {
			EateryBaseModel cafe = cafeData.get(i);
			Double lat = cafe.getLatitude();
			Double lng = cafe.getLongitude();
			LatLng latLng = new LatLng(lat, lng);
			String name = cafe.getNickName();
			String isOpenedStr = cafe.getCurrentStatus().toString();
			String loc = TimeUtil.format(cafe.getCurrentStatus(), cafe.getChangeTime());
			Marker cafeMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(name));
			cafeMarker.setSnippet(isOpenedStr + " " + loc);

			if (cafe.getCurrentStatus() == EateryBaseModel.Status.CLOSED) {
				cafeMarker.setIcon(
						BitmapDescriptorFactory.fromBitmap(
								Bitmap.createScaledBitmap(
										bitmapDescriptorFromVector(this, R.drawable.gray_pin), 72, 96, false)));
			} else if (cafe.getCurrentStatus() == EateryBaseModel.Status.OPEN) {
				cafeMarker.setIcon(
						BitmapDescriptorFactory.fromBitmap(
								Bitmap.createScaledBitmap(
										bitmapDescriptorFromVector(this, R.drawable.blue_pin), 72, 96, false)));
			} else {
				cafeMarker.setIcon(
						BitmapDescriptorFactory.fromBitmap(
								Bitmap.createScaledBitmap(
										bitmapDescriptorFromVector(this, R.drawable.blue_pin), 72, 96, false)));
			}
		}

		// Clicking on an eatery icon on the map will take the user to the MenuActivity of that eatery
		mMap.setOnInfoWindowClickListener(
				new GoogleMap.OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker marker) {
						Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
						String markerName = marker.getTitle();
						int position = 0;
						for (int i = 0; i < cafeData.size(); i++) {
							if (cafeData.get(i).getNickName().equalsIgnoreCase(markerName)) {
								position = i;
							}
						}
						intent.putExtra("testData", cafeData);
						intent.putExtra("cafeInfo", cafeData.get(position));
						intent.putExtra("locName", cafeData.get(position).getNickName());

						startActivity(intent);
					}
				});

		mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
		mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
		enableMyLocationIfPermitted();
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.setMinZoomPreference(15);
		mMap.moveCamera(CameraUpdateFactory.newLatLng(cornell));
	}

	public boolean onOptionsItemSelected(MenuItem item){
		finish();
		return true;
	}

	// Gets user permission to use location
	private void enableMyLocationIfPermitted() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					this,
					new String[]{
							Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
					},
					LOCATION_PERMISSION_REQUEST_CODE);
		} else if (mMap != null) {
			mMap.setMyLocationEnabled(true);
		}
	}

	private void showDefaultLocation() {
		Toast.makeText(
				this,
				"Location permission not granted, " + "showing default location",
				Toast.LENGTH_SHORT)
				.show();
		// (42.4471,-76.4832) is the location for Day Hall
		LatLng cornell = new LatLng(42.4471, -76.4832);
		;
		mMap.moveCamera(CameraUpdateFactory.newLatLng(cornell));
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case LOCATION_PERMISSION_REQUEST_CODE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
		vectorDrawable.setBounds(
				0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
		Bitmap bitmap =
				Bitmap.createBitmap(
						vectorDrawable.getIntrinsicWidth(),
						vectorDrawable.getIntrinsicHeight(),
						Bitmap.Config.ARGB_8888);
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
