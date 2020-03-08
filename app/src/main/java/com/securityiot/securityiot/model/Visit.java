package com.securityiot.securityiot.model;

public class Visit {

    String visitId;

    String fechaVisita;

    String nombreVisitante;

    boolean entro;

    public Visit(String visitId, String fechaVisita, String nombreVisitante, boolean entro) {
        this.visitId = visitId;
        this.fechaVisita = fechaVisita;
        this.nombreVisitante = nombreVisitante;
        this.entro = entro;
    }

    public Visit(){

    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getNombreVisitante() {
        return nombreVisitante;
    }

    public void setNombreVisitante(String nombreVisitante) {
        this.nombreVisitante = nombreVisitante;
    }

    public String getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(String fechaVisita) {
        this.fechaVisita = fechaVisita;
    }



    public boolean isEntro() {
        return entro;
    }

    public void setEntro(boolean entro) {
        this.entro = entro;
    }
}
