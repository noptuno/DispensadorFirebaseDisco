package com.example.dispensadorfirebase.clase;

import java.util.List;

public class ClaseHistorico {

    String name;
    List<SectorHistorico> Historico;

    public ClaseHistorico(String name, List<SectorHistorico> historico) {
        this.name = name;
        Historico = historico;
    }

    public List<SectorHistorico> getHistorico() {
        return Historico;
    }

    public void setHistorico(List<SectorHistorico> historico) {
        Historico = historico;
    }
}
