package com.enfermeraya.enfermeraya.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.views.MapsActivity;
import com.enfermeraya.enfermeraya.views.Menu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback {

    private HomeViewModel homeViewModel;
    Modelo modelo = Modelo.getInstance();
    Geocoder geocoder = null;
    private GoogleMap mMap;
    EditText search;
    MarkerOptions markerOptions;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        markerOptions = new MarkerOptions();
        mapFragment.getMapAsync(this::goolemapa);

        search = (EditText)root.findViewById(R.id.search);
        mapa();

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        goolemapa(mMap);


    }


    public void goolemapa(GoogleMap mMap){


        //5.059288, -75.497652
        LatLng ctg = new LatLng(modelo.latitud, modelo.longitud);// colombia
        CameraPosition possiCameraPosition = new CameraPosition.Builder().target(ctg).zoom(15).bearing(0).tilt(0).build();
        CameraUpdate cam3 =
                CameraUpdateFactory.newCameraPosition(possiCameraPosition);
        mMap.animateCamera(cam3);
       // mMap.addMarker(new MarkerOptions().position(ctg).title("Mi ubicación"));

       // float verde = BitmapDescriptorFactory.HUE_GREEN;
        //marcadorColor(modelo.latitud, modelo.longitud,"Pais Colombia", verde,mMap);
        marcadorImg(modelo.latitud, modelo.longitud,"Pais Colombia",mMap);
        //setLocation();

    }

    private void marcadorColor(double lat, double lng, String  pais, float color, GoogleMap mMap){
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pais).icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

    private void marcadorImg(double lat, double lng, String  pais, GoogleMap mMap){

        LatLng  latLng = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Enfermeraya")
                .snippet(getCity(latLng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pingmapa))
                .draggable(true)
        );


        if (mMap != null) {
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    Log.v("1","1");
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    Log.v("2","2");
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Log.v("3","3");
                    getCity(marker.getPosition());
                }
            });


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(getContext(),"click", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

    }



    //GPS
    public void mapa(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        HomeFragment.Localizacion Local = new HomeFragment.Localizacion();
        Local.setMainActivity(HomeFragment.this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        Toast.makeText(getActivity(),"Localización agregada", Toast.LENGTH_SHORT).show();

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {

                try {
                    if(geocoder == null){
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    }


                }catch (Exception e){
                    String ex = e.getMessage();
                }
                
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);


                    String dir =  DirCalle.getAddressLine(0);

                    // Toast.makeText(getApplicationContext(),"Mi direccion es "+ dir, Toast.LENGTH_SHORT).show();
                    String[] parts = dir.split(",");
                    String direc = parts[0];
                    search.setText("" + direc);
                    search.setSelection(search.getText().length());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public  String getCity(LatLng posicion){

        Geocoder geocoder;
        List<Address> addresses;
        String dir  = "";

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            List<Address> list = geocoder.getFromLocation(
            posicion.latitude, posicion.longitude, 1);
            if (!list.isEmpty()) {
                Address DirCalle = list.get(0);


                 dir = DirCalle.getAddressLine(0);

                // Toast.makeText(getApplicationContext(),"Mi direccion es "+ dir, Toast.LENGTH_SHORT).show();
                String[] parts = dir.split(",");
                String direc = parts[0];
                search.setText("" + direc);

            }
        }catch (Exception e){
            String ex = e.getMessage();
            e.printStackTrace();
        }
        return dir;
    }


    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        HomeFragment mainActivity;
        public HomeFragment getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(HomeFragment mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();


            modelo.latitud = loc.getLatitude();
            modelo.longitud = loc.getLongitude();

           // this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado

            Toast.makeText(getActivity().getApplicationContext(),"GPS Desactivado", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado

            Toast.makeText(getActivity().getApplicationContext(),"GPS Activado", Toast.LENGTH_SHORT).show();
            goolemapa(mMap);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }



    //fin gps



}
