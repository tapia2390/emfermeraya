package com.enfermeraya.enfermeraya.comandos;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.TipoServicio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ComandoObternerDatosEnfermera {


    Modelo modelo = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference referencia = database.getReference();
    DataSnapshot dataSnapshot;



    //interface del listener de la actividad interesada
    private ComandoObternerDatosEnfermera.OnSercicioChangeListener mListener;

    /**
     * Interfaz para avisar de eventos a los interesados
     */
    public interface OnSercicioChangeListener {


        void getTipoServicio();

    }

    public ComandoObternerDatosEnfermera(ComandoObternerDatosEnfermera.OnSercicioChangeListener mListener){

        this.mListener = mListener;

    }


    public void  getTipoServicio(){
        //preguntasFrecuentes
        modelo.listTipoServicios.clear();
        DatabaseReference ref = database.getReference("Servicios/");//ruta path

        ChildEventListener listener = new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot snFav, String s) {

                TipoServicio ser = new TipoServicio();
                ser.setKey(snFav.getKey());
                ser.setNombre(snFav.child("Nombre").getValue().toString());
                Long precio=  (Long) snFav.child("precio").getValue();

                ser.setPrecio(precio);
                modelo.listTipoServicios.add(ser);
                mListener.getTipoServicio();

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


    /**
     * Para evitar nullpointerExeptions
     */
    private static ComandoObternerDatosEnfermera.OnSercicioChangeListener sDummyCallbacks = new ComandoObternerDatosEnfermera.OnSercicioChangeListener()
    {

        @Override
        public void getTipoServicio() {}

    };
}
