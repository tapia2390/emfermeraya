package com.enfermeraya.enfermeraya.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.util.Util;
import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Favoritos;
import com.enfermeraya.enfermeraya.clases.RecyclerItemClickListener;
import com.enfermeraya.enfermeraya.clases.Servicios;
import com.enfermeraya.enfermeraya.clases.Usuario;
import com.enfermeraya.enfermeraya.comandos.ComandoEnfermeroPrestadorSer;
import com.enfermeraya.enfermeraya.comandos.ComandoFavoritos;
import com.enfermeraya.enfermeraya.comandos.ComandoSercicio;
import com.enfermeraya.enfermeraya.comandos.ComandoServicio;
import com.enfermeraya.enfermeraya.dapter.FavoritoAdapter;
import com.enfermeraya.enfermeraya.dapter.ServicioAdapter;
import com.enfermeraya.enfermeraya.models.utility.Utility;
import com.enfermeraya.enfermeraya.notificacion.APIService;
import com.enfermeraya.enfermeraya.notificacion.Client;
import com.enfermeraya.enfermeraya.notificacion.Data;
import com.enfermeraya.enfermeraya.notificacion.MyResponse;
import com.enfermeraya.enfermeraya.notificacion.NotificationSender;
import com.enfermeraya.enfermeraya.views.ListaFavoritos;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment implements
        OnMapReadyCallback, ComandoFavoritos.OnFavoritosChangeListener, ComandoSercicio.OnSercicioChangeListener
        , TimePickerDialog.OnTimeSetListener, android.app.TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, ComandoEnfermeroPrestadorSer.OnSercicioChangeListener
         {

    Modelo modelo = Modelo.getInstance();
    Geocoder geocoder = null;
    EditText search;
    ImageView imgsearc;
    MarkerOptions markerOptions;
    LatLng startingPoint;
    ComandoFavoritos comandoFavoritos;
    ComandoSercicio comandoSercicio;
    Utility utility;
    private FavoritoAdapter favortoAdapter;
    private ServicioAdapter servicioAdapter;
    Button btn_servicio;
    GoogleMap mMap;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //adapter
    private List<Favoritos> favList = new ArrayList<>();
    private List<Servicios> serList = new ArrayList<>();

    //datepiker
    DatePickerDialog datePickerDialog;
    Date date;
    DateFormat hourFormat;

    Button txtfechainicio;
    Button txtfechafin;
    int setHora = 0;
    String token = "";

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutManager2;
    RecyclerView recyclerView, recyclerView2;
    HomeViewModel homeViewModel;
    ComandoEnfermeroPrestadorSer comandoEnfermeroPrestadorSer;


    private Serializable escolas;
    private Circle mCircle;
    private Marker mMarker;

    String tiemposervicio = "";


    private APIService apiService;

    SearchView search2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        markerOptions = new MarkerOptions();
        mapFragment.getMapAsync(this::goolemapa);

        comandoFavoritos = new ComandoFavoritos(this);
        comandoSercicio = new ComandoSercicio(this);
        utility = new Utility();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        //search gogole map
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getActivity());


        search = (EditText) root.findViewById(R.id.search);
        imgsearc = (ImageView) root.findViewById(R.id.imgsearc);
        btn_servicio = (Button) root.findViewById(R.id.btn_servicio);

        //btn_n1 = (Button) root.findViewById(R.id.btn_n1);
        ///btn_n2 = (Button) root.findViewById(R.id.btn_n2);

        token = FirebaseInstanceId.getInstance().getToken();
        modelo.token = token;
        comandoEnfermeroPrestadorSer = new ComandoEnfermeroPrestadorSer(this);



        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // Toast.makeText(getActivity(),"Enviado a todos!", Toast.LENGTH_LONG).show();
            }
        });

        /*btn_n1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificacionEspecifico();
            }
        });


        btn_n2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificacionTropico();
            }
        });*/


        mapa();


        imgsearc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogmap(search.getText().toString());

            }
        });


        btn_servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utility.estado(getActivity())) {
                    if (search.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Ingrese una dirección", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    generarServicio();
                } else {
                    alerta("Sin Internet", "Valide la conexión a internet");
                }
            }
        });


        if (utility.estado(getActivity())) {
            modelo.listServicios.clear();
            comandoFavoritos.getListFavorito();
            comandoSercicio.getListServicio();
            comandoSercicio.getTipoServicio();
            comandoSercicio.updateToken(token);
            comandoEnfermeroPrestadorSer.getListClient();
        } else {
            alerta("Sin Internet", "Valide la conexión a internet");
        }

        date = new Date();
        hourFormat = new SimpleDateFormat("hh:mm aa");
        System.out.println("Hora: " + hourFormat.format(date));


        search.setFocusable(false);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME);

                // create Intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(getActivity());

                //star activity result
                startActivityForResult(intent,100);

            }
        });


        return root;

        ///https://es.stackoverflow.com/questions/116681/como-puedo-calcular-la-distancia-entre-2-puntos-en-google-maps-v3



    }
    //puch
             public void sendNotifications(String usertoken, String title, String message) {
                 Data data = new Data(title, message);
                 NotificationSender sender = new NotificationSender(data, usertoken);
                 apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                     @Override
                     public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                         if (response.code() == 200) {
                             if (response.body().success != 1) {
                                 Toast.makeText(getActivity(), "Failed ", Toast.LENGTH_LONG).show();
                             }else{
                                 Toast.makeText(getActivity(), "servicio aceptado ", Toast.LENGTH_LONG).show();
                             }
                         }
                         else{
                             Toast.makeText(getActivity(), "Error code" + response.code(), Toast.LENGTH_SHORT).show();
                         }
                     }

                     @Override
                     public void onFailure(Call<MyResponse> call, Throwable t) {
                         String error = t.getMessage();
                         String error2 = call.request().body().toString();

                     }
                 });
             }

    public void notificacionEspecifico() {

        RequestQueue myrequest = Volley.newRequestQueue(getActivity());
        JSONObject json = new JSONObject();
        try {

            //dgwoF-I3Q5-Ey2XrHjsQH4:APA91bETkph4iSnuQuGDoDMXHCX9wzoXA4Nc9zNsCapOd9OQ7F_PjM1ZGEesZtk_bOvNFbc9FrYvMyndpzgN5x8FVnU41SlmEsnS2Zxn9WcBTjNJUIg-QVoF3z8dsmddBnHma094vhGK
            //fiU8SCcVQ0iVLaKM92VUc3:APA91bGb08P-X71QpmSGTuxGKWacitD8_QIMNPl_jcvwZrimLdclkr1x4j6ZbX_ZGAghVSbDw_t071xe27K3k8nt9JUUcSttEJwaEjD5NEwlvVMQDipoDYNsJNmLDZpu1veqkAFz9xCw
            //eAPR_vWsRp-tAlE2uWhCro:APA91bHHDYV8GzvBDJXOKjE0PMrbv_T7MQ27KS0fGCRFPxPNh2k_TmNBpuYNiqBq4XfRDTksxE7WJKAMO-enTRn5bkQ6LNrpz4jwZA6p2KgHv4PZAPYhm2kNJRt8BPrLhEk7uLxU6DQ0
            //"eAPR_vWsRp-tAlE2uWhCro:APA91bHHDYV8GzvBDJXOKjE0PMrbv_T7MQ27KS0fGCRFPxPNh2k_TmNBpuYNiqBq4XfRDTksxE7WJKAMO-enTRn5bkQ6LNrpz4jwZA6p2KgHv4PZAPYhm2kNJRt8BPrLhEk7uLxU6DQ0"
            String token = "eAPR_vWsRp-tAlE2uWhCro:APA91bHHDYV8GzvBDJXOKjE0PMrbv_T7MQ27KS0fGCRFPxPNh2k_TmNBpuYNiqBq4XfRDTksxE7WJKAMO-enTRn5bkQ6LNrpz4jwZA6p2KgHv4PZAPYhm2kNJRt8BPrLhEk7uLxU6DQ0 ";
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("titulo", "Titulo M");
            notification.put("detalle", "Detalle desde el movil");
            json.put("data", notification);
            String url = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json, null, null) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "AAAAjam3JLE:APA91bGNpDYH4v7oL2Ik8plXaKi60l8pyuWucBoAxmjZ0-TZlWSamGvF5r6Sjg3snoPNlSd7ugQtlK18-Rbzs5ISoUprY_-v200uIrjdsuWyE6fBkwYyjyq_MlHreD-MwAOf6o57iCvo");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e) {

        }

    }


    public void notificacionTropico() {
        RequestQueue myrequest = Volley.newRequestQueue(getActivity());
        JSONObject json = new JSONObject();
        try {

            String token = "/topics/" + "enviaratodos";
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("titulo", "Titulo M");
            notification.put("detalle", "Detalle desde el movil");
            json.put("data", notification);
            String url = "https://fcm.googleapis.com/fcm/notification";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json, null, null) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "aplication/json");
                    header.put("Authorization", "key=AAAAjam3JLE:APA91bGNpDYH4v7oL2Ik8plXaKi60l8pyuWucBoAxmjZ0-TZlWSamGvF5r6Sjg3snoPNlSd7ugQtlK18-Rbzs5ISoUprY_-v200uIrjdsuWyE6fBkwYyjyq_MlHreD-MwAOf6o57iCvo");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e) {

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // mMap = googleMap;
        modelo.mMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

      //  googleMap.setMyLocationEnabled(true);

        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);



        goolemapa(modelo.mMap );

    }


    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 400.0;
        int strokeColor = 0x000000ff; //red outline
        int shadeColor = 0x440000ff; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = modelo.mMap.addCircle(circleOptions);

        //MarkerOptions markerOptions = new MarkerOptions().position(position);
        //mMarker = modelo.mMap.addMarker(markerOptions);

    }


    public void goolemapa(GoogleMap mMap){

        modelo.mMap = mMap;
        //5.059288, -75.497652
        LatLng ctg = new LatLng(modelo.latitud, modelo.longitud);// colombia
        CameraPosition possiCameraPosition = new CameraPosition.Builder().target(ctg).zoom(15).bearing(0).tilt(0).build();
        CameraUpdate cam3 =
                CameraUpdateFactory.newCameraPosition(possiCameraPosition);
        mMap.animateCamera(cam3);
       // mMap.addMarker(new MarkerOptions().position(ctg).title("Mi ubicación"));

       // float verde = BitmapDescriptorFactory.HUE_GREEN;
        //marcadorColor(modelo.latitud, modelo.longitud,"Pais Colombia", verde,mMap);
        marcadorImg(modelo.latitud, modelo.longitud,"Pais",mMap);
        //setLocation();

        try {

            modelo.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(modelo.latitud, modelo.longitud), 15));

            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker

            options.position(new LatLng(modelo.latitud, modelo.longitud));

            //googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            LatLng latLng = new LatLng(modelo.latitud, modelo.longitud);
            drawMarkerWithCircle(latLng);


            modelo.mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    float[] distance = new float[2];

                        /*
                        Location.distanceBetween( mMarker.getPosition().latitude, mMarker.getPosition().longitude,
                                mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);
                                */

                    Location.distanceBetween( location.getLatitude(), location.getLongitude(),
                            mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);

                    if( distance[0] > mCircle.getRadius()  ){
                        //Afuera, distancia del centro
                        //Toast.makeText(getActivity(), "Outside, distance from center: " + distance[0] + " radius: " + mCircle.getRadius(), Toast.LENGTH_LONG).show();
                    } else {
                        //Dentro, distancia del centro:
                        //Toast.makeText(getActivity(), "Inside, distance from center: " + distance[0] + " radius: " + mCircle.getRadius() , Toast.LENGTH_LONG).show();
                    }

                }
            });




        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void goolemapaCliente(GoogleMap mMap, double lat, double longi, String nombre){

        modelo.mMap = mMap;
        LatLng ctg = new LatLng(modelo.latitud, modelo.longitud);// colombia
        CameraPosition possiCameraPosition = new CameraPosition.Builder().target(ctg).zoom(15).bearing(0).tilt(0).build();
        CameraUpdate cam3 =
                CameraUpdateFactory.newCameraPosition(possiCameraPosition);
        mMap.animateCamera(cam3);
        marcadorColor(modelo.latitud, modelo.longitud,nombre,mMap);

    }


    private void marcadorColor(double lat, double lng, String  pais, GoogleMap mMap){
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pais).icon(BitmapDescriptorFactory.defaultMarker()));
    }


    protected Marker createMarker(double lat, double lng, String  pais, GoogleMap mMap) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .anchor(0.5f, 0.5f)
                .title(pais)
                .snippet(pais)
                .icon(BitmapDescriptorFactory.defaultMarker()));
    }

    private void marcadorImg(double lat, double lng, String  pais, GoogleMap mMap){
        //modelo.mMap = mMap;
     //   MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng)).title("Enfermera");

        LatLng  latLng = new LatLng(lat,lng);

        modelo.mMap.clear();
        modelo.mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Enfermeraya")
                .snippet(getCity(latLng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pingmapa))
                .draggable(true)
        );


        if (modelo.mMap != null) {
            modelo.mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
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

                    modelo.latitud = marker.getPosition().latitude;
                    modelo.longitud = marker.getPosition().latitude;
                    goolemapa(modelo.mMap);
                    listaMapa();
                }
            });


            modelo.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //Toast.makeText(getContext(),"click", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });



            modelo.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                   // Toast.makeText(getActivity(), "datos"+latLng, Toast.LENGTH_SHORT).show();
                    modelo.latitud = latLng.latitude;
                    modelo.longitud = latLng.longitude;
                    goolemapa(modelo.mMap);
                    listaMapa();
                }
            });
        }

    }

    public void onMapSearch() {

        String str = search.getText().toString();
        List<Address> list = null;
        Geocoder geocoder = new Geocoder(getActivity());
        try {
            list = geocoder.getFromLocationName(
                    str,10);
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        if(list.size() > 0){


            String total_str = list.get(0).toString();
            System.out.println(":"+total_str);
            //Toast.makeText(getActivity().getApplicationContext(),total_str, Toast.LENGTH_LONG);
            int position=total_str.indexOf("latitude");
            String str1 = total_str.substring(position+9); //위치 받아옴(latitude)
            //  System.out.println(">>"+str1);
            int position_comma = str1.indexOf(",");
            String latitude = str1.substring(0,position_comma);
            System.out.println("latitude>>"+latitude);
            int position2=total_str.indexOf("longitude");
            String str2 = total_str.substring(position2+10); //위치 받아옴(latitude)

            int position_comma2 = str2.indexOf(",");
            String longitude = str2.substring(0,position_comma2);
            System.out.println("longitude>>"+longitude);

            startingPoint = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
            getCity(startingPoint);

            if(modelo.mMap != null){
                modelo.latitud = startingPoint.latitude;
                modelo.longitud = startingPoint.longitude;

                //getCity(startingPoint);
              //  marcadorImg(startingPoint.latitude,startingPoint.longitude, "",modelo.mMap);

                goolemapa(modelo.mMap);
            }

        }else{
            Toast.makeText(getActivity(),"Dirección no encontrda", Toast.LENGTH_SHORT).show();

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


        try {
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

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

    @Override
    public void getFavorito() {

        //Toast.makeText(getActivity(), ""+modelo.listFavoritos.size(), Toast.LENGTH_SHORT).show();

        if(recyclerView != null){


                favList = modelo.listFavoritos;
                favortoAdapter = new FavoritoAdapter(getActivity(), favList);
                layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView2.setLayoutManager(layoutManager2);
                recyclerView2.setAdapter(favortoAdapter);
                favortoAdapter.notifyDataSetChanged();

        }



    }



    @Override
    public void cargoFavorito() {

        //alerta("Registro","Favorito registrada con exito");
        if (utility.estado(getActivity())) {
            comandoFavoritos.getListFavorito();
        }else{
            alerta("Sin Internet","Valide la conexión a internet");
        }
    }

    @Override
    public void errorFavorito() {
        alerta("Error","No se pudo guardar la direción");
    }

    @Override
    public void getTipoServicio() {
        Log.v("size",""+ modelo.listTipoServicios.size());
    }

    @Override
    public void getServicio() {

        Log.v("tamano:", ""+modelo.listServicios.size());

        onMapReady(modelo.mMap);

        if(recyclerView != null){

                serList = modelo.listServicios;
                servicioAdapter = new ServicioAdapter(getActivity(), serList);
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(servicioAdapter);


            servicioAdapter.notifyDataSetChanged();

        }


        //mostrar pines
        //goolemapa(modelo.mMap);
        listaMapa();

    }


    @Override
    public void getClientes() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int listUsuario = modelo.listUsuario.size();
                Log.v("client size",  ": " +  listUsuario);

                if(listUsuario > 0){
                    listaMapa();
                }
            }
        },1000);




    }


    @Override
    public void errorClientes() {

    }

    public  void listaMapa(){
        int listUsuario = modelo.listUsuario.size();

        LatLng latlocal = new LatLng(modelo.latitud, modelo.longitud);// colombia

        if(listUsuario > 0) {

            PolylineOptions pop;

            // Initialize the SDK

            for (int i = 0; i < listUsuario; i++) {
                LatLng latclient = new LatLng(modelo.listUsuario.get(i).getLatitud(), modelo.listUsuario.get(i).getLongitud());
               // double dista = CalculationByDistance(latclient,latlocal );
                Location location = new Location("localizacion 1");
                location.setLatitude(modelo.latitud);  //latitud
                location.setLongitude(modelo.latitud); //longitud
                Location location2 = new Location("localizacion 2");
                location2.setLatitude(modelo.listUsuario.get(i).getLatitud());  //latitud
                location2.setLongitude(modelo.listUsuario.get(i).getLatitud()); //longitud
                double distance = location.distanceTo(location2);


                pop= new PolylineOptions();
                pop.width(5).color(Color.BLUE).geodesic(true);;
                pop.add(latlocal);
                pop.add(latclient);
                modelo.mMap.addPolyline(pop);

                modelo.listUsuario.get(i).setDistancia(distance);
                createMarker( modelo.listUsuario.get(i).getLatitud(), modelo.listUsuario.get(i).getLongitud(), modelo.listUsuario.get(i).getNombre(),modelo.mMap);
            }



        }
    }

    @Override
    public void cargoServicio() {
       /// alerta("Registro","Servicio registrada con exito");
        if (utility.estado(getActivity())) {
            comandoSercicio.getListServicio();
            comandoFavoritos.getListFavorito();

        }else{
            alerta("Sin Internet","Valide la conexión a internet");
        }

    }

    @Override
    public void errorServicio() {

    }

    @Override
    public void actualizarFavorito() {

    }



    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {


        int month= monthOfYear+1;
        String fm=""+month;
        String fd=""+dayOfMonth;
        if(month<10){
            fm ="0"+month;
        }
        if (dayOfMonth<10){
            fd="0"+dayOfMonth;
        }

        //String date = dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        String date= ""+fd+"/"+fm+"/"+year;
        //txtfecha .setText(""+date);

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
            goolemapa(modelo.mMap);
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


    public void alerta(String titulo, String descripcion){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance

                        sDialog.hide();
                    }
                })
                .show();
    }

    public void favoritos(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Advertencia")
                .setContentText("Desea agregar esta hubicación!")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance

                        if (utility.estado(getActivity())) {
                            comandoFavoritos.registarFavotito(search.getText().toString(),modelo.latitud, modelo.longitud);
                        }else{
                            alerta("Sin Internet","Valide la conexión a internet");
                        }
                        sDialog.hide();
                    }
                })
                .setCancelText("Cancelar")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.hide();
            }
        })
                .show();
    }


    public  void listaFavoritos(){
        Intent i = new Intent(getActivity(), ListaFavoritos.class);
        startActivity(i);

    }

    public void dialogmap(String direccion){

        modelo.modal = "searchdir";
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_map, null);


        //tabhost

        Resources res = getResources();

        TabHost tabs=(TabHost)mView.findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",
                res.getDrawable(R.drawable.tapubicaion));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("",
                res.getDrawable(R.drawable.tapservicio));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("",
                res.getDrawable(R.drawable.tapfavorito));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);



        //atributos modal - popup
        search2 = (SearchView) mView.findViewById(R.id.search_view);

        Button btnfavoritos = (Button) mView.findViewById(R.id.btnfavoritos);
        Button cerrar = (Button) mView.findViewById(R.id.cerrar);
         recyclerView = mView.findViewById(R.id.recycler_view);
        recyclerView2 = mView.findViewById(R.id.recycler_view2);
        LinearLayout layaut1 = mView.findViewById(R.id.layaut1);
        LinearLayout layaut2 = mView.findViewById(R.id.layaut2);


        //click tap
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i("AndroidTabsDemo", "Pulsada pestaña: " + tabId);
                //Toast.makeText(getActivity(),"Pulsada pestaña: " + tabId, Toast.LENGTH_SHORT).show();

                if(tabId.equals("mitab1")){
                    modelo.modal = "searchdir";
                    search2.setQuery("",false);
                }
                if(tabId.equals("mitab3")){
                    search2.setQuery("",false);
                    modelo.modal= "favoritos";
                    comandoFavoritos.getListFavorito();

                }

                else if(tabId.equals("mitab2")){
                    search2.setQuery("",false);
                    modelo.modal= "servicios";
                    comandoSercicio.getListServicio();

                }
            }
        });



        // search2.setQuery(direccion, false);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        /*btnservicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //onMapSearch();
                servicioAdapter.notifyDataSetChanged();
                    modelo.modal= "servicios";

                btnservicios.setBackgroundColor(R.drawable.fondo_post_border_style);
                btnfavoritos.setBackgroundResource(R.drawable.fondo_post_border_style);

                    //dialog.dismiss();
            }
        });



        btnfavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  // onMapSearch();

                favortoAdapter.notifyDataSetChanged();
                    modelo.modal= "favoritos";
                    comandoFavoritos.getListFavorito();
                btnfavoritos.setBackgroundColor(R.drawable.fondo_post_border_style);
                btnservicios.setBackgroundResource(R.drawable.fondo_post_border_style);

            }
        });*/


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        //Toast.makeText(getActivity(), "1:" + position,Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                        modelo.mMap.clear();
                        modelo.latitud = modelo.listServicios.get(position).getLatitud();
                        modelo.longitud = modelo.listServicios.get(position).getLongitud();
                        goolemapa(modelo.mMap);
                        listaMapa();


                        dialog.dismiss();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        recyclerView2.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        ///Toast.makeText(getActivity(), "2:" + position,Toast.LENGTH_SHORT).show();

                        modelo.latitud = modelo.listServicios.get(position).getLatitud();
                        modelo.longitud = modelo.listServicios.get(position).getLongitud();
                        goolemapa(modelo.mMap);

                        dialog.dismiss();

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




           search2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextSubmit(String queryString) {

                   if(modelo.modal.equals("searchdir")){
                       //search gogole map
                       // Initialize the SDK
                      if(!search2.getQuery().toString().equals("")){
                          Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

                          search2.setFocusable(false);

                          List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                          Intent intent = new Autocomplete.IntentBuilder(
                                  AutocompleteActivityMode.FULLSCREEN, fields)
                                  .build(getActivity());
                          startActivityForResult(intent, 110);
                      }
                   }
                   else if(modelo.modal.equals("servicios")){
                       servicioAdapter.getFilter().filter(queryString);
                   }else{
                       favortoAdapter.getFilter().filter(queryString);
                   }

                   return false;
               }

               @Override
               public boolean onQueryTextChange(String queryString) {

                   if(modelo.modal.equals("searchdir")){
                       //search gogole map
                       // Initialize the SDK
                       if(!search2.getQuery().toString().equals("")){
                           Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

                           search2.setFocusable(false);

                           List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                           Intent intent = new Autocomplete.IntentBuilder(
                                   AutocompleteActivityMode.FULLSCREEN, fields)
                                   .build(getActivity());
                           startActivityForResult(intent, 110);
                       }
                   }
                   else if(modelo.modal.equals("servicios")){
                       servicioAdapter.getFilter().filter(queryString);
                   }else{
                       favortoAdapter.getFilter().filter(queryString);
                   }
                   return false;
               }
           });

    }


    public void generarServicio(){

        //alert
        AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(getActivity());
        View mView2 = getLayoutInflater().inflate(R.layout.dialog_service, null);

        Button btnAcetar = (Button) mView2.findViewById(R.id.btnAcetar);
        Button btnCancelar = (Button) mView2.findViewById(R.id.btnCancelar);
        Button btntiposervicio = (Button) mView2.findViewById(R.id.btntiposervicio);
        EditText nombre_usuario = (EditText) mView2.findViewById(R.id.nombre_usuario);
        //txtfecha = (EditText) mView2.findViewById(R.id.txtfecha);
        txtfechainicio = (Button) mView2.findViewById(R.id.txtfechainicio);
        EditText txt_direccion = (EditText) mView2.findViewById(R.id.txt_direccion);
        txtfechafin = (Button) mView2.findViewById(R.id.txtfechafin);
        EditText infodireccion = (EditText) mView2.findViewById(R.id.infodireccion);
        EditText infoobservacione = (EditText) mView2.findViewById(R.id.infoobservacione);
        LinearLayout layputprecio = (LinearLayout) mView2.findViewById(R.id.layputprecio);
        TextView txtprecio = (TextView) mView2.findViewById(R.id.txtprecio);


        String direccionBusqueda = search.getText().toString();
        txt_direccion.setText(direccionBusqueda);
        //
        if(modelo.tipoLogin.equals("normal")){
            nombre_usuario.setText(modelo.usuario.getNombre() + " " + modelo.usuario.getApellido());
        }else{
            nombre_usuario.setText(user.getDisplayName());
        }


        //show
        mBuilder2.setView(mView2);
        final AlertDialog dialog2 = mBuilder2.create();



        //eventos

        //piker
        /*txtfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datepickerdialog = DatePickerDialog.newInstance(
                        (DatePickerDialog.OnDateSetListener) HomeFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datepickerdialog.setMinDate(now);
                datepickerdialog.setThemeDark(true); //set dark them for dialog?
                datepickerdialog.vibrate(true); //vibrate on choosing date?
                datepickerdialog.dismissOnPause(true); //dismiss dialog when onPause() called?
                datepickerdialog.showYearPickerFirst(false); //choose year first?
                datepickerdialog.setAccentColor(Color.parseColor("#6BC0DC")); // custom accent color
                datepickerdialog.setTitle("Selecione una fecha"); //dialog title
                datepickerdialog.show(getFragmentManager(), "Datepickerdialog"); //show dialog
            }
        });*/


        txtfechainicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 tiemposervicio ="ahora";
                txtfechainicio.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.fondo_post_border_style));
                txtfechainicio.setTextColor(getActivity().getResources().getColor(R.color.colorBlanco));

                txtfechafin.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edittext_style));
                txtfechafin.setTextColor(getActivity().getResources().getColor(R.color.colorFondo));
            }
        });


        txtfechafin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hora(view);

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("cerrar","cerrar");
                dialog2.dismiss();
            }
        });

        btnAcetar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //odenamos la lista
                Collections.sort(modelo.listUsuario, new Comparator<Usuario>() {
                    @Override
                    public int compare(Usuario c1, Usuario c2) {
                        return Double.compare(c1.getDistancia(), c2.getDistancia());
                    }
                });




                String fecha = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy", new java.util.Date()));

                String tipoServicio = btntiposervicio.getText().toString();
                String nombreServicio = nombre_usuario.getText().toString();
                //String fecha = txtfecha.getText().toString();
                String horanicio = txtfechainicio.getText().toString();
                String horaFin = txtfechafin.getText().toString();
                String direccion = txt_direccion.getText().toString();
                String informacionAdicional = infodireccion.getText().toString();
                String informaconObservaciones = infoobservacione.getText().toString();
                String nombreEnfermero =  modelo.usuario.getNombre();



                if(tipoServicio.equals("Seleccione el servicio") || nombreServicio.equals(" ") || tiemposervicio.equals("")|| informacionAdicional.equals("")|| informaconObservaciones.equals("")){
                    Toast.makeText(getActivity(),"Todos los campos son obligatorios", Toast.LENGTH_LONG).show();
                    return;
                }


                comandoSercicio.registarServicio(tipoServicio,fecha, tiemposervicio,direccion,informacionAdicional, informaconObservaciones,modelo.latitud, modelo.longitud,"",nombreEnfermero, txtprecio.getText().toString());
                btntiposervicio.setText("Seleccione el servicio");
                infodireccion.setText("");
                infoobservacione.setText("");

                int cont = 0;
                if (tiemposervicio.equals("ahora")){

                    for (int i = 0; i < modelo.listUsuario.size() ; i++) {
                        if( modelo.listUsuario.get(i).getDistancia() <= 400){
                            String token = modelo.listUsuario.get(i).getToken();
                            sendNotifications( token,"Servicio", "Hay un nuevo servicio");
                            cont = cont+1;
                        }
                    }
                }else{
                    if(cont == 0){
                        for (int i = 0; i < modelo.listUsuario.size() ; i++) {
                            sendNotifications(modelo.listUsuario.get(i).getToken(), "Servicio", "Hay un nuevo servicio");
                        }
                    }

                }
                dialog2.dismiss();

            }
        });

        btntiposervicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getActivity(), "1", Toast.LENGTH_LONG).show();

                final CharSequence[] items = new CharSequence[modelo.listTipoServicios.size()];
                final CharSequence[] items2 = new CharSequence[modelo.listTipoServicios.size()];
                ArrayList<String> servicioNames  = new ArrayList<String>();
                for (int i = 0; i < modelo.listTipoServicios.size() ; i++) {
                    items[i] = modelo.listTipoServicios.get(i).getNombre();
                    items2[i] = modelo.listTipoServicios.get(i).getPrecio().toString();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Seleccione el servicio");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                       // Toast.makeText(getActivity(),  ":"+item + items[item], Toast.LENGTH_LONG).show();
                        btntiposervicio.setText(items[item]);
                        layputprecio.setVisibility(View.VISIBLE);
                        txtprecio.setText(items2[item]);
                        dialog.dismiss();
                    }
                });
                builder.show();


            }
        });

        dialog2.show();

    }


    //__methode will be call when we click on "Custom Date Picker Dialog" and will be show the custom date selection dilog.
    public void customTimePickerDialog() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false);
        dpd.setAccentColor(getResources().getColor(R.color.colorVerdeOscuro));
        dpd.show(getFragmentManager(), "Timepickerdialog");
    }

    //___this is the listener callback method will be call on time selection by default date picker.
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        //Toast.makeText(this, "Selected by default time picker : " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);
        date=datetime.getTime();
        //Toast.makeText(this, "Selected by custom time picker : " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),""+hourFormat.format(date), Toast.LENGTH_LONG).show();

        //txtfecha.setText(""+hourFormat.format(date));

    }





    public void hora(View view) {

        switch (view.getId()) {
            case R.id.txtfechainicio:
                setHora = 1;
                customTimePickerDialog();
                break;

            case R.id.txtfechafin:
                setHora = 2;
                customTimePickerDialog();
                break;
        }
    }




    //___this is the listener callback method will be call on time selection by custom date picker.
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);
        date = datetime.getTime();
         Log.v("hourOfDay", "hourOfDay" + hourOfDay + "hra ..." + hourFormat.format(date));
        //Toast.makeText(this, "Selected by custom time picker : " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),""+hourFormat.format(date), Toast.LENGTH_LONG).show();


        String string = "" + hourFormat.format(date);
        String[] parts = string.split(" ");
        String part1 = parts[0];
        String part2 = parts[1];

        if (part2.equals("a.") || part2.equals("a.m.") || part2.equals("a. m.") || part2.equals("A.")) {
            part2 = "AM";
        }
        if (part2.equals("p.") || part2.equals("p.m.") || part2.equals("p. m.") || part2.equals("P.")) {
            part2 = "PM";
        }

        if (setHora == 1) {
            txtfechainicio.setText("" + part1 + " " + part2);

        }
        if (setHora == 2) {
            tiemposervicio="" + "" + part1 + " " + part2;
            txtfechafin.setText("" + "" + part1 + " " + part2);

            txtfechafin.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.fondo_post_border_style));
            txtfechafin.setTextColor(getActivity().getResources().getColor(R.color.colorBlanco));

            txtfechainicio.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edittext_style));
            txtfechainicio.setTextColor(getActivity().getResources().getColor(R.color.colorFondo));
        }

    }

    //calcular distancia
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 400;// radio de la tierra en  kilómetros
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 2) {

            //wen succes
            //Initialize plac3
            Place place = Autocomplete.getPlaceFromIntent(data);
            //set adres editTest
            search.setText(place.getAddress());
            //set location name
            String name = String.format("Locaty Name : %s", place.getName());
            // set Lat Long
            LatLng search = place.getLatLng();
        }
        else if(resultCode == AutocompleteActivity.RESULT_CANCELED){
                //when error
                //initialize status
            Status  status = Autocomplete.getStatusFromIntent(data);
            //display toas
            Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place.getLatLng() != null) {
                    // reverse geoCoding to get Street Address, city,state and postal code

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        System.out.println("------addressList-----" + place.getAddress() + "             " + place.getName());
                        List<Address> addressList = geocoder.getFromLocation(
                                place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        System.out.println("------addressList-----" + addressList);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            System.out.println("------address-----" + address);
                            search.setText(address.getAddressLine(0));
                            String featureName = "";
                            if (address.getFeatureName()!=null){
                                featureName = address.getFeatureName();
                            }
                            String throughFare = "";
                            if (address.getThoroughfare()!=null){
                                throughFare = address.getThoroughfare();
                            }
                            String streetAddress = featureName + " " + throughFare;
                            search.setText(streetAddress);
                            if (address.getLocality() != null) {
                                search.setText(address.getLocality());
                            } else {
                               // callGeoCodeAPI(place.getLatLng().latitude + "," + place.getLatLng().longitude);
                            }
                            //stateEd.setText(address.getAdminArea());
                            //postCodeEd.setText(address.getPostalCode());
                            //countryEd.setText(address.getCountryName());
                        }

                    } catch (IOException e) {
                        Log.e("TAG", "Unable connect to Geocoder", e);
                    }

                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                if (getActivity() != null) {
                   Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == 110) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place.getLatLng() != null) {
                    // reverse geoCoding to get Street Address, city,state and postal code

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        System.out.println("------addressList-----" + place.getAddress() + "             " + place.getName());
                        List<Address> addressList = geocoder.getFromLocation(
                                place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        System.out.println("------addressList-----" + addressList);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            System.out.println("------address-----" + address);
                            search2.setQuery(address.getAddressLine(0),false);
                            String featureName = "";
                            if (address.getFeatureName()!=null){
                                featureName = address.getFeatureName();
                            }
                            String throughFare = "";
                            if (address.getThoroughfare()!=null){
                                throughFare = address.getThoroughfare();
                            }
                            String streetAddress = featureName + " " + throughFare;
                            search2.setQuery(streetAddress,false);
                            if (address.getLocality() != null) {
                                search2.setQuery(address.getLocality(),false);
                            } else {
                                // callGeoCodeAPI(place.getLatLng().latitude + "," + place.getLatLng().longitude);
                            }
                            //stateEd.setText(address.getAdminArea());
                            //postCodeEd.setText(address.getPostalCode());
                            //countryEd.setText(address.getCountryName());
                        }

                    } catch (IOException e) {
                        Log.e("TAG", "Unable connect to Geocoder", e);
                    }

                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                if (getActivity() != null) {
                    search2.setQuery("",false);
                    Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
