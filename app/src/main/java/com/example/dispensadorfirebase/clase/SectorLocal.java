package com.example.dispensadorfirebase.clase;

import java.io.Serializable;

public class SectorLocal implements Serializable {
            int numeroatendiendo;
            int ultimoNumeroDispensador;
            int cantidadEspera;
            int limite;
            int numeroDispensador;
            String nombreSector;
            String ColorSector;
            int notificacion ;
            int notificaciondeshabilitar;
            int estado;
            int llamarsupervisor;
            String fondoSectorH;
            String fondoSectorV;
            String idsector;
            int variableNumero;
            String ultimaFecha;
            int variableNumeroTablet;

    public SectorLocal(int numeroatendiendo, int ultimoNumeroDispensador, int cantidadEspera, int limite, int numeroDispensador, String nombreSector, String colorSector, int notificacion, int notificaciondeshabilitar, int estado, int llamarsupervisor, String fondoSectorH, String fondoSectorV, String idsector, int variableNumero, String ultimaFecha, int variableNumeroTablet) {
        this.numeroatendiendo = numeroatendiendo;
        this.ultimoNumeroDispensador = ultimoNumeroDispensador;
        this.cantidadEspera = cantidadEspera;
        this.limite = limite;
        this.numeroDispensador = numeroDispensador;
        this.nombreSector = nombreSector;
        ColorSector = colorSector;
        this.notificacion = notificacion;
        this.notificaciondeshabilitar = notificaciondeshabilitar;
        this.estado = estado;
        this.llamarsupervisor = llamarsupervisor;
        this.fondoSectorH = fondoSectorH;
        this.fondoSectorV = fondoSectorV;
        this.idsector = idsector;
        this.variableNumero = variableNumero;
        this.ultimaFecha = ultimaFecha;
        this.variableNumeroTablet = variableNumeroTablet;
    }


    public int getVariableNumeroTablet() {
        return variableNumeroTablet;
    }

    public void setVariableNumeroTablet(int variableNumeroTablet) {
        this.variableNumeroTablet = variableNumeroTablet;
    }

    public String getUltimaFecha() {
        return ultimaFecha;
    }

    public void setUltimaFecha(String ultimaFecha) {
        this.ultimaFecha = ultimaFecha;
    }

    public int getVariableNumero() {
        return variableNumero;
    }

    public void setVariableNumero(int variableNumero) {
        this.variableNumero = variableNumero;
    }

    public int getNumeroatendiendo() {
        return numeroatendiendo;
    }

    public void setNumeroatendiendo(int numeroatendiendo) {
        this.numeroatendiendo = numeroatendiendo;
    }

    public int getUltimoNumeroDispensador() {
        return ultimoNumeroDispensador;
    }

    public void setUltimoNumeroDispensador(int ultimoNumeroDispensador) {
        this.ultimoNumeroDispensador = ultimoNumeroDispensador;
    }

    public int getCantidadEspera() {
        return cantidadEspera;
    }

    public void setCantidadEspera(int cantidadEspera) {
        this.cantidadEspera = cantidadEspera;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public int getNumeroDispensador() {
        return numeroDispensador;
    }

    public void setNumeroDispensador(int numeroDispensador) {
        this.numeroDispensador = numeroDispensador;
    }

    public String getNombreSector() {
        return nombreSector;
    }

    public void setNombreSector(String nombreSector) {
        this.nombreSector = nombreSector;
    }

    public String getColorSector() {
        return ColorSector;
    }

    public void setColorSector(String colorSector) {
        ColorSector = colorSector;
    }

    public int getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(int notificacion) {
        this.notificacion = notificacion;
    }

    public int getNotificaciondeshabilitar() {
        return notificaciondeshabilitar;
    }

    public void setNotificaciondeshabilitar(int notificaciondeshabilitar) {
        this.notificaciondeshabilitar = notificaciondeshabilitar;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getLlamarsupervisor() {
        return llamarsupervisor;
    }

    public void setLlamarsupervisor(int llamarsupervisor) {
        this.llamarsupervisor = llamarsupervisor;
    }

    public String getFondoSectorH() {
        return fondoSectorH;
    }

    public void setFondoSectorH(String fondoSectorH) {
        this.fondoSectorH = fondoSectorH;
    }

    public String getFondoSectorV() {
        return fondoSectorV;
    }

    public void setFondoSectorV(String fondoSectorV) {
        this.fondoSectorV = fondoSectorV;
    }

    public String getIdsector() {
        return idsector;
    }

    public void setIdsector(String idsector) {
        this.idsector = idsector;
    }



    public SectorLocal() {
    }

    public boolean sumarTablet(){

        boolean a = false;

        if (cantidadEspera>0){

            if (numeroatendiendo==99){

                this.numeroatendiendo = 1;
                this.cantidadEspera--;
                this.variableNumeroTablet++;

            }else {
                this.numeroatendiendo++;
                this.cantidadEspera--;

            }

            a = true;
        }

        return a;

    }

    public void sumarDispensdor(){

        if (numeroDispensador == 99){

            this.cantidadEspera++;
            this.numeroDispensador = 1;
            this.ultimoNumeroDispensador = 99;
            this.variableNumero++;
        }else{
            this.cantidadEspera++;
            this.numeroDispensador++;
            this.ultimoNumeroDispensador = numeroDispensador-1;
        }
    }


    public boolean restar(){

        boolean a = false;

        if (cantidadEspera<ultimoNumeroDispensador){

            if (numeroatendiendo==1){

                this.numeroatendiendo = 99;
                this.cantidadEspera++;
                this.variableNumeroTablet--;

            }else {
                this.numeroatendiendo--;
                this.cantidadEspera++;

            }
            a = true;
        }


        return a;


    }
    public void reset(){

        this.numeroatendiendo=0;
        this.cantidadEspera=0;
        this.ultimoNumeroDispensador = 0;
        this.numeroDispensador=1;
        this.notificacion=0;
        this.notificaciondeshabilitar=0;
        this.variableNumero++;
        this.variableNumeroTablet = variableNumero;
    }


}
