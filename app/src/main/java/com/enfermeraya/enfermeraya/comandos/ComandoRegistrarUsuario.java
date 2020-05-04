package com.enfermeraya.enfermeraya.comandos;

import com.enfermeraya.enfermeraya.app.Modelo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComandoRegistrarUsuario {

    Modelo modelo = Modelo.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference referencia = database.getReference();

    public interface OnRegistrarUsuarioChangeListener {

        void cargoRegistrarUsuario();
        void errorcargoRegistrarUsuario();

    }


    //interface del listener de la actividad interesada
    private OnRegistrarUsuarioChangeListener mListener;

    public ComandoRegistrarUsuario(OnRegistrarUsuarioChangeListener mListener){

        this.mListener = mListener;

    }


    //enviar datos a firebase registro usuario
    public  void enviarRegistrarUsuario(final String nombre, final String apellido, final String celular, final String correo, final String tokenDevice,final String uid ,final String nit ,final String tipo,final String codigoProveedor, final String dni){
        DatabaseReference key = referencia.push();

        final DatabaseReference ref = database.getReference("usuarios/"+uid+"/" );//ruta path
        ref.addListenerForSingleValueEvent(new ValueEventListener() {//addListenerForSingleValueEvent no queda escuchando peticiones
            @Override
            public void onDataChange(DataSnapshot snap) {


                Date date = new Date();
                Map<String, Object> registrarusuario = new HashMap<String, Object>();

                registrarusuario.put("empresa", nit);
                registrarusuario.put("tipo", tipo);
                registrarusuario.put("estado", "registrado");
                registrarusuario.put("fechaCreacion", ""+modelo.dateFormat.format(date));
                registrarusuario.put("fechaUltimoLogeo", ""+modelo.dateFormat.format(date));
                registrarusuario.put("horaUltimoLogeo", ""+modelo.hourFormat.format(date));
                registrarusuario.put("modoIngreso", "correo");
                registrarusuario.put("systemDevice", "Android" );
                registrarusuario.put("version", modelo.versionapp);
                registrarusuario.put("apellido", apellido);
                registrarusuario.put("celular", celular );
                registrarusuario.put("correo", correo );
                registrarusuario.put("nombre", nombre);
                registrarusuario.put("dni", dni);
                registrarusuario.put("codigoProveedor", codigoProveedor);
                registrarusuario.put("tokenDevice", tokenDevice);
                registrarusuario.put("timestamp", ServerValue.TIMESTAMP);



                ref.setValue(registrarusuario, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {

                            mListener.cargoRegistrarUsuario();
                            return;
                        } else {
                            mListener.errorcargoRegistrarUsuario();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    /**
     * Para evitar nullpointerExeptions
     */
    private static OnRegistrarUsuarioChangeListener sDummyCallbacks = new OnRegistrarUsuarioChangeListener()
    {
        @Override
        public void cargoRegistrarUsuario()
        {}


        @Override
        public void errorcargoRegistrarUsuario()
        {}

    };


}
