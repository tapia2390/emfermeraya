package com.enfermeraya.enfermeraya.app;


import com.enfermeraya.enfermeraya.clases.ClassTerminosYCondiciones;
import com.enfermeraya.enfermeraya.clases.Usuario;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Modelo {
    public static final Modelo ourInstance = new Modelo();
    public String uid = "";
    public String tipo ="";
    public String terminosycondiciones = "";

    public static Modelo getInstance() {
        return ourInstance;
    }

    public Modelo() {
    }

    public ClassTerminosYCondiciones classTerminosYCondiciones = new ClassTerminosYCondiciones();


    public Usuario usuario = new Usuario();
    public String tipoLogin = "";


    //version app
    public String versionapp ="";
    public DecimalFormat decimales = new DecimalFormat("#.##");

    //datos date

    //Date date = new Date();
    //Caso 1: obtener la hora y salida por pantalla con formato:
    public DateFormat hourFormat = new SimpleDateFormat("hh:mm a");
    //System.out.println("Hora: "+hourFormat.format(date));
    //Caso 2: obtener la fecha y salida por pantalla con formato:
    public DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    //System.out.println("Fecha: "+dateFormat.format(date));
    //Caso 3: obtenerhora y fecha y salida por pantalla con formato:
    public DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    //System.out.println("Hora y fecha: "+hourdateFormat.format(date));



    //Devuelve true si la cadena que llega es un numero decimal, false en caso contrario
    public boolean esDecimal(String cad)
    {
        try
        {
            Double.parseDouble(cad);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }



}
