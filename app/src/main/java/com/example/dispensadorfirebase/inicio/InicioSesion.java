package com.example.dispensadorfirebase.inicio;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLAUSUARIO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Flash;
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Principal;
import com.example.dispensadorfirebase.clase.Local;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class InicioSesion extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencelocales;
    private Button btnconfirmar,btnsalir;
    private EditText edtcorreo,edtpassword;
    private AlertDialog Adialog;
    private FirebaseAuth mAuth;
    private SharedPreferences pref;

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);


      btnconfirmar = findViewById(R.id.btnConfirmar);
      btnsalir= findViewById(R.id.btnSalir);
      edtcorreo= findViewById(R.id.edtcorreo);
      edtpassword= findViewById(R.id.edtpassword);

      inicializarFirebase();

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("ESTADOSESION", "NO");

        if (estado.equals("SI")){
            Intent intent = new Intent(InicioSesion.this, InicioOpcionDispositivo.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();

        }


      btnconfirmar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              String correo = edtcorreo.getText().toString();
              String pass = edtpassword.getText().toString();

              if (correo.length()>0 && pass.length()>0){

                  btnconfirmar.setEnabled(false);
                  setProgressDialog();
                  validarUsuario(correo,pass);

              }else{

                  //mensaje faltan datos
              }


          }
      });

        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

finish();

            }
        });


    }

    private void validarUsuario(String correo,String pass) {



            mAuth.signInWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(InicioSesion.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                            } else {
                                Toast.makeText(InicioSesion.this, "Usuario Invalido", Toast.LENGTH_LONG).show();
                                btnconfirmar.setEnabled(true);
                                Adialog.dismiss();
                            }


                        }

                    });
    }

    private void updateUI(FirebaseUser user) {
/*
        databaseReferencelocales.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLAUSUARIO).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // How to return this value?

                Usuario a =  dataSnapshot.getValue(Usuario.class);
                Log.d("TAG", a.getEmpresa());
                Adialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Adialog.dismiss();
            }
        });
*/

        databaseReferencelocales.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLAUSUARIO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    Usuario temp = objSnaptshot.getValue(Usuario.class);

                    if (temp.getEmail().equals(user.getEmail())){

                        Log.d("TAG", temp.getEmpresa());


                        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("ESTADOSESION","SI");
                        editor.putString("CLIENTE",temp.getEmpresa());
                        editor.putString("ROL",temp.getRol());
                        editor.apply();

                        Intent mainIntent = new Intent(InicioSesion.this, InicioOpcionDispositivo.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        InicioSesion.this.finish();

                        break;

                    }

                }

                Adialog.dismiss();
                btnconfirmar.setEnabled(true);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InicioSesion.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();
                btnconfirmar.setEnabled(true);
            }

        });



    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencelocales = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
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
        //TODO revisar
        //Adialog.show();
        Adialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {



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

}