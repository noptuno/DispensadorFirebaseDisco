package com.example.dispensadorfirebase.clase;


import java.util.List;


public class SectorHistorico {

    String idSector;
    String nombreSector;
    String idDispositivo;
    String nombreDispositivo;
    int numeroDispensado;
    String fecha_entrega;
    String hora_entrega;
    String fecha_atencion;
    String hora_atencion;
    String idLocal;
    int limite_superado;

    public SectorHistorico(String idSector, String nombreSector, String idDispositivo, String nombreDispositivo, int numeroDispensado, String fecha_entrega, String hora_entrega, String fecha_atencion, String hora_atencion, String idLocal, int limite_superado) {
        this.idSector = idSector;
        this.nombreSector = nombreSector;
        this.idDispositivo = idDispositivo;
        this.nombreDispositivo = nombreDispositivo;
        this.numeroDispensado = numeroDispensado;
        this.fecha_entrega = fecha_entrega;
        this.hora_entrega = hora_entrega;
        this.fecha_atencion = fecha_atencion;
        this.hora_atencion = hora_atencion;
        this.idLocal = idLocal;
        this.limite_superado = limite_superado;
    }

    public String getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public int getLimite_superado() {
        return limite_superado;
    }

    public void setLimite_superado(int limite_superado) {
        this.limite_superado = limite_superado;
    }

    public SectorHistorico() {
    }



    public String getIdSector() {
        return idSector;
    }

    public void setIdSector(String idSector) {
        this.idSector = idSector;
    }

    public String getNombreSector() {
        return nombreSector;
    }

    public void setNombreSector(String nombreSector) {
        this.nombreSector = nombreSector;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public int getNumeroDispensado() {
        return numeroDispensado;
    }

    public void setNumeroDispensado(int numeroDispensado) {
        this.numeroDispensado = numeroDispensado;
    }

    public String getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(String fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public String getHora_entrega() {
        return hora_entrega;
    }

    public void setHora_entrega(String hora_entrega) {
        this.hora_entrega = hora_entrega;
    }

    public String getFecha_atencion() {
        return fecha_atencion;
    }

    public void setFecha_atencion(String fecha_atencion) {
        this.fecha_atencion = fecha_atencion;
    }

    public String getHora_atencion() {
        return hora_atencion;
    }

    public void setHora_atencion(String hora_atencion) {
        this.hora_atencion = hora_atencion;
    }
}
