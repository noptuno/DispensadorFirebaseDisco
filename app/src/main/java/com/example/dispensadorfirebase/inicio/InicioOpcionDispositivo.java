package com.example.dispensadorfirebase.inicio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayPequeño;
import com.google.android.gms.dynamic.IFragmentWrapper;

public class InicioOpcionDispositivo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

TextView numeroserie;
    Button btnconfirmar,validar;
Spinner dispositivo;
String dispositivo_seleccionado= null;
    ActionBar actionBar;
    private SharedPreferences pref;
    private String estado = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_dispositivo);

        btnconfirmar = findViewById(R.id.btnconfirmar);
        validar = findViewById(R.id.btnValidar);
        dispositivo = findViewById(R.id.spinner_dispositivo);
        numeroserie = findViewById(R.id.txtsn);

        ocultarbarra();
        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);

        dispositivo.setOnItemSelectedListener(this);


        btnconfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (dispositivo_seleccionado.equals("Seleccionado")){

                    Toast.makeText(InicioOpcionDispositivo.this, "Debe Seleccionar el Dispositivo", Toast.LENGTH_LONG).show();

                }else{

                    Intent intent = new Intent(InicioOpcionDispositivo.this, InicioOpcionLocal.class);
                    intent.putExtra("DISPOSITIVO", dispositivo_seleccionado);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                }


                //guardar share preference

            }
        });
        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnconfirmar.setEnabled(true);
                if (!numeroserie.getText().toString().equals("")){
                    btnconfirmar.setEnabled(true);
                }else {
                    Toast.makeText(InicioOpcionDispositivo.this, "Numero de Serie Invalido", Toast.LENGTH_LONG).show();

                }



            }
        });

    //abriraplicacion();

    }

    private void abriraplicacion() {

        estado = pref.getString("ESTADO", "NO");
        String tipoaplicaion = pref.getString("NOMBREDELDISPOSITIVO", "NO");
        Intent intent;

        if (estado.equals("SI")){

            if (tipoaplicaion.equals("DISPENSADOR")){
                intent = new Intent(InicioOpcionDispositivo.this, DispensadorTurno.class);

            }else if (tipoaplicaion.equals("TABLET 10PLG")){
                intent = new Intent(InicioOpcionDispositivo.this, DisplayPequeño.class);

            }else{
                intent = new Intent(InicioOpcionDispositivo.this, DisplayPequeño.class);

            }
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }


    }

    private void ocultarbarra() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
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