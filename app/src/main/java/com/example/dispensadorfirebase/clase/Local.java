package com.example.dispensadorfirebase.clase;

public class Local {
    String idLocal;
    String nombreLocal;
    int numeroLocal;
    String estado;
    String logo;
    String logoImpreso;

    public String getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(String idLocal) {
        this.idLocal = idLocal;
    }

    public String getNombreLocal() {
        return nombreLocal;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }

    public int getNumeroLocal() {
        return numeroLocal;
    }

    public void setNumeroLocal(int numeroLocal) {
        this.numeroLocal = numeroLocal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogoImpreso() {
        return logoImpreso;
    }

    public void setLogoImpreso(String logoImpreso) {
        this.logoImpreso = logoImpreso;
    }

    public Local() {
    }

    public Local(String estado, String idLocal, String logo, String logoImpreso, String nombreLocal, int numeroLocal) {

        this.estado = estado;
        this.idLocal = idLocal;
        this.logo = logo;
        this.logoImpreso = logoImpreso;
        this.nombreLocal = nombreLocal;
        this.numeroLocal = numeroLocal;


    }
}
