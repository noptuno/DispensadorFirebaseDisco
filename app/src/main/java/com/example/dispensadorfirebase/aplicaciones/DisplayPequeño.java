package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.Datos;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayPequeño extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;

    DatabaseReference databaseReference;

    ActionBar actionBar;

    SectorLocal datos = new SectorLocal();

    ConstraintLayout constrain;

    TextView txtnumero_actual, txtnombre_sector;
    private AlertDialog Adialog;
    MediaPlayer click, click2;

    private SectorDB db;

    String NOMBRELOCALSELECCIONADO=null;
    String NOMBREDELDISPOSITIVO=null;

    ArrayList<SectoresElegidos> listtemp;
    List<String> sectoreselegidos;
    private SharedPreferences pref;

    private Button configurarnuevamente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pequeno);


        validarConfiguracion();

        configurarnuevamente = findViewById(R.id.btn_configurar4);

        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ESTADO", "NO");
                editor.apply();
                Toast.makeText(getApplicationContext(), "No hay registro guardado", Toast.LENGTH_LONG).show();
                finish();

            }
        });




        actionBar = getSupportActionBar();

        click = MediaPlayer.create(DisplayPequeño.this, R.raw.fin);
        click2 = MediaPlayer.create(DisplayPequeño.this, R.raw.notidos);


        constrain = findViewById(R.id.constrainlayoutpeque);

        txtnumero_actual = findViewById(R.id.txtNumero_Actual);
        txtnombre_sector = findViewById(R.id.txtNombre_Sector);

       // gif = findViewById(R.id.imggif);



       // String EDteamImage = "https://geant.vteximg.com.br/arquivos/ids/275434/m-pizzas-disco-2021.gif?v=637595480814100000";
       // Glide.with(getApplicationContext()).load(EDteamImage).into(gif);


        hidebarras();
        leerSectoresLocales();
        conectarFirebase();

    }

    private void validarConfiguracion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("ESTADO", "NO");
        if (estado.equals("NO")){
            regresarConfiguracion();
        }

        NOMBREDELDISPOSITIVO = pref.getString("NOMBREDELDISPOSITIVO", "NO");
        NOMBRELOCALSELECCIONADO = pref.getString("NOMBRELOCALSELECCIONADO", "NO");
    }

    private void leerSectoresLocales() {

        db = new SectorDB(this);

        try {
            db = new SectorDB(this);
            listtemp = db.loadSector();

            for (SectoresElegidos sectores : listtemp) {
               // sectoreselegidos.add(sectores.getNombre());
                Log.i("---> Base de ds: ", sectores.toString());

            }

        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
        }


    }
    private void regresarConfiguracion(){

        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ESTADO", "NO");
        editor.apply();
        finish();

    }
    void conectarFirebase(){

        inicializarFirebase();

        setProgressDialog();



        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){

                        for (SectoresElegidos sec : listtemp) {
                            if (sec.getNombre().equals(sectores.getNombreSector())){
                                datos = sectores;
                                Actualizar();
                                break;
                            }
                            Log.i("---> Base de ds: ", sectores.toString());
                        }
                    }
                }

                Adialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayPequeño.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();
            }

        });

    }

    void Actualizar(){



            try {
                int d = datos.getNumeroatendiendo();
                int f = Integer.parseInt(txtnumero_actual.getText().toString());
                if (f != d) {

                   // Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                   // Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                  //  r.play();

                    click2.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        txtnumero_actual.setText(""+datos.getNumeroatendiendo());
        txtnombre_sector.setText(""+datos.getNombreSector());
        txtnombre_sector.setBackgroundColor(Color.parseColor(datos.getColorSector()));
        // databaseReference.child("Datos").child("dispensador1").setValue(datos);



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

                conectarFirebase();
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
}