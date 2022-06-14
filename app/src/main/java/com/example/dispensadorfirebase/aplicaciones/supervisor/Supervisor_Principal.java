package com.example.dispensadorfirebase.aplicaciones.supervisor;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
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
import com.example.dispensadorfirebase.adapter.AdapterSectorLocal;
import com.example.dispensadorfirebase.adapter.AdapterSupervisorPrincipal;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.Datos;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.example.dispensadorfirebase.principaltemp.notificador.Constants;
import com.example.dispensadorfirebase.principaltemp.notificador.FlashSupervisor;
import com.example.dispensadorfirebase.principaltemp.notificador.MyIntentService;
import com.example.dispensadorfirebase.principaltemp.notificador.Supervisor;
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

    boolean nobuscar = false;
    private Button deshabilitar;

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

    Boolean Solicitud = false;
    String CLIENTE=null;
    String IDNOMBRELOCALSELECCIONADO=null;
    String LOGOLOCAL=null;
    String LOGOLOCALIMPRE=null;
    String BDCARGADO = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_principal);
        inicializarFirebase();

        regresar = findViewById(R.id.btn_salir2);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // load the dialog_promt_user.xml layout and inflate to view
                LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
                View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Supervisor_Principal.this);

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
                                editor.putString("ESTADO", "NO");
                                editor.apply();

                                Intent intent = new Intent(Supervisor_Principal.this, Supervisor_Flash.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                               Supervisor_Principal.this.finish();



                            }else{

                                Toast.makeText(getApplicationContext(), "Contraseña Incorrecta", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

                // all set and time to build and show up!
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                userAnswer.requestFocus();

            }
        });




        actionBar = getSupportActionBar();
        actionBar.hide();

        adapter = new AdapterSupervisorPrincipal();


        list = new ArrayList<>();
        validarConfiguracion();
        leerInicioSectores();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclersupervisor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

      //  CargarDatos();

        adapter.setOnDetailListener(new AdapterSupervisorPrincipal.OnNoteDetailListener() {
            @Override
            public void onDetail(SectorLocal note) {

                note.setNotificacion(0);
                note.setNotificaciondeshabilitar(1);
               // actualizarReciclerView();

                databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").child(note.getIdsector()).setValue(note);

            }
        });


        IntentFilter filter = new IntentFilter(
                com.example.dispensadorfirebase.aplicaciones.supervisor.Constants.ACTION_RUN_ISERVICE);


        filter.addAction(com.example.dispensadorfirebase.aplicaciones.supervisor.Constants.ACTION_PROGRESS_EXIT);


        Supervisor_Principal.ResponseReceiver receiver = new Supervisor_Principal.ResponseReceiver();
        registerReceiver(receiver,filter);


        Intent intent = new Intent(this, MyIntentServiceSupervisor.class);
        intent.setAction(Constants.ACTION_RUN_ISERVICE);
        startService(intent);

    }
    private class ResponseReceiver extends BroadcastReceiver {

        // Sin instancias
        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case Constants.ACTION_RUN_ISERVICE:
                    //list.clear();

                    ArrayList<SectorLocal> listaSectores = (ArrayList<SectorLocal>) intent.getSerializableExtra(Constants.EXTRA_PROGRESS);
                   // SectorLocal sectores = (SectorLocal) intent.getSerializableExtra(Constants.EXTRA_PROGRESS);
                    leerSectoresLocales(listaSectores);
                    //datos = (Datos) intent.getSerializableExtra(Constants.EXTRA_PROGRESS);

                   // actualziar();
                    break;

                case Constants.ACTION_PROGRESS_EXIT:

                    break;
            }
        }
    }


    private boolean validaryguardar(String pass) {

            boolean v = false;
            if (pass.equals("dmr")){
                v = true;
            }

            return v;


    }

    private void validarConfiguracion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("ESTADO", "NO");
        if (estado.equals("NO")){
            regresarConfiguracion();
        }else{
            NOMBREDELDISPOSITIVO = pref.getString("DISPOSITIVO", "NO");
            NOMBRELOCALSELECCIONADO = pref.getString("LOCAL", "NO");
            LOGOLOCAL = pref.getString("LOGOLOCAL","NO");
            LOGOLOCALIMPRE= pref.getString("LOGOLOCALIMPRE","NO");
            CLIENTE= pref.getString("CLIENTE","NO");
            IDNOMBRELOCALSELECCIONADO = pref.getString("IDLOCAL", "NO");
            BDCARGADO = pref.getString("BDCARGADO","NO");
        }







    }


    private void regresarConfiguracion(){

        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ESTADO", "NO");
        editor.apply();

        Intent intent= new Intent(Supervisor_Principal.this, InicioOpcionLocal.class);
        startActivity(intent);
        Supervisor_Principal.this.finish();


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

                Log.e("---> Consulta Base", "ENTRO: ");

                list.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){

                       // leerSectoresLocales(sectores);

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


    @SuppressLint("NotifyDataSetChanged")
    private void leerSectoresLocales(ArrayList<SectorLocal>  lista) {
            list.clear();
        for (SectorLocal sectores : lista) {


            try {
                db = new SectorDB(this);


                SectoresElegidos sec = db.validarSector(sectores.getNombreSector());

                if (sec!=null){

                    list.add(sectores);

                    if (sectores.getNotificacion()==1 && sectores.getNotificaciondeshabilitar()==0){

                        setPendingIntent();
                        createNotificationChannel();
                        createNotification(sectores.getNombreSector().toString());
                    }

                    if (sectores.getLlamarsupervisor() == 1){

                        //sectores.setLlamarsupervisor(0);
                        setPendingIntent();
                        createNotificationChannel();
                        createNotification(sectores.getNombreSector().toString());
                        sectores.setLlamarsupervisor(0);

                        // actualizarReciclerView();
                        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(sectores.getNombreSector()).setValue(sectores);

                        // nobuscar = true;
                        //databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRELOCALSELECCIONADO).child(sectores.getNombreSector()).setValue(sectores);

                    }

                }
                actualizarReciclerView();

            } catch (Exception e) {
                Log.e("error", "mensaje mostrar bse local");
            }


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
