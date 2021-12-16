package com.example.dispensadorfirebase.clase;

public class Local {

    String NombreLocal;
    int NumeroLocal;
    String EstadoLocal;

    public Local() {
    }

    public String getEstadoLocal() {
        return EstadoLocal;
    }

    public void setEstadoLocal(String estadoLocal) {
        EstadoLocal = estadoLocal;
    }

    public Local(String nombreLocal, int numeroLocal, String estadoLocal) {
        NombreLocal = nombreLocal;
        NumeroLocal = numeroLocal;
        EstadoLocal = estadoLocal;
    }

    public String getNombreLocal() {
        return NombreLocal;
    }

    public void setNombreLocal(String nombreLocal) {
        NombreLocal = nombreLocal;
    }

    public int getNumeroLocal() {
        return NumeroLocal;
    }

    public void setNumeroLocal(int numeroLocal) {
        NumeroLocal = numeroLocal;
    }

}
