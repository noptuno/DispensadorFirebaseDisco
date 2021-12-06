package com.example.dispensadorfirebase.principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.Datos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Tablet extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ActionBar actionBar;
    ConstraintLayout constrain;
    TextView txtnumeroactual, txtcantidadespera, txtsector;
    MediaPlayer click, click2;
    int baselimite;
Button btnsupervisor;

    LinearLayout layoutsupervisor;
    LinearLayout layoutrollo;

    int limiteretroceder = 10;
    int retrocesos = 0;


    Button sumar,restar,reset;
private  AlertDialog Adialog;
    int  Numero_Actual = 0,Cantidad_Espera = 10, Ultimo_numero = 0, limite_espera = 8;
    Datos datos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet);


        txtnumeroactual = findViewById(R.id.txtNumero_Actual);
        txtcantidadespera= findViewById(R.id.txtscantidad);
        txtsector = findViewById(R.id.txttnombresector);
       sumar = findViewById(R.id.btnSuma);
       restar = findViewById(R.id.btnResta);
       reset = findViewById(R.id.btnReset);


        btnsupervisor = findViewById(R.id.btnllamarsupervisor);


        click = MediaPlayer.create(Tablet.this, R.raw.fin);
        click2 = MediaPlayer.create(Tablet.this, R.raw.ckickk);

        constrain = findViewById(R.id.constrainTablet);

        datos = new Datos(0,0,0,8,1,"Comidas","#2196F3",0,0,0);


        btnsupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                datos.setLlamarsupervisor(1);


                    sumar.setEnabled(false);
                    restar.setEnabled(false);
                    reset.setEnabled(false);
                btnsupervisor.setEnabled(false);
                    click2.start();

                    // setProgressDialog();
                    delay();


                databaseReference.child("Datos").child("dispensador1").setValue(datos);



            }
        });




        sumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                sumar();

                if (retrocesos>0){
                    retrocesos--;
                }

            }
        });

        restar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (retrocesos < limiteretroceder){

                    restar();

                }else{

                    Toast.makeText(Tablet.this, "El limite es de 10 turnos para retroceder", Toast.LENGTH_LONG).show();

                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reset();


            }
        });

        actionBar = getSupportActionBar();
        constrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidebarras();
            }
        });

        conectarFirebase();
    }


    void conectarFirebase(){
        inicializarFirebase();
        setProgressDialog();
        databaseReference.child("Datos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    datos = objSnaptshot.getValue(Datos.class);
                    Actualizar();
                    Adialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Tablet.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();
            }

        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        hidebarras();
    }

    void hidebarras() {

        constrain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    void sumar(){



        if (datos.sumar()){

            sumar.setEnabled(false);
            restar.setEnabled(false);
            reset.setEnabled(false);

            click2.start();

            // setProgressDialog();
            delay();

            Registrar();
        }else{
            Toast.makeText(Tablet.this, "No hay Clientes para atender", Toast.LENGTH_LONG).show();
        }

    }

    void restar(){


        if (datos.restar()){

            sumar.setEnabled(false);
            restar.setEnabled(false);
            reset.setEnabled(false);

            //setProgressDialog();
            click2.start();
            delay();
            retrocesos++;

            Registrar();
        }else{
            Toast.makeText(Tablet.this, "No hay Clientes en Esperando", Toast.LENGTH_LONG).show();
        }


    }

    void reset(){

        sumar.setEnabled(false);
        restar.setEnabled(false);
        reset.setEnabled(false);
        //setProgressDialog();
        click2.start();
        delay();
        permisos();

    }

    private void permisos() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Tablet.this);
        View mView = getLayoutInflater().inflate(R.layout.alerdiaglog, null);
        final EditText mEmail = (EditText) mView.findViewById(R.id.etEmail);
        final EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
        final TextView text = (TextView) mView.findViewById(R.id.txt_sucursal);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);

        mBuilder.setView(mView);
        final AlertDialog dialogg = mBuilder.create();
        dialogg.show();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPassword.getText().toString().isEmpty()) {


                    String PASSWORD = "123";
                    String PASSWORDROOT = "dmrmilrollos";

                    if (mPassword.getText().toString().equals(PASSWORD) || mPassword.getText().toString().equals(PASSWORDROOT) ){

                        retrocesos = 0;
                        datos.reset();
                        Registrar();
                        dialogg.dismiss();
                        hidebarras();

                    }else{

                        Toast.makeText(Tablet.this,"Acceso Denegado", Toast.LENGTH_SHORT).show();
                        dialogg.dismiss();
                        hidebarras();
                    }

                } else {

                    mPassword.setError("Faltan Datos");
                    mPassword.requestFocus();
                    hidebarras();
                }
            }
        });



    }


    void delay(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                btnsupervisor.setEnabled(true);
                sumar.setEnabled(true);
                restar.setEnabled(true);
                reset.setEnabled(true);
                Adialog.dismiss();
            }


        }, 1000);

    }


    void Registrar(){

        click2.start();
        txtnumeroactual.setText(""+datos.getNumeroatendiendo());
        txtcantidadespera.setText(""+datos.getCantidadEspera());



        if (datos.getCantidadEspera()<baselimite){
            datos.setNotificacion(0);
            datos.setNotificaciondeshabilitar(0);
        }else{

            datos.setNotificacion(1);
        }


        databaseReference.child("Datos").child("dispensador1").setValue(datos);




    }


    void Actualizar(){


        txtnumeroactual.setText(""+datos.getNumeroatendiendo());
        txtcantidadespera.setText(""+datos.getCantidadEspera());
        baselimite = datos.getLimite();
        txtsector.setText(datos.getNombreSector());
        txtsector.setBackgroundColor(Color.parseColor(datos.getColorSector()));

       // databaseReference.child("Datos").child("dispensador1").setValue(datos);

    }



    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //se creo una actividad para gejecutar este metodo
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }




    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        Adialog = builder.create();
        Adialog.setCancelable(true);

        Adialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                conectarFirebase();
            }
        });


        Adialog.show();


        Window window = Adialog.getWindow();

        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Adialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            Adialog.getWindow().setAttributes(layoutParams);
        }



    }



}