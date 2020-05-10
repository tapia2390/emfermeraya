package com.enfermeraya.enfermeraya.comandos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.enfermeraya.enfermeraya.clases.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComandoPerfil {


    Modelo modelo = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference referencia = database.getReference();



    //interface del listener de la actividad interesada
    private ComandoPerfil.OnPerfilChangeListener mListener;

    /**
     * Interfaz para avisar de eventos a los interesados
     */
    public interface OnPerfilChangeListener {


        void cargoUSuario();
        void setUsuarioListener();
        void errorSetUsuario();

    }

    public ComandoPerfil(ComandoPerfil.OnPerfilChangeListener mListener){

        this.mListener = mListener;

    }

    public  void actualizarUsuario(Usuario u){
        //creating a new user
        //creating a new user


        //String uid = mAuth.getCurrentUser().getUid();

        final DatabaseReference ref = database.getReference("usuario/"+modelo.uid+"/");//ruta path

        Map<String, Object> enviarRegistroUsuario = new HashMap<String, Object>();

        enviarRegistroUsuario.put("nombre", u.getNombre());
        enviarRegistroUsuario.put("apellido", u.getApellido());
        enviarRegistroUsuario.put("celular", u.getCelular());

        if(!u.getFoto().equals("")){
            enviarRegistroUsuario.put("foto", u.getFoto());
        }


        ref.updateChildren(enviarRegistroUsuario, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {

                    mListener.setUsuarioListener();
                    return;
                } else {
                    mListener.errorSetUsuario();
                }
            }
        });



    }



    public void getUsuario(){
        //metodo que me trae la razon social
        //se guarda en clase modelo

            DatabaseReference ref = database.getReference("usuario/" + modelo.uid);//ruta path
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snap) {

                    modelo.usuario.setNombre(snap.child("nombre").getValue().toString());
                    modelo.usuario.setApellido(snap.child("apellido").getValue().toString());
                    modelo.usuario.setCelular(snap.child("celular").getValue().toString());
                    modelo.usuario.setCorreo(snap.child("correo").getValue().toString());
                    modelo.usuario.setFoto(snap.child("foto").getValue().toString());
                    modelo.usuario.setToken(snap.child("tokem").getValue().toString());


                    mListener.cargoUSuario();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v("Error :X",""+databaseError.getMessage());

                    mListener.setUsuarioListener();
                }
            });


    }





    /**
     * Para evitar nullpointerExeptions
     */
    private static ComandoPerfil.OnPerfilChangeListener sDummyCallbacks = new ComandoPerfil.OnPerfilChangeListener()
    {

        @Override
        public void cargoUSuario()
        {}

        @Override
        public void setUsuarioListener()
        {}

        @Override
        public void errorSetUsuario()
        {}


    };
}

