package com.enfermeraya.enfermeraya.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.enfermeraya.enfermeraya.R;
import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Favoritos;
import com.enfermeraya.enfermeraya.clases.Servicios;
import com.enfermeraya.enfermeraya.comandos.ComandoFavoritos;
import com.enfermeraya.enfermeraya.comandos.ComandoSercicio;
import com.enfermeraya.enfermeraya.dapter.FavoritoAdapter;
import com.enfermeraya.enfermeraya.dapter.ServicioAdapter;
import com.enfermeraya.enfermeraya.models.utility.Utility;
import com.enfermeraya.enfermeraya.views.ListaFavoritos;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;



public class HomeFragment extends Fragment implements
        OnMapReadyCallback, ComandoFavoritos.OnFavoritosChangeListener, ComandoSercicio.OnSercicioChangeListener
        , TimePickerDialog.OnTimeSetListener, android.app.TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{




    private HomeViewModel homeViewModel;
    Modelo modelo = Modelo.getInstance();
    Geocoder geocoder = null;
    private GoogleMap mMap;
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

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //adapter
    private List<Favoritos> favList = new ArrayList<>();
    private List<Servicios> serList = new ArrayList<>();

    //datepiker
    DatePickerDialog datePickerDialog;
    Date date;
    DateFormat hourFormat;
    EditText txtfecha;
    EditText txtfechainicio;
    EditText txtfechafin;
    int setHora = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        markerOptions = new MarkerOptions();
        mapFragment.getMapAsync(this::goolemapa);

        comandoFavoritos = new ComandoFavoritos(this);
        comandoSercicio = new ComandoSercicio(this);
        utility = new Utility();
        search = (EditText)root.findViewById(R.id.search);
        imgsearc = (ImageView) root.findViewById(R.id.imgsearc);
        btn_servicio = (Button) root.findViewById(R.id.btn_servicio);


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
                    if(search.getText().toString().equals("")){
                        Toast.makeText(getActivity(), "Ingrese una dirección", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    generarServicio();
                }else{
                    alerta("Sin Internet","Valide la conexión a internet");
                }
            }
        });


        if (utility.estado(getActivity())) {
            comandoFavoritos.getListFavorito();
            comandoSercicio.getListServicio();
            comandoSercicio.getTipoServicio();
        }else{
            alerta("Sin Internet","Valide la conexión a internet");
        }


        date = new Date();
        hourFormat = new SimpleDateFormat("hh:mm aa");
        System.out.println("Hora: " + hourFormat.format(date));




        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        modelo.mMap = googleMap;
        goolemapa(modelo.mMap );


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
        marcadorImg(modelo.latitud, modelo.longitud,"Pais Colombia",mMap);
        //setLocation();

    }

    private void marcadorColor(double lat, double lng, String  pais, float color, GoogleMap mMap){
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(pais).icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

    private void marcadorImg(double lat, double lng, String  pais, GoogleMap mMap){
        modelo.mMap = mMap;
     //   MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng)).title("Enfermera");

        LatLng  latLng = new LatLng(lat,lng);

        mMap.clear();
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



            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                   // Toast.makeText(getActivity(), "datos"+latLng, Toast.LENGTH_SHORT).show();
                    modelo.latitud = latLng.latitude;
                    modelo.longitud = latLng.longitude;
                    goolemapa(modelo.mMap);
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
            Toast.makeText(getActivity().getApplicationContext(),total_str, Toast.LENGTH_LONG);
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

    @Override
    public void getFavorito() {

        //Toast.makeText(getActivity(), ""+modelo.listFavoritos.size(), Toast.LENGTH_SHORT).show();
        favList = modelo.listFavoritos;

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
        serList = modelo.listServicios;
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
        txtfecha .setText(""+date);

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

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_map, null);


        //tabhost

        Resources res = getResources();

        TabHost tabs=(TabHost)mView.findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",
                res.getDrawable(R.drawable.gpsimg));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("",
                res.getDrawable(android.R.drawable.btn_star_big_off));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("",
                res.getDrawable(android.R.drawable.btn_star_big_off));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);



        //atributos modal - popup
        final SearchView search2 = (SearchView) mView.findViewById(R.id.search_view);

        Button btnfavoritos = (Button) mView.findViewById(R.id.btnfavoritos);
        Button cerrar = (Button) mView.findViewById(R.id.cerrar);
        RecyclerView recyclerView = mView.findViewById(R.id.recycler_view);
        RecyclerView recyclerView2 = mView.findViewById(R.id.recycler_view2);
        LinearLayout layaut1 = mView.findViewById(R.id.layaut1);
        LinearLayout layaut2 = mView.findViewById(R.id.layaut2);


        servicioAdapter = new ServicioAdapter(getActivity(), serList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(servicioAdapter);


        favortoAdapter = new FavoritoAdapter(getActivity(), favList);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(favortoAdapter);

        //click tap
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i("AndroidTabsDemo", "Pulsada pestaña: " + tabId);
                Toast.makeText(getActivity(),"Pulsada pestaña: " + tabId, Toast.LENGTH_SHORT).show();

                if(tabId.equals("mitab3")){
                    favortoAdapter.notifyDataSetChanged();
                    modelo.modal= "favoritos";
                    comandoFavoritos.getListFavorito();
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

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




           search2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextSubmit(String queryString) {

                   if(modelo.modal.equals("servicios")){
                       servicioAdapter.getFilter().filter(queryString);
                   }else{
                       favortoAdapter.getFilter().filter(queryString);
                   }

                   return false;
               }

               @Override
               public boolean onQueryTextChange(String queryString) {
                   if(modelo.modal.equals("servicios")){
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
        txtfecha = (EditText) mView2.findViewById(R.id.txtfecha);
        txtfechainicio = (EditText) mView2.findViewById(R.id.txtfechainicio);
        txtfechafin = (EditText) mView2.findViewById(R.id.txtfechafin);
        EditText infodireccion = (EditText) mView2.findViewById(R.id.infodireccion);
        EditText infoobservacione = (EditText) mView2.findViewById(R.id.infoobservacione);


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
        txtfecha.setOnClickListener(new View.OnClickListener() {
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
                datepickerdialog.show(getActivity().getFragmentManager(), "Datepickerdialog"); //show dialog
            }
        });


        txtfechainicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hora(view);
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
                comandoSercicio.registarServicio(search.getText().toString(), modelo.latitud, modelo.longitud);
                infodireccion.setText("");
                infoobservacione.setText("");
                dialog2.dismiss();
            }
        });

        btntiposervicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "1", Toast.LENGTH_LONG).show();
            }
        });

        dialog2.show();

    }


    //__methode will be call when we click on "Custom Date Picker Dialog" and will be show the custom date selection dilog.
    public void customTimePickerDialog() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false);
        dpd.setAccentColor(getResources().getColor(R.color.colorVerdeOscuro));
        dpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
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
            txtfechafin.setText("" + "" + part1 + " " + part2);

        }

    }




}
