package com.enfermeraya.enfermeraya.app;

import androidx.appcompat.app.AppCompatActivity;

import com.enfermeraya.enfermeraya.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsMarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    // Include the OnCreate() method here too, as described above.

    Modelo modelo = Modelo.getInstance();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(modelo.latitud, modelo.longitud);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Enfermeraya")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pingmapa))
                .draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
