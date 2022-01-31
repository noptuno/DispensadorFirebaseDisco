package com.example.dispensadorfirebase.aplicaciones.supervisor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.DisplayPequeño;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;

public class Supervisor_Flash extends AppCompatActivity {

    ActionBar actionBar;
    private String NOMBREDELDISPOSITIVO = "SUPERVISOR";
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_supervisor);
        actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


                    pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
                    String estado = pref.getString("ESTADO", "NO");

                            if (estado.equals("SI")) {

                                Intent mainIntent = new Intent(Supervisor_Flash.this, Supervisor_Principal.class);
                                startActivity(mainIntent);
                                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                Supervisor_Flash.this.finish();

                            }else{
                                Intent mainIntent = new Intent(Supervisor_Flash.this, InicioOpcionLocal.class);
                                mainIntent.putExtra("DISPOSITIVO", NOMBREDELDISPOSITIVO);
                                startActivity(mainIntent);
                                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                Supervisor_Flash.this.finish();

                            }

                }



        }, 2000);



    }
}