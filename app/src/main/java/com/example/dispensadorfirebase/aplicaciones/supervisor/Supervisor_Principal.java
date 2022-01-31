package com.example.dispensadorfirebase.aplicaciones.supervisor;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.adapter.AdapterDisplayGrande;
import com.example.dispensadorfirebase.adapter.AdapterSupervisorPrincipal;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
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

public class Supervisor_Principal extends AppCompatActivity {

    private Button btDeshabilitar;
    private AlertDialog Adialog;
    ActionBar actionBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private String color = "#F44336";
    private String colordos = "#4CAF50";
    private PendingIntent pendingIntent;

    private final static String CHANNEL_ID = "NOTIFICACION";
    public final static int NOTIFICACION_ID = 0;

    ArrayList<SectorLocal> list = new ArrayList<>();;
    ArrayList<SectoresElegidos> listtemp = new ArrayList<>();
    private SectorDB db = new SectorDB(this);
    private SharedPreferences pref;
    String NOMBRELOCALSELECCIONADO=null;
    String NOMBREDELDISPOSITIVO=null;
    AdapterSupervisorPrincipal adapter;

    private Button regresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_principal);
        inicializarFirebase();


        regresar = findViewById(R.id.btn_salir2);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        btDeshabilitar = findViewById(R.id.btnSupervisor);


        actionBar = getSupportActionBar();
        actionBar.hide();



        btDeshabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btDeshabilitar.setEnabled(false);
                btDeshabilitar.setBackgroundColor(Color.parseColor(color));

            }
        });

        adapter = new AdapterSupervisorPrincipal();
        list = new ArrayList<>();
        validarConfiguracion();
        leerInicioSectores();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclersupervisor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

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
        finish();

    }

   void actualziar(){

       // cantidad.setText(""+datos.getCantidadEspera());
      //  sector.setText(""+datos.getNombreSector());
      //  sector.setBackgroundColor(Color.parseColor(datos.getColorSector()));



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

                if (!(listtemp.size() >0)){
                    regresarConfiguracion();
                }


                Adialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Supervisor_Principal.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();

            }
        });


    }

    public void actualizarReciclerView() {
        adapter.setNotes(list);
        adapter.notifyDataSetChanged();
    }


    private void leerSectoresLocales(SectorLocal sectores) {
        String Color = sectores.getColorSector();
        try {
            db = new SectorDB(this);


            SectoresElegidos sec = db.validarSector(sectores.getNombreSector());

            if (sec!=null){

                list.add(sectores);

                if (!db.validarUltimoNumero(sectores.getNumeroatendiendo()+"")){
                    sec.setUltimonumero(sectores.getNumeroatendiendo()+"");

                    db.updateSector(sec);

                    //sectores.setColorSector(color);

                    sectores.setColorSector("#FFE80606");

                    //adapter.notifyDataSetChanged();


                    //click2.start();


                    new Handler().postDelayed(new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {

                            sectores.setColorSector(Color);
                            adapter.notifyDataSetChanged();

                        }
                    },4000);



                }

                actualizarReciclerView();

            }

        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
        }
    }


    private void leerInicioSectores() {

        try {

            listtemp = db.loadSector();


            if (listtemp!= null || !(listtemp.size() >0) ){
                if (listtemp.size()>1){
                  //  lineartitulo.setVisibility(View.VISIBLE);
                }else{
                   // lineartitulo.setVisibility(View.GONE);
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



    private void setPendingIntent(){
        Intent intent = new Intent(this, Supervisor_Principal.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Supervisor_Principal.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification(String mensaje){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_sms_black_24dp);
        builder.setContentTitle("Notificacion");
        builder.setContentText(mensaje);
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setLights(Color.MAGENTA, 1000, 1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

       builder.setContentIntent(pendingIntent);
       // builder.addAction(R.drawable.ic_sms_black_24dp, "Si", siPendingIntent);
       // builder.addAction(R.drawable.ic_sms_black_24dp, "No", noPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build());
    }
}
