package com.enfermeraya.enfermeraya.comandos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComandoTarjeta {


    public interface OnTarjeta {
        void oktarjeta();
        void errortarjeta();
    }


    public static void getTarjeta(String fourTarjeta, final OnTarjeta listener){

        listener.oktarjeta();

    }



}

