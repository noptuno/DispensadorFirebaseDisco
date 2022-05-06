package com.example.dispensadorfirebase.clase;


import java.util.List;


public class SectorHistorico {
    String cliente;
    String local;
    String sector;
    int ticket;
    //dispensador->
    String fecha_entrega;
    String hora_entrega;
    //tablet->
    String fecha_atencion;
    String hora_atencion;


    public SectorHistorico() {

    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
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

    public SectorHistorico(String cliente, String local, String sector, int ticket, String fecha_entrega, String hora_entrega, String fecha_atencion, String hora_atencion) {
        this.cliente = cliente;
        this.local = local;
        this.sector = sector;
        this.ticket = ticket;
        this.fecha_entrega = fecha_entrega;
        this.hora_entrega = hora_entrega;
        this.fecha_atencion = fecha_atencion;
        this.hora_atencion = hora_atencion;
    }
}
