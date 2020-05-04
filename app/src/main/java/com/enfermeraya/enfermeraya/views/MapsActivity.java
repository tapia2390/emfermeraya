package com.enfermeraya.enfermeraya.views;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;

import com.enfermeraya.enfermeraya.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_maps);
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

        // Add a marker in Sydney and move the camera

        //5.059288, -75.497652
        LatLng ctg = new LatLng(5.059288, -75.497652);// colombia
        CameraPosition possiCameraPosition = new CameraPosition.Builder().target(ctg).zoom(15).bearing(0).tilt(0).build();
        CameraUpdate cam3 =
                CameraUpdateFactory.newCameraPosition(possiCameraPosition);
        mMap.animateCamera(cam3);
        mMap.addMarker(new MarkerOptions().position(ctg).title("Mi ubicación"));

        float verde = BitmapDescriptorFactory.HUE_GREEN;
        marcadorColor(5.059288, -75.497652,"Pais Colombia", verde);
    }


    private void marcadorColor(double lat, double lng, String  pais, float color){
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pais).icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

}
