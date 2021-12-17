package com.example.dispensadorfirebase.basedatossectoreselegidos;

public class ConstantsDB {
    //General
    public static final String DB_NAME = "SECTORESELEGIDOS.db";
    public static final int DB_VERSION = 1;



    //TABLAPRODUCTO

    public static final String TABLA_SECTORESELEGIDOS = "sectoreselegidos";
    public static final String SEC_IDSECTOR = "_idsector";
    public static final String SEC_NOMBRE = "nombre";



    public static final String TABLA_SECTORESELEGIDOS_SQL =
            "CREATE TABLE  " + TABLA_SECTORESELEGIDOS + "(" +
                    SEC_IDSECTOR+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SEC_NOMBRE  + " TEXT);" ;

}
