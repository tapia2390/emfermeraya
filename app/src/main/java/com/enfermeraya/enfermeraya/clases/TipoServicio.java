package com.enfermeraya.enfermeraya.clases;

public class TipoServicio {

    String key;
    String nombre;
    Long precio;


    public TipoServicio() {
    }

    public TipoServicio(String key, String nombre, Long precio) {
        this.key = key;
        this.nombre = nombre;
        this.precio =precio;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }
}
