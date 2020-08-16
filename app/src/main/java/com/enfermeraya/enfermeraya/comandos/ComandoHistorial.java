package com.enfermeraya.enfermeraya.comandos;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Historial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ComandoHistorial {


    Modelo modelo = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference referencia = database.getReference();
    DataSnapshot dataSnapshot;



    //interface del listener de la actividad interesada
    private OnHistorialChangeListener mListener;


    /**
     * Interfaz para avisar de eventos a los interesados
     */
    public interface OnHistorialChangeListener {


        void errorHistorial();
        void okHistorial();

    }

    public ComandoHistorial(OnHistorialChangeListener mListener){

        this.mListener = mListener;

    }






    public void  getListaHistorico(){
        //preguntasFrecuentes
        modelo.listHistorial.clear();
        DatabaseReference ref = database.getReference("historial");//ruta path
        Query query = ref.orderByChild("uid").equalTo(modelo.uid);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snFav, @Nullable String s) {
                Historial ser = new Historial();
                Long timestamp =  (Long) snFav.child("timestamp").getValue();
                ser.setKey(snFav.getKey());

                ser.setTimestamp(timestamp);

                double lattitud = (double)snFav.child("latitud").getValue();
                double longitud = (double)snFav.child("longitud").getValue();

                if(snFav.child("calificacion").exists()){
                    double calificacion = (double)snFav.child("calificacion").getValue();
                    ser.setCalificaion(calificacion);

                }else{
                    ser.setCalificaion(0.0);

                }

                ser.setLatitud(lattitud);
                ser.setLongitud(longitud);

                ser.setTipoServicio(snFav.child("tipoServicio").getValue().toString());
                ser.setFecha(snFav.child("fecha").getValue().toString());
                ser.setHoraServicio(snFav.child("horaServicio").getValue().toString());

                ser.setDireccion(snFav.child("direccion").getValue().toString());
                ser.setInformacion(snFav.child("informacion").getValue().toString());
                ser.setObsciones(snFav.child("obsciones").getValue().toString());


                ser.setTitulo(snFav.child("titulo").getValue().toString());
                ser.setEstado(snFav.child("estado").getValue().toString());
                ser.setUid(snFav.child("uid").getValue().toString());
                ser.setToken(snFav.child("token").getValue().toString());
                ser.setObservacionesEnfermero(snFav.child("observacionesEnfermero").getValue().toString());
                ser.setMedicamentosAsignados(snFav.child("medicamentosAsignados").getValue().toString());
                ser.setFoto(snFav.child("foto").getValue().toString());
                modelo.listHistorial.add(ser);

                mListener.okHistorial();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void calificar(String uidServicio, double clificaion){

        final DatabaseReference ref = database.getReference("historial/"+uidServicio+"/calificacion/");//ruta path
        ref.setValue(clificaion);
        mListener.okHistorial();
    }


    /**
     * Para evitar nullpointerExeptions
     */
    private static OnHistorialChangeListener sDummyCallbacks = new OnHistorialChangeListener()
    {

        @Override
        public void okHistorial() {}

        @Override
        public void errorHistorial() {}

    };
}
