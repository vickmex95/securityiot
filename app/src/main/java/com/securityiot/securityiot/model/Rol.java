package com.securityiot.securityiot.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rol{

    private String nombre;


    public Rol(String nombre) {
        this.nombre = nombre;
    }

    public Rol() {

    }

    protected Rol(Parcel in) {
        nombre = in.readString();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String title) {
        this.nombre = nombre;
    }



}


