package com.example.dispensadorfirebase.clase;

public class Variables {

    String ROOT;
    String PASSWORD;
    String TIPOPAPEL;

    public Variables(String ROOT, String PASSWORD) {
        this.ROOT = ROOT;
        this.PASSWORD = PASSWORD;

    }

    public String getROOT() {
        return ROOT;
    }

    public void setROOT(String ROOT) {
        this.ROOT = ROOT;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
}
