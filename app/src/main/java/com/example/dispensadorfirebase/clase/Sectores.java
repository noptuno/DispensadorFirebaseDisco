package com.example.dispensadorfirebase.clase;

public class Sectores {


    String nombre;
    int limite;
    String color;
    int estado;

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Sectores() {
    }

    public Sectores(String nombre, int limite, String color, int estado) {
        this.nombre = nombre;
        this.limite = limite;
        this.color = color;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getLimite() {
        return limite;
    }

    public void setLimite(int limite) {
        this.limite = limite;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
