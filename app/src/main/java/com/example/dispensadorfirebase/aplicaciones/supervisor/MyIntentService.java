package com.example.dispensadorfirebase.aplicaciones.supervisor;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.dispensadorfirebase.clase.Datos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {

    public static final String ACTION_PROGRESO = "net.sgoliver.intent.action.PROGRESO";
    public static final String ACTION_FIN = "net.sgoliver.intent.action.FIN";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Datos datos;

    private final static String CHANNEL_ID = "NOTIFICACION";

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();

                if (Constants.ACTION_RUN_ISERVICE.equals(action)) {
                    cargardatos();
                }

        }
    }

    private void cargardatos() {


        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        datos = new Datos(0,0,0,8,1,"Comidas","#2196F3",0,0,0);

        databaseReference.child("Datos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //  EnableDialog(true,"true");
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {

                    datos = objSnaptshot.getValue(Datos.class);

                        Intent localIntent = new Intent(Constants.ACTION_RUN_ISERVICE)
                                .putExtra(Constants.EXTRA_PROGRESS,datos);
                        sendBroadcast(localIntent);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


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