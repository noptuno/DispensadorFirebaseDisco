package com.example.dispensadorfirebase.basedatossectoreselegidos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.example.dispensadorfirebase.clase.SectoresElegidos;

import java.util.ArrayList;

public class SectorDB {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public SectorDB(Context context) {
        dbHelper = new DBHelper(context);
    }

    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if(db!=null){
            db.close();
        }
    }


    private ContentValues clienteMapperContentValues(SectoresElegidos sectores) {

        ContentValues cv = new ContentValues();
        cv.put(ConstantsDB.SEC_IDSECTOR, sectores.getIdSector());
        cv.put(ConstantsDB.SEC_NOMBRE, sectores.getNombre());

        return cv;
    }

    public long insertarSector(SectoresElegidos sector) {
        this.openWriteableDB();
        long rowID = db.insert(ConstantsDB.TABLA_SECTORESELEGIDOS, null, clienteMapperContentValues(sector));
        this.closeDB();
        return rowID;
    }

    public void eliminarSector(int codigo) {
        this.openWriteableDB();
        String where = ConstantsDB.SEC_IDSECTOR + "= ?";
        db.delete(ConstantsDB.TABLA_SECTORESELEGIDOS, where, new String[]{String.valueOf(codigo)});
        this.closeDB();
    }
    public Boolean validar(String nombre) {

        boolean error= false;
        this.openReadableDB();
        String where = ConstantsDB.SEC_NOMBRE+ "= ?";

        Cursor c = db.query(ConstantsDB.TABLA_SECTORESELEGIDOS, null, where, new String[]{nombre}, null, null, null, null);
        try{
            if( c.getCount()>0) {
                c.close();
                error = true;
            }

        }catch (Exception ex){
            Log.e("valiando" + "", "error al leer");
            error = true;
        }

        return error;
    }

    public void eliminarSector(String nombre) {
        this.openWriteableDB();
        String where = ConstantsDB.SEC_NOMBRE + "= ?";
        db.delete(ConstantsDB.TABLA_SECTORESELEGIDOS, where, new String[]{String.valueOf(nombre)});
        this.closeDB();
    }



    public void updateSector(SectoresElegidos sector) {

        this.openWriteableDB();
        String where = ConstantsDB.SEC_IDSECTOR + "= ?";
        db.update(ConstantsDB.TABLA_SECTORESELEGIDOS, clienteMapperContentValues(sector), where, new String[]{String.valueOf(sector.getIdSector())});
        db.close();
    }




    public ArrayList loadSector() {

        ArrayList<SectoresElegidos> list = new ArrayList<>();
        this.openReadableDB();
        String[] campos = new String[]{ConstantsDB.SEC_IDSECTOR, ConstantsDB.SEC_NOMBRE};
        Cursor c = db.query(ConstantsDB.TABLA_SECTORESELEGIDOS, campos, null, null, null, null, null);

        try {
            while (c.moveToNext()) {
                SectoresElegidos sector = new SectoresElegidos();
                sector.setIdSector(c.getInt(0));
                sector.setNombre(c.getString(1));
                list.add(sector);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }

    public void eliminarAll() {
        this.openWriteableDB();
        db.delete(ConstantsDB.TABLA_SECTORESELEGIDOS, null, null);
        this.closeDB();

    }

    /*
    public ArrayList loadSectorDispensador() {

        ArrayList<SectoresElegidos> list = new ArrayList<>();
        this.openReadableDB();
        String desc = ConstantsDB.SEC_NOMBRE + "= ?";
        String where = ConstantsDB.SEC_HABILITADO + "= ?";
        String[] campos = new String[]{ConstantsDB.SEC_ID, ConstantsDB.SEC_NOMBRE, ConstantsDB.SEC_NUMERO,ConstantsDB.SEC_COLOR,ConstantsDB.SEC_HABILITADO};
        Cursor c = db.query(ConstantsDB.TABLA_SECTOR, campos, where, new String[]{String.valueOf(1)}, null, null, desc +" DESC LIMIT 3");

        try {
            while (c.moveToNext()) {
                Sector sector = new Sector();
                sector.setIdSector(c.getInt(0));
                sector.setNombreSector(c.getString(1));
                sector.setNumeroSector(c.getInt(2));
                sector.setColorSector(c.getString(3));
                sector.setHabilitadoSector(c.getInt(4));
                list.add(sector);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }
*/


    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, ConstantsDB.DB_NAME, null, ConstantsDB.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(ConstantsDB.TABLA_SECTORESELEGIDOS_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
