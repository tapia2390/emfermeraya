package com.enfermeraya.enfermeraya.clases;

public class Favoritos {
    String key;
    String direccion;
    double latitud;
    double longitud;
    String estado;
    long timestamp;

    public Favoritos(){}
    public Favoritos(String key, String direccion, double latitud, double longitud, String estado, long timestamp) {
        this.key = key;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.timestamp = timestamp;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
