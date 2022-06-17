package com.example.dispensadorfirebase.aplicaciones.supervisor;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentServiceSupervisor extends IntentService {

    public static final String ACTION_PROGRESO = "net.sgoliver.intent.action.PROGRESO";
    public static final String ACTION_FIN = "net.sgoliver.intent.action.FIN";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SectorLocal sectorlocal;

    String IDNOMBRELOCALSELECCIONADO=null;
    String CLIENTE=null;



    private final static String CHANNEL_ID = "NOTIFICACION";

    public MyIntentServiceSupervisor() {
        super("MyIntentServiceSupervisor");

    }


    @Override
    protected void onHandleIntent(Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();

                IDNOMBRELOCALSELECCIONADO = intent.getExtras().getString("ID","NO");
                CLIENTE = intent.getExtras().getString("CLI","NO");

                intent.getDataString();

                if (com.example.dispensadorfirebase.aplicaciones.supervisor.Constants.ACTION_RUN_ISERVICE.equals(action)) {

                    if (!IDNOMBRELOCALSELECCIONADO.equals("NO") && !CLIENTE.equals("NO")){
                        cargardatos();
                    }else{

                        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
                        sendBroadcast(localIntent);
                    }


                }
        }
    }




    private void cargardatos() {


        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("---> Consulta Base", "ENTRO: ");

               // list.clear();
                ArrayList<SectorLocal> list2 = new ArrayList<>();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){

                        list2.add(sectores);


                       // leerSectoresLocales(sectores);

                    }
                }

                Intent localIntent = new Intent(Constants.ACTION_RUN_ISERVICE)
                        .putExtra(Constants.EXTRA_PROGRESS,list2);
                sendBroadcast(localIntent);


              //  Adialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              //  Toast.makeText(Supervisor_Principal.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
               // Adialog.dismiss();

            }
        });


    }



    @Override
    public void onDestroy() {
        super.onDestroy();

       // Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show();

        // Emisión para avisar que se terminó el servicio
      //  Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
      //  sendBroadcast(localIntent);

     //   Log.d("TAG", "Servicio destruido...");



    }
}