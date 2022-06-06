package com.example.dispensadorfirebase.administrador;

import static com.example.dispensadorfirebase.app.variables.BASEDATOSSECTORESTEMP;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterSectorLocal;
import com.example.dispensadorfirebase.app.variables;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.Sectores;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AsignarSectoress extends AppCompatActivity {

        ArrayList<Sectores> listnombresectores;
         ArrayList<SectorLocal> listsectoreslocal;
        Button RegistroSectores,btnAsignar,guardar;
        AdapterSectorLocal adapter;
        AlertDialog Adialog;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;
        String CLIENTE;
       String NOMBRELOCALSELECCIONADO=null;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_asignar_sectoresss);

            //FIREBASE
            inicializarFirebase();


            NOMBRELOCALSELECCIONADO = getIntent().getStringExtra("LOCAL");
            CLIENTE = getIntent().getStringExtra("CLIENTE");

            listnombresectores = new ArrayList<>();
            listsectoreslocal = new ArrayList<>();
            adapter = new AdapterSectorLocal();



            //varaibles layout
            RegistroSectores = findViewById(R.id.btnCrearSector);
            btnAsignar = findViewById(R.id.btnAsignar);
            guardar = findViewById(R.id.btnGuardarConfigPrincipal);

            //variables lcoales



            //Acciones layout

            RegistroSectores.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(AsignarSectoress.this, CrearSectores.class);
                    intent.putExtra("CLIENTE", CLIENTE);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            });

            btnAsignar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

            // este asignar deberia implementarlo en el crear local
                    // falta crear sectores
                    if (NOMBRELOCALSELECCIONADO!=null) {
                        asignarsectoresallocalautomatico();
                    }else{
                        Toast.makeText(AsignarSectoress.this, "No selecciono un local", Toast.LENGTH_LONG).show();
                    }

                }
            });

            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            // funcionaldiades

            adapter.setOnNoteSelectedListener(new AdapterSectorLocal.OnNoteSelectedListener() {
                @Override
                public void onClick(SectorLocal note) {

                    //habilitar y registrar en firebase , crear hijo con este nombre sector con todos sus datos
                }
            });

            adapter.setOnDetailListener(new AdapterSectorLocal.OnNoteDetailListener() {
                @Override
                public void onDetail(SectorLocal note) {

                    databaseReference.child(variables.NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(note.getNombreSector()).setValue(note);

                }
            });





            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerSector);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);

            //tempral:

           // crear todos los sectores en la tabla del lcoal en firebase
            cargarListaNombresSectores();
            cargarListaSectoresLocales();


        }

    private void asignarsectoresallocalautomatico() {

        try {
            for (Sectores setor : listnombresectores) {
                Log.i("---> Base Listas: ", setor.getNombre() + " "+ setor.getEstado());

                String nombre = setor.getNombre();
                int limite =  setor.getLimite();
                String color =  setor.getColor();
                String fondoV = setor.getFondoV();
                String fondoH= setor.getFondoH();

                SectorLocal datos = new SectorLocal("0",0,0,0,limite,0,nombre,color,0,0,1,0,fondoH,fondoV,1,"00/00/0000");
                databaseReference.child(variables.NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(setor.getNombre()).setValue(datos);


            }

        } catch (Exception e) {
            Log.e("error", "mensajed");
        }

    }


    private void cargarListaSectoresLocales() {

            setProgressDialog();

            databaseReference.child(variables.NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    listsectoreslocal.clear();

                    for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                        SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);
                        listsectoreslocal.add(sectores);
                    }

                    Adialog.dismiss();
                    actualizarReciclerView();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AsignarSectoress.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                    Adialog.dismiss();
                }

            });

        }


        public void actualizarReciclerView() {
            adapter.setNotes(listsectoreslocal);
            adapter.notifyDataSetChanged();
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
            Adialog.show();
            Adialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                    cargarListaSectoresLocales();

                }
            });

            Window window = Adialog.getWindow();

            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(Adialog.getWindow().getAttributes());
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                Adialog.getWindow().setAttributes(layoutParams);
            }



        }


        private void inicializarFirebase() {
            FirebaseApp.initializeApp(this);
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
        }

    private void cargarListaNombresSectores() {


        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(BASEDATOSSECTORESTEMP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listnombresectores.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    Sectores sectores = objSnaptshot.getValue(Sectores.class);
                    listnombresectores.add(sectores);
                    Log.i("---> Lista Sectores: ", sectores.getNombre() + " "+ sectores.getColor());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    }