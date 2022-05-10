package com.example.dispensadorfirebase.administrador;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.example.dispensadorfirebase.inicio.InicioOpcionSectores;

public class  Menu extends AppCompatActivity {


    Button btnRegistroLocales, btnReportes;

EditText nombrecliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_administrador);

        btnRegistroLocales = findViewById(R.id.btnRegistroLocales);
        btnReportes= findViewById(R.id.btnReportes);

        nombrecliente = findViewById(R.id.editclientebase);



        btnRegistroLocales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Menu.this, ListaLocales.class);

                intent.putExtra("CLIENTE", nombrecliente.getText().toString());

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