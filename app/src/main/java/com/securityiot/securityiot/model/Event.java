package com.securityiot.securityiot.model;

import java.sql.Timestamp;
import java.util.List;

public class Event {

    private String eventId;

    private String fechaCreacion;

    private String nombre;

    private int codigoEvento;

    public Event(String eventId, String fechaCreacion, String nombre, int codigoEvento) {
        this.eventId = eventId;
        this.fechaCreacion = fechaCreacion;
        this.nombre = nombre;
        this.codigoEvento = codigoEvento;
    }


    public Event() {

    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(int codigoEvento) {
        this.codigoEvento = codigoEvento;
    }
}
