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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.DisplayPequeño;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.google.android.gms.dynamic.IFragmentWrapper;

public class InicioOpcionDispositivo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String NOMBREDELDISPOSITIVO;
    private EditText password,nombrecliente;
    Button btnconfirmar;
    Spinner dispositivo;
    String dispositivo_seleccionado= null;
    ActionBar actionBar;
    private SharedPreferences pref;
    private String estado = "NO";
    private String CLIENTE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_dispositivo);

        btnconfirmar = findViewById(R.id.btnconfirmar);
        nombrecliente = findViewById(R.id.editcliente);
        dispositivo = findViewById(R.id.spinner_dispositivo);
        password = findViewById(R.id.editPassword);

        abriraplicacion();

        ocultarbarra();

        dispositivo.setOnItemSelectedListener(this);

        btnconfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dispositivo_seleccionado.equals("Seleccione") && nombrecliente.getText().toString().length()==0){

                    Toast.makeText(InicioOpcionDispositivo.this, "Debe Seleccionar el Dispositivo", Toast.LENGTH_LONG).show();

                }else{

                    if (validaryguardar()){

                        guardarSharePreferencePrincipal();

                        Intent intent = new Intent(InicioOpcionDispositivo.this, InicioOpcionLocal.class);
                        intent.putExtra("DISPOSITIVO", dispositivo_seleccionado);
                        intent.putExtra("CLIENTE",nombrecliente.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        finish();

                    }else{

                        Toast.makeText(InicioOpcionDispositivo.this, "Error de contraseña", Toast.LENGTH_LONG).show();



                    }
                }
            }
        });



    }

    private void guardarSharePreferencePrincipal() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CONFIGURACIONDMR", "SI");
        editor.putString("DISPOSITIVO", dispositivo_seleccionado);
        editor.putString("CLIENTE", nombrecliente.getText().toString());
        editor.apply();


    }

    private boolean validaryguardar(){
        boolean v = false;
        String pass = password.getText().toString();


        if (pass.equals("dmr")){
            v = true;
        }


            return v;
    }



    private void abriraplicacion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("ESTADO", "NO");
        String configuracion = pref.getString("CONFIGURACIONDMR", "NO");

        if (configuracion.equals("SI")){


            NOMBREDELDISPOSITIVO = pref.getString("DISPOSITIVO", "NO");
            CLIENTE = pref.getString("CLIENTE", "NO");

            if (!NOMBREDELDISPOSITIVO.equals("NO") && !CLIENTE.equals("NO")){

                    Intent intent = new Intent(InicioOpcionDispositivo.this, InicioOpcionLocal.class);
                    intent.putExtra("DISPOSITIVO", NOMBREDELDISPOSITIVO);
                    intent.putExtra("CLIENTE", CLIENTE);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();

            }

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