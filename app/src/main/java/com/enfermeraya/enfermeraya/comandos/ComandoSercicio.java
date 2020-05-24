package com.enfermeraya.enfermeraya.comandos;

import androidx.annotation.NonNull;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Favoritos;
import com.enfermeraya.enfermeraya.clases.Servicios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComandoSercicio {


    Modelo modelo = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference referencia = database.getReference();
    DataSnapshot dataSnapshot;



    //interface del listener de la actividad interesada
    private ComandoSercicio.OnSercicioChangeListener mListener;

    /**
     * Interfaz para avisar de eventos a los interesados
     */
    public interface OnSercicioChangeListener {


        void getServicio();
        void cargoServicio();
        void errorServicio();
        void actualizarFavorito();

    }

    public ComandoSercicio(ComandoSercicio.OnSercicioChangeListener mListener){

        this.mListener = mListener;

    }

    public  void registarServicio(String direccion, double lat , double longi){
        //creating a new user
        //creating a new user


        //String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference memoReference = FirebaseDatabase.getInstance().getReference();
        String key = memoReference.push().getKey();
        final DatabaseReference ref = database.getReference("usuario/"+modelo.uid+"/servicios/"+key);//ruta path


        Map<String, Object> favorito = new HashMap<String, Object>();


        favorito.put("direccion", direccion);
        favorito.put("latitud", lat);
        favorito.put("longitud", longi);
        favorito.put("timestamp", ServerValue.TIMESTAMP);
        favorito.put("estado", "false");




        ref.setValue(favorito, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {

                    mListener.cargoServicio();
                    return;
                } else {
                    mListener.errorServicio();
                }
            }
        });



    }



    public void  getListServicio(){
        //preguntasFrecuentes
        modelo.listServicios.clear();
        DatabaseReference ref = database.getReference("usuario/"+modelo.uid+"/servicios/");//ruta path

        ChildEventListener listener = new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot snFav, String s) {

                Servicios ser = new Servicios();
                Long timestamp =  (Long) snFav.child("timestamp").getValue();
                ser.setKey(snFav.getKey());

                ser.setTimestamp(timestamp);
                ser.setDireccion(snFav.child("direccion").getValue().toString());
                ser.setEstado(snFav.child("estado").getValue().toString());
                modelo.listServicios.add(ser);

                mListener.getServicio();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ref.addChildEventListener(listener);


    }




    public void updateFavorito(String estado, String key){

        final DatabaseReference ref = database.getReference("usuario/"+modelo.uid+"/servicios/"+key+ "/estado/");//ruta path
        ref.addListenerForSingleValueEvent(new ValueEventListener() {//addListenerForSingleValueEvent no queda escuchando peticiones
            @Override
            public void onDataChange(DataSnapshot snap) {

                ref.setValue(estado);
                mListener.actualizarFavorito();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }





    /**
     * Para evitar nullpointerExeptions
     */
    private static ComandoSercicio.OnSercicioChangeListener sDummyCallbacks = new ComandoSercicio.OnSercicioChangeListener()
    {

        @Override
        public void cargoServicio()
        {}


        @Override
        public void errorServicio()
        {}

        @Override
        public void getServicio()
        {}

    @Override
    public void actualizarFavorito()
        {}

    };
}
