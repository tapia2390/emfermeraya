package com.enfermeraya.enfermeraya.comandos;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Servicios;
import com.enfermeraya.enfermeraya.clases.TipoServicio;
import com.enfermeraya.enfermeraya.clases.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ComandoEnfermeroPrestadorSer {


    Modelo modelo = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    //interface del listener de la actividad interesada
    private ComandoEnfermeroPrestadorSer.OnSercicioChangeListener mListener;

    /**
     * Interfaz para avisar de eventos a los interesados
     */
    public interface OnSercicioChangeListener {



        void getClientes();
        void errorClientes();


    }

    public ComandoEnfermeroPrestadorSer(ComandoEnfermeroPrestadorSer.OnSercicioChangeListener mListener){

        this.mListener = mListener;

    }




    public void  getListClient(){
        //preguntasFrecuentes
        modelo.listUsuario.clear();

        DatabaseReference ref = database.getReference("cliente/");//ruta path

        ChildEventListener listener = new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot snFav, String s) {

                Usuario user = new Usuario();

                double lattitud = (double)snFav.child("lat").getValue();
                double longitud = (double)snFav.child("long").getValue();

                user.setLatitud(lattitud);
                user.setLongitud(longitud);

                user.setNombre(snFav.child("nombre").getValue().toString());
                user.setApellido(snFav.child("apellido").getValue().toString());
                user.setFoto(snFav.child("foto").getValue().toString());
                user.setCelular(snFav.child("celular").getValue().toString());
                 modelo.listUsuario.add(user);

                mListener.getClientes();

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
    private static ComandoEnfermeroPrestadorSer.OnSercicioChangeListener sDummyCallbacks = new ComandoEnfermeroPrestadorSer.OnSercicioChangeListener()
    {

        @Override
        public void getClientes() {}

        @Override
        public void errorClientes() {}

    };
}
