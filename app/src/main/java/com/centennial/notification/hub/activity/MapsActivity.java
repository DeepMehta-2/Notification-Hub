package com.centennial.notification.hub.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.centennial.notification.hub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;

    String appName;
    byte[] appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        appName = getIntent().getExtras().getString("appName", getResources().getString(R.string.app_name));
        appIcon = getIntent().getExtras().getByteArray("appIcon");
        latitude = (double) getIntent().getExtras().get("latitude");
        longitude = (double) getIntent().getExtras().get("longitude");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in user's current location and move the camera
        LatLng locationMarker = new LatLng(latitude, longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(locationMarker).title(appName));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationMarker, 15));

        if (appIcon != null) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(appIcon, 0, appIcon.length),
                            100, 100, true));
            Bitmap bitmap = bitmapDrawable.getBitmap();
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
    }
}