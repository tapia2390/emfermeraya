package com.enfermeraya.enfermeraya.comandos;

import android.util.Log;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ComandoFavoritos {


    Modelo modelo = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference referencia = database.getReference();
    DataSnapshot dataSnapshot;



    //interface del listener de la actividad interesada
    private ComandoFavoritos.OnFavoritosChangeListener mListener;

    /**
     * Interfaz para avisar de eventos a los interesados
     */
    public interface OnFavoritosChangeListener {


        void cargoFavorito();
        void errorFavorito();

    }

    public ComandoFavoritos(ComandoFavoritos.OnFavoritosChangeListener mListener){

        this.mListener = mListener;

    }

    public  void registarFavotito(String direccion, double lat , double longi){
        //creating a new user
        //creating a new user


        //String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference memoReference = FirebaseDatabase.getInstance().getReference();
        String key = memoReference.push().getKey();
        final DatabaseReference ref = database.getReference("usuario/"+modelo.uid+"/favoritos/"+key);//ruta path


        Map<String, Object> favorito = new HashMap<String, Object>();

        favorito.put("direccion", direccion);
        favorito.put("latitud", lat);
        favorito.put("longitud", longi);




        ref.setValue(favorito, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {

                    mListener.cargoFavorito();
                    return;
                } else {
                    mListener.errorFavorito();
                }
            }
        });



    }



    public void getFavoritos(){

    }





    /**
     * Para evitar nullpointerExeptions
     */
    private static ComandoFavoritos.OnFavoritosChangeListener sDummyCallbacks = new ComandoFavoritos.OnFavoritosChangeListener()
    {

        @Override
        public void cargoFavorito()
        {}


        @Override
        public void errorFavorito()
        {}


    };
}
