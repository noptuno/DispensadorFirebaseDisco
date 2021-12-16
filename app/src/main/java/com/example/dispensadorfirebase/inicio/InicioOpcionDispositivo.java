package com.example.dispensadorfirebase.inicio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.dispensadorfirebase.R;

public class InicioOpcionDispositivo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Button btnconfirmar;
Spinner dispositivo;
String dispositivo_seleccionado= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_dispositivo);

        btnconfirmar = findViewById(R.id.btnconfirmar);

        dispositivo = findViewById(R.id.spinner_dispositivo);


        dispositivo.setOnItemSelectedListener(this);


        btnconfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(InicioOpcionDispositivo.this, InicioOpcionLocal.class);
                intent.putExtra("DISPOSITIVO", dispositivo_seleccionado);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                //guardar share preference

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id)
    {
        String item = parent.getItemAtPosition(pos).toString();
        dispositivo_seleccionado = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        dispositivo_seleccionado = null;
    }




}