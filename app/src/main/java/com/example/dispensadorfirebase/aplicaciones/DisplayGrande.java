package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.adapter.AdapterDisplayGrande;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayGrande extends AppCompatActivity {



    MediaPlayer click, click2;

    ConstraintLayout constrain;
    ActionBar actionBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String NOMBRELOCALSELECCIONADO=null;
    String NOMBREDELDISPOSITIVO=null;
    private AlertDialog Adialog;
    AdapterDisplayGrande adapter;
private LinearLayout lineartitulo;
    ArrayList<SectorLocal> list;
    ArrayList<SectoresElegidos> listtemp;
    private SectorDB db;
    private SharedPreferences pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_grande);

        list = new ArrayList<>();

        lineartitulo = findViewById(R.id.linearturnos);

        validarConfiguracion();
        leerSectoresLocalesinicio();


        adapter = new AdapterDisplayGrande(listtemp.size());
        constrain = findViewById(R.id.constrainLayoutGrande);


        inicializarFirebase();


        //valdiar que el los nombres de sectores en firebase coincidan con los nombres de sercotres locales
        //el que no exista que lo elimine


        click = MediaPlayer.create(DisplayGrande.this, R.raw.fin);
        click2 = MediaPlayer.create(DisplayGrande.this, R.raw.ckickk);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerviewgrande);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        hidebarras();
        CargarDatos();
    }

    private void validarConfiguracion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("ESTADO", "NO");
        if (estado.equals("NO")){
            regresarConfiguracion();
        }else{

            NOMBREDELDISPOSITIVO = pref.getString("DISPOSITIVO", "NO");
            NOMBRELOCALSELECCIONADO = pref.getString("LOCAL", "NO");
        }


    }

    private void regresarConfiguracion(){

        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ESTADO", "NO");
        editor.apply();
        Toast.makeText(DisplayGrande.this, "No hay registro guardado", Toast.LENGTH_LONG).show();
        finish();

    }

    private void leerSectoresLocalesinicio() {

        db = new SectorDB(this);

        try {
            db = new SectorDB(this);
            listtemp = db.loadSector();

            if (!(listtemp.size() >0)){
                regresarConfiguracion();
            }



        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
        }


        if (listtemp.size()>1){
            lineartitulo.setVisibility(View.VISIBLE);
        }else{
            lineartitulo.setVisibility(View.GONE);
        }
        Toast.makeText(DisplayGrande.this, listtemp.size() + "", Toast.LENGTH_LONG).show();

    }

    private void leerSectoresLocales(SectorLocal sectores) {

        db = new SectorDB(this);

        listtemp.clear();

        try {
            db = new SectorDB(this);
            listtemp = db.loadSector();

            for (SectoresElegidos sectoreselegidos : listtemp) {

                    if (sectoreselegidos.getNombre().equals(sectores.getNombreSector())){

                        int numerotemporal = sectoreselegidos.getUltimonumero();
                        int numeoractual = sectores.getNumeroatendiendo();

                        Log.i("---> Numeros: ", sectores.toString());


                        if (numerotemporal!=numeoractual){

                            //guardardarnumerodelsector
//TODO NO SE ESTA REGISTRANDO EL ULTIMO NUMERO
                            sectoreselegidos.setUltimonumero(numeoractual);
                            db.updateSector(sectoreselegidos);
                            hacerflash(sectores);


                        }


                        list.add(sectores);

                    }
            }

            if (!(listtemp.size() >0)){
                regresarConfiguracion();
            }

        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
        }

    }

    private void hacerflash(SectorLocal sectores) {

        Toast.makeText(DisplayGrande.this, sectores.getNombreSector() +" B Cambio a: " + sectores.getNumeroatendiendo(), Toast.LENGTH_LONG).show();

    }


    private void CargarDatos() {

        setProgressDialog();
        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){
                        leerSectoresLocales(sectores);

                    }
                }


                Adialog.dismiss();
                actualizarReciclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayGrande.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();

            }
        });


    }


    public void actualizarReciclerView() {

        adapter.setNotes(list);
        adapter.notifyDataSetChanged();
        click2.start();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //se creo una actividad para gejecutar este metodo
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
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
        Adialog.setCancelable(false);
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