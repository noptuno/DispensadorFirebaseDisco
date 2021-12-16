package com.example.dispensadorfirebase.principaltemp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.principaltemp.notificador.FlashSupervisor;

public class Menu extends AppCompatActivity {

    ActionBar actionBar;

    Button display,dispensador,tablet,supervisor,configuracion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        display = findViewById(R.id.btnDisplay);
        dispensador = findViewById(R.id.btnDispensador);
        tablet = findViewById(R.id.btnTablet);
        supervisor = findViewById(R.id.btnsupervisor);
        configuracion = findViewById(R.id.btnconfig);

        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });



        supervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Menu.this, FlashSupervisor.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Menu.this, Display.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });


        dispensador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Menu.this, Dispensador.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


            }
        });

        tablet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Menu.this, Tablet.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


            }
        });

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }


    }



}