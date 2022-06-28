package com.example.dispensadorfirebase.aplicaciones;


import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.ROOTINTERNO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.adapter.AdapterDisplayGrande;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DisplayGrande extends AppCompatActivity {

    MediaPlayer mp;
    Button b1;
    int posicion = 0;


    MediaPlayer click, click2;

    ConstraintLayout constrain;
    ActionBar actionBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String NOMBRELOCALSELECCIONADO=null;
    String DISPOSITIVO=null;
    String COMPLETADO=null;
    String IDNOMBRELOCALSELECCIONADO=null;
    String CLIENTE = null;
    private AlertDialog Adialog;
    AdapterDisplayGrande adapter;
    ArrayList<SectorLocal> list = new ArrayList<>();;
    ArrayList<SectoresElegidos> listtemp = new ArrayList<>();
    private SectorDB db = new SectorDB(this);
    private SharedPreferences pref;
    String LOGOLOCAL=null;


private ImageView logolocal;
    private Button configurarnuevamente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_grande);

        configurarnuevamente = findViewById(R.id.btn_configurar);


        logolocal = findViewById(R.id.logolocaldisplay);

        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botonregresar();


            }
        });


        validarConfiguracion();
        leerInicioSectores();


        adapter = new AdapterDisplayGrande(listtemp.size());


        constrain = findViewById(R.id.constrainLayoutGrande);


        inicializarFirebase();


        click = MediaPlayer.create(DisplayGrande.this, R.raw.fin);
        click2 = MediaPlayer.create(DisplayGrande.this, R.raw.notidos);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerviewgrande);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        CargarDatos();

        if (!LOGOLOCAL.equals("NO")){
            cargarLogo(LOGOLOCAL);
        }

        actionBar = getSupportActionBar();
        /*
        hidebarras();
        constrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidebarras();
            }
        });
*/

    }


    private void cargarLogo(String LinkLogo) {

        Uri fondo = Uri.parse(LinkLogo);
        Glide.with(getApplicationContext()).load(fondo).into(logolocal);


    }



    private void botonregresar() {

        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
        View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DisplayGrande.this);

        alertDialogBuilder.setView(promptUserView);

        final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);

        alertDialogBuilder.setTitle("Usuario Administrador: ");

        // prompt for username
        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // and display the username on main activity layout


                if (!userAnswer.equals("") && userAnswer.getText().length()>0){

                    if (validaryguardar(userAnswer.getText().toString())){

                        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("COMPLETADO", "NO");
                        editor.apply();

                        Intent intent= new Intent(DisplayGrande.this, InicioOpcionLocal.class);
                        startActivity(intent);

                        DisplayGrande.this.finish();

                    }else{
                        validar(userAnswer.getText().toString());
                    }


                }

            }
        });

        // all set and time to build and show up!
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        userAnswer.requestFocus();


    }

    private void validar(String password) {

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child("CONFIGURACION").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Map<String, String> stars  = (Map<String, String>) task.getResult().getValue();
                    for (Map.Entry<String, String> entry : stars.entrySet()) {

                        if(password.equals(entry.getValue())){


                            SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("ESTADO", "NO");
                            editor.apply();

                            Intent intent= new Intent(DisplayGrande.this, InicioOpcionLocal.class);
                            startActivity(intent);

                            DisplayGrande.this.finish();
                            break;
                        }

                        //entry.getKey() + "=" + entry.getValue();
                    }

                }
            }
        });
    }





    @Override
    public void onBackPressed() {


        botonregresar();
        // super.onBackPressed();


    }



    private void validarConfiguracion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("COMPLETADO", "NO");

        if (estado.equals("NO")){

            regresarConfiguracion();

        }else{

            DISPOSITIVO = pref.getString("DISPOSITIVO", "NO");
            CLIENTE= pref.getString("CLIENTE","NO");
            NOMBRELOCALSELECCIONADO = pref.getString("NOMBRELOCALSELECCIONADO", "NO");
            IDNOMBRELOCALSELECCIONADO = pref.getString("IDLOCAL", "NO");
            LOGOLOCAL = pref.getString("LOGOLOCAL","NO");
            COMPLETADO = pref.getString("COMPLETADO","NO");

            if (CLIENTE.equals("NO") || IDNOMBRELOCALSELECCIONADO.equals("NO")){

                regresarConfiguracion();
            }
        }

    }

    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals(ROOTINTERNO)){
            v = true;
        }

        return v;
    }



    private void regresarConfiguracion(){


        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("COMPLETADO", "NO");
        editor.apply();
        Intent intent= new Intent(DisplayGrande.this, InicioOpcionLocal.class);
        startActivity(intent);
        DisplayGrande.this.finish();

    }

    private void leerInicioSectores() {

        try {

            listtemp = db.loadSector();


            if (listtemp!= null || !(listtemp.size() >0) ){
                if (listtemp.size()>1){
                   // lineartitulo.setVisibility(View.VISIBLE);
                }else{
                 //   lineartitulo.setVisibility(View.GONE);
                }
            }else{
                regresarConfiguracion();
            }

        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
            regresarConfiguracion();
        }

        //Log.e("SECTORES", "cantidad de sectoes" + listtemp.size());
     //   Toast.makeText(DisplayGrande.this, listtemp.size() + "", Toast.LENGTH_LONG).show();

    }

    private void leerSectoresLocales(SectorLocal sectores) {
        String Color = sectores.getColorSector();

        try {

            SectoresElegidos sec = db.validarSector(sectores.getIdsector());

            if (sec!=null){

                int numeroactual = sec.getUltimonumero();
                int numeronuevo = sectores.getNumeroatendiendo();

                if (numeroactual != numeronuevo){

                    sec.setUltimonumero(numeronuevo);
                    db.updateSector(sec);
                    sectores.setColorSector("#FFE80606");

                    iniciar();

                    new Handler().postDelayed(new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {
                            detener();
                            sectores.setColorSector(Color);
                            adapter.notifyDataSetChanged();

                        }
                    },3000);

                }
                list.add(sectores);
                actualizarReciclerView();
            }

        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
        }
    }



        public void destruir() {
            if (mp != null)
                mp.release();
        }

        public void iniciar() {
            destruir();
            mp = MediaPlayer.create(this, R.raw.notidosaumentadodos);
            mp.start();
            //String op = b1.getText().toString();
            mp.setLooping(false);

        }

    public void detener() {
        if (mp != null) {
            mp.stop();
            posicion = 0;
        }
    }


    private void CargarDatos() {

        setProgressDialog();

        this.databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              list.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){

                        leerSectoresLocales(sectores);

                    }
                }

                if (!(listtemp.size() >0)){
                    regresarConfiguracion();
                }

                Adialog.dismiss();

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


    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //se creo una actividad para gejecutar este metodo
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
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