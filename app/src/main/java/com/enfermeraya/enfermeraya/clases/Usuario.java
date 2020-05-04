package com.enfermeraya.enfermeraya.clases;

public class Usuario {

    private  String key;
    private  String tipo;
    private  String estado;
    private  String empresa;
    private  String nombre;
    private  String apellido;
    private  String celular;
    private  String correo;
    private  String codigoproveedor;
    private  String dni;

    public Usuario(){}


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }


    public String getCodigoproveedor() {
        return codigoproveedor;
    }

    public void setCodigoproveedor(String codigoproveedor) {
        this.codigoproveedor = codigoproveedor;
    }


    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
