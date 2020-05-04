package com.enfermeraya.enfermeraya.models.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Utility {


   /*validar conexión a inernet*/

    //validacion conecion

   public Boolean estado(Context context){
       boolean res = false;
       if (conectadoWifi(context) == true) {
           res = true;
       }else if(conectadoRedMovil(context)){
           res =true;
       }
       return res;

    }


    //conexion wifi
    protected Boolean conectadoWifi(Context context){

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    //conexion movil
    protected Boolean conectadoRedMovil(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    //validacion conecion a internet



    public static String convertDateToString(Date indate)
    {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy");

        try{
            dateString = sdfr.format( indate );
        }catch (Exception ex ){
            System.out.println(ex);
        }
        return dateString;
    }






    //validar correo
    public boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }




}
