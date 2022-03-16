package com.example.dispensadorfirebase.administrador;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.Datos;
import com.example.dispensadorfirebase.clase.Local;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.dispensadorfirebase.app.variables;
public class CrearLocalDialog extends AppCompatActivity {

    //referencia firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    //varaibles layout
    EditText NombreLocal,NumeroLocal;
    TextView EstadoLocal;
    Button Guardar,Cancelar;


    //variables lcoales

    Local local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_local_dialog);

      NombreLocal = findViewById(R.id.txtNombreLocal);
      NumeroLocal= findViewById(R.id.txtNumeroLocal);
      EstadoLocal= findViewById(R.id.txtEstadoLocal);
      Guardar= findViewById(R.id.btnGuardar);
      Cancelar= findViewById(R.id.btnCancelar);

      inicializarFirebase();

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //validar limite de locales

                //validar llenar datos


                String nom = NombreLocal.getText().toString();
                String val1= NumeroLocal.getText().toString();
                int num=Integer.parseInt(val1);
                String est = "true";


                Local local=new Local(nom,num,est);

                RegistroFirebase(local);




            }
        });


    }

    private void RegistroFirebase(Local local) {

        //validar que el nombre no se repita

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(local.getNombreLocal()).setValue(local);

        finish();
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }



}