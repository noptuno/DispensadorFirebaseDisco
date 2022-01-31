package com.example.dispensadorfirebase.principaltemp.notificador;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.Datos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Supervisor extends AppCompatActivity {

    private Button btDeshabilitar;
    private AlertDialog Adialog;
    private TextView sector,cantidad;
    private boolean activado = false;

    ActionBar actionBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Datos datos, datos2;
    private String color = "#F44336";
    private String colordos = "#4CAF50";
    private PendingIntent pendingIntent;
    private PendingIntent siPendingIntent;
    private PendingIntent noPendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    public final static int NOTIFICACION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);


        btDeshabilitar = findViewById(R.id.btnSupervisor);

        sector = findViewById(R.id.txtssector);
        cantidad = findViewById(R.id.txtscantidad);

        actionBar = getSupportActionBar();
        actionBar.hide();

        inicializarFirebase();

        datos = new Datos(0,0,0,0,1,"otros","#2196F3",0,0,0);


        btDeshabilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datos.setNotificaciondeshabilitar(1);
                btDeshabilitar.setEnabled(false);
                btDeshabilitar.setBackgroundColor(Color.parseColor(color));
                databaseReference.child("Datos").child("dispensador1").setValue(datos);

            }
        });


        IntentFilter filter = new IntentFilter(
                Constants.ACTION_RUN_ISERVICE);


        filter.addAction(Constants.ACTION_PROGRESS_EXIT);


        ResponseReceiver receiver = new ResponseReceiver();
        registerReceiver(receiver,filter);



        Intent intent = new Intent(this, MyIntentService.class);
        intent.setAction(Constants.ACTION_RUN_ISERVICE);
        startService(intent);

    }

   void actualziar(){

        cantidad.setText(""+datos.getCantidadEspera());
        sector.setText(""+datos.getNombreSector());
        sector.setBackgroundColor(Color.parseColor(datos.getColorSector()));

       if (datos.getNotificacion()==1 && datos.getNotificaciondeshabilitar()==0){

           setPendingIntent();
           createNotificationChannel();
           createNotification("Atender el Sector");
           btDeshabilitar.setEnabled(true);
           btDeshabilitar.setBackgroundColor(Color.parseColor(colordos));
       }else{

           btDeshabilitar.setEnabled(false);
           btDeshabilitar.setBackgroundColor(Color.parseColor(color));
       }

       if (datos.getLlamarsupervisor() == 1){

           datos.setLlamarsupervisor(0);
           setPendingIntent();
           createNotificationChannel();
           createNotification("Lo estan Solicitando");
           databaseReference.child("Datos").child("dispensador1").setValue(datos);

       }

   }

    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {


                case Constants.ACTION_RUN_ISERVICE:

                    datos = (Datos) intent.getSerializableExtra(Constants.EXTRA_PROGRESS);
                    actualziar();
                    break;

                case Constants.ACTION_PROGRESS_EXIT:


                    break;
            }
        }
    }



    private void cargardatos() {







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
        Intent intent = new Intent(this, Supervisor.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Supervisor.class);
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
