package com.example.dispensadorfirebase.clase;

public class SectoresElegidos {

    Integer idSector;
    String nombre;

    public SectoresElegidos() {
    }

    public SectoresElegidos(Integer idSector, String nombre) {
        this.idSector = idSector;
        this.nombre = nombre;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String toString() {
        return "SectoresElegidos{" +
                "idSector=" +         idSector +
                ", NombreSectoro='" +  nombre + '\'' +

                '}';
    }


}
