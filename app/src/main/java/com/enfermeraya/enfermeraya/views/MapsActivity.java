package com.enfermeraya.enfermeraya.views;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.Splash;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    final static int REQUEST_LOCATION = 199;
    private FusedLocationProviderClient fClient;

    //gps
    private LocationManager locManager;
    private Location loc;
    double latitud = 0.0;
    double longitud = 0.0;


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

        if (savedInstanceState != null){
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
            finish();
            return;
        }

        fClient = LocationServices.getFusedLocationProviderClient(this);

        //permisos


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

        getLocationPermission();

        permisogps();
        // Add a marker in Sydney and move the camera

        //5.059288, -75.497652
        LatLng ctg = new LatLng(latitud, longitud);// colombia
        CameraPosition possiCameraPosition = new CameraPosition.Builder().target(ctg).zoom(15).bearing(0).tilt(0).build();
        CameraUpdate cam3 =
                CameraUpdateFactory.newCameraPosition(possiCameraPosition);
        mMap.animateCamera(cam3);
        mMap.addMarker(new MarkerOptions().position(ctg).title("Mi ubicación"));

        float verde = BitmapDescriptorFactory.HUE_GREEN;
        marcadorColor(latitud, longitud,"Pais Colombia", verde);
    }


    private void marcadorColor(double lat, double lng, String  pais, float color){
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pais).icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

    public void menu(View v){
        Intent i = new Intent(getApplicationContext(), Menu.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }


    //permiso gps
    public void permisogps(){

        //permisos
        ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            Toast.makeText(getApplicationContext(), "No se han definido los permisos necesarios.", Toast.LENGTH_SHORT).show();
            return;
        }else
        {
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            try {

                if(loc != null){
                    latitud = loc.getLatitude();
                    longitud = loc.getLongitude();
                }else{
                    showAlertGPs();
                }
            }catch (Exception e){}


        }


    }

    private void showAlertGPs() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
            startLocationUpdates();
            //getUltimaUbicacion();


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }


    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5 * 1000);
        mLocationRequest.setSmallestDisplacement(3);
        mLocationRequest.setFastestInterval(4 * 1000);


        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);


        SettingsClient settingsClient = LocationServices.getSettingsClient(MapsActivity.this);
        Task resul = settingsClient.checkLocationSettings(builder.build());


        final LocationRequest finalMLocationRequest = mLocationRequest;
        resul.addOnSuccessListener(this, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                //fClient.requestLocationUpdates(finalMLocationRequest, mLocationCallback, null );


            }
        });

        resul.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                mLocationPermissionGranted = true;

            } else {
                mLocationPermissionGranted = false;
            }

            startLocationUpdates();
        }

    }


    /////////////////////////////

}
