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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.DisplayPequeño;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;

public class InicioOpcionDispositivo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String NOMBREDELDISPOSITIVO;
    private String NOMBREUBICACIONDISPOSITIVO = "NO";
    Button btnconfirmar,btncerrarsesion;
    Spinner dispositivo;
    String dispositivo_seleccionado= null;
    ActionBar actionBar;
    private SharedPreferences pref;
    private String estado = "NO";
    private String CLIENTE;
    private String supervisor = "NO";
    private FirebaseAuth mAuth;

    LinearLayout linearubicacion;
    EditText ubicacion;
    boolean habilitarUbicacion = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_dispositivo);

        btnconfirmar = findViewById(R.id.btnconfirmar);
        dispositivo = findViewById(R.id.spinner_dispositivo);
        btncerrarsesion = findViewById(R.id.btncerrarSesion);

        linearubicacion= findViewById(R.id.linear_ubicacion);
        ubicacion= findViewById(R.id.edtUbicacion);


        mAuth = FirebaseAuth.getInstance();



        btncerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mAuth.signOut();
                SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ESTADOSESION", "NO");
                editor.apply();

                Intent intent = new Intent(InicioOpcionDispositivo.this, InicioSesion.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            }
        });


        abriraplicacion();

        ocultarbarra();

        dispositivo.setOnItemSelectedListener(this);

        btnconfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dispositivo_seleccionado.equals("Seleccione")){
                    Toast.makeText(InicioOpcionDispositivo.this, "Elegir dispositivo", Toast.LENGTH_LONG).show();
                }else{

                    if (habilitarUbicacion){

                        if (ubicacion.length()>0){

                            NOMBREUBICACIONDISPOSITIVO = ubicacion.getText().toString();
                            guardarAvanzar();

                        }else{
                            ubicacion.requestFocus();
                            Toast.makeText(InicioOpcionDispositivo.this, "Escribir Nombre del Dispositivo", Toast.LENGTH_LONG).show();
                        }

                    } else{

                        guardarAvanzar();
                    }

                }

            }
        });


    }


    private void guardarAvanzar(){

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CONFIGURACIONDMR", "SI");
        editor.putString("DISPOSITIVO", dispositivo_seleccionado);
        editor.putString("NOMBREUBICACIONDISPOSITIVO",NOMBREUBICACIONDISPOSITIVO);
        editor.apply();

        Intent intent = new Intent(InicioOpcionDispositivo.this, InicioOpcionLocal.class);
        intent.putExtra("DISPOSITIVO", dispositivo_seleccionado);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();


    }




    private void abriraplicacion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
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



        if  (dispositivo_seleccionado.equals("DISPENSADOR")){

            linearubicacion.setVisibility(View.VISIBLE);
            ubicacion.setText("");
            habilitarUbicacion = true;

        }else{
            linearubicacion.setVisibility(View.GONE);
            ubicacion.setText("");
            habilitarUbicacion = false;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {
        dispositivo_seleccionado = null;
    }




}