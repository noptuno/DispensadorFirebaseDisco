package com.example.dispensadorfirebase.clase;

public class Usuario {

    String email;
    String empresa;
    String nombre;
    String rol;
    String estado;

    public Usuario() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario(String email, String empresa, String nombre, String rol, String estado) {
        this.email = email;
        this.empresa = empresa;
        this.nombre = nombre;
        this.rol = rol;
        this.estado = estado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
