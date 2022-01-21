package com.example.dispensadorfirebase.clase;

public class SectoresElegidos {

    Integer idSector;
    String nombre;
    int favorito;
    String ultimonumero;

    public SectoresElegidos() {
    }

    public SectoresElegidos(Integer idSector, String nombre, int favorito, String ultimonumero) {
        this.idSector = idSector;
        this.nombre = nombre;
        this.favorito = favorito;
        this.ultimonumero = ultimonumero;
    }

    public String getUltimonumero() {
        return ultimonumero;
    }

    public void setUltimonumero(String ultimonumero) {
        this.ultimonumero = ultimonumero;
    }

    public int getFavorito() {
        return favorito;
    }

    public void setFavorito(int favorito) {
        this.favorito = favorito;
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
                ", ultimonumer='" +  ultimonumero + '\'' +
                '}';
    }


}
