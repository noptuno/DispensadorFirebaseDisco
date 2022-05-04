package com.example.dispensadorfirebase.clase;

public class Local {

    String NombreLocal;
    int NumeroLocal;
    String EstadoLocal;
    String logo;
    String logoImpresion;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Local() {
    }

    public String getLogoImpresion() {
        return logoImpresion;
    }

    public void setLogoImpresion(String logoImpresion) {
        this.logoImpresion = logoImpresion;
    }

    public String getEstadoLocal() {
        return EstadoLocal;
    }

    public void setEstadoLocal(String estadoLocal) {
        EstadoLocal = estadoLocal;
    }

    public Local(String nombreLocal, int numeroLocal, String estadoLocal,String logo,String logoimpresion) {
        NombreLocal = nombreLocal;
        NumeroLocal = numeroLocal;
        EstadoLocal = estadoLocal;
        this.logo = logo;
        this.logoImpresion = logoimpresion;
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
