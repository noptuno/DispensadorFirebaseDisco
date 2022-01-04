package com.example.dispensadorfirebase.administrador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;

public class  Menu extends AppCompatActivity {


    Button btnRegistroLocales, btnReportes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);

        btnRegistroLocales = findViewById(R.id.btnRegistroLocales);
        btnReportes= findViewById(R.id.btnReportes);



        btnRegistroLocales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Menu.this, ListaLocales.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Menu.this, InicioOpcionDispositivo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });




    }
}