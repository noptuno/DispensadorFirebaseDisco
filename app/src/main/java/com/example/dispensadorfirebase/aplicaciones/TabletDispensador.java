package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Principal;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.Datos;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.example.dispensadorfirebase.inicio.InicioOpcionSectores;
import com.example.dispensadorfirebase.principaltemp.Tablet;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TabletDispensador extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ActionBar actionBar;
    ConstraintLayout constrain;
    TextView txtnumeroactual, txtcantidadespera, txtsector;
    MediaPlayer click, click2;
    int baselimite;
    Button btnsupervisor;

    LinearLayout layoutsupervisor;
    LinearLayout layoutrollo;

    int limiteretroceder = 10;
    int retrocesos = 0;

    private SectorDB db = new SectorDB(this);


    Button sumar,restar,reset;
    private AlertDialog Adialog;
    int  Numero_Actual = 0,Cantidad_Espera = 10, Ultimo_numero = 0, limite_espera = 8;
    SectorLocal datos = new SectorLocal();
    String NOMBRELOCALSELECCIONADO=null;
    String NOMBREDELDISPOSITIVO=null;
    private SharedPreferences pref;
    ArrayList<SectoresElegidos> listtemp = new ArrayList<>();;
    private Button configurarnuevamente;
    String LOGOLOCAL=null;
private ImageView logolocal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_dispensador);


        txtnumeroactual = findViewById(R.id.txtNumero_Actual);
        txtcantidadespera= findViewById(R.id.txtscantidad);
        txtsector = findViewById(R.id.txttnombresector);
        sumar = findViewById(R.id.btnSuma);
        restar = findViewById(R.id.btnResta);
        reset = findViewById(R.id.btnReset);
        btnsupervisor = findViewById(R.id.btnllamarsupervisor);
        click = MediaPlayer.create(TabletDispensador.this, R.raw.fin);
        click2 = MediaPlayer.create(TabletDispensador.this, R.raw.ckickk);
        constrain = findViewById(R.id.constrainTablet);

        logolocal = findViewById(R.id.logolocaltablet);

        configurarnuevamente = findViewById(R.id.btn_configurar3);

        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botonregresar();

            }
        });


        validarConfiguracion();

        leerInicioSectores();

        inicializarFirebase();

        conectarFirebase();



        btnsupervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                datos.setLlamarsupervisor(1);


                sumar.setEnabled(false);
                restar.setEnabled(false);
                reset.setEnabled(false);
                btnsupervisor.setEnabled(false);
                click2.start();

                // setProgressDialog();
                delay();

                databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBREBASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(datos.getNombreSector()).setValue(datos);

                }
        });


        sumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sumar();

                if (retrocesos>0){
                    retrocesos--;
                }

            }
        });

        restar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (retrocesos < limiteretroceder){

                    restar();

                }else{

                    Toast.makeText(TabletDispensador.this, "El limite es de 10 turnos para retroceder", Toast.LENGTH_LONG).show();

                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reset();


            }
        });

        if (!LOGOLOCAL.equals("NO")){
            cargarLogo(LOGOLOCAL);
        }

        actionBar = getSupportActionBar();
        /*
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TabletDispensador.this);

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

                        Intent intent= new Intent(TabletDispensador.this, InicioOpcionLocal.class);
                        startActivity(intent);
                        TabletDispensador.this.finish();

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

    @Override
    public void onBackPressed() {


        botonregresar();
       // super.onBackPressed();


    }


    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals("dmr")){
            v = true;
        }

        return v;
    }

    void conectarFirebase(){


        setProgressDialog();

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBREBASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){

                        for (SectoresElegidos sec : listtemp) {
                            if (sec.getIdSectorFirebase().equals(sectores.getIdsector())){
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
                Toast.makeText(TabletDispensador.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();
            }

        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

       // hidebarras();
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

    void sumar(){



        if (datos.sumar()){

            sumar.setEnabled(false);
            restar.setEnabled(false);
            reset.setEnabled(false);

            click2.start();

            // setProgressDialog();
            delay();

            Registrar();
        }else{
            Toast.makeText(TabletDispensador.this, "No hay Clientes para atender", Toast.LENGTH_LONG).show();
        }

    }

    void restar(){


        if (datos.restar()){

            sumar.setEnabled(false);
            restar.setEnabled(false);
            reset.setEnabled(false);

            //setProgressDialog();
            click2.start();
            delay();
            retrocesos++;

            Registrar();
        }else{
            Toast.makeText(TabletDispensador.this, "No hay Clientes en Esperando", Toast.LENGTH_LONG).show();
        }


    }

    void reset(){

        sumar.setEnabled(false);
        restar.setEnabled(false);
        reset.setEnabled(false);
        //setProgressDialog();
        click2.start();
        delay();
        permisos();

    }

    private void permisos() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TabletDispensador.this);
        View mView = getLayoutInflater().inflate(R.layout.alerdiaglog, null);
        final EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);

        mBuilder.setView(mView);
        final AlertDialog dialogg = mBuilder.create();
        dialogg.show();

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPassword.getText().toString().isEmpty()) {


                    String PASSWORD = "dmr";
                    String PASSWORDROOT = "dmrmilrollos";

                    if (mPassword.getText().toString().equals(PASSWORD) || mPassword.getText().toString().equals(PASSWORDROOT) ){

                        retrocesos = 0;
                        datos.reset();
                        Registrar();
                        dialogg.dismiss();
                       // hidebarras();

                    }else{

                        Toast.makeText(TabletDispensador.this,"Acceso Denegado", Toast.LENGTH_SHORT).show();
                        dialogg.dismiss();
                       // hidebarras();
                    }

                } else {

                    mPassword.setError("Faltan Datos");
                    mPassword.requestFocus();
                    //hidebarras();
                }
            }
        });



    }


    void delay(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                btnsupervisor.setEnabled(true);
                sumar.setEnabled(true);
                restar.setEnabled(true);
                reset.setEnabled(true);
                Adialog.dismiss();
            }


        }, 1000);

    }


    void Registrar(){

        click2.start();
        txtnumeroactual.setText(""+datos.getNumeroatendiendo());
        txtcantidadespera.setText(""+datos.getCantidadEspera());



        if (datos.getCantidadEspera()<baselimite){
            datos.setNotificacion(0);
            datos.setNotificaciondeshabilitar(0);
        }else{

            datos.setNotificacion(1);
        }

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBREBASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(datos.getNombreSector()).setValue(datos);




    }


    void Actualizar(){


        txtnumeroactual.setText(""+datos.getNumeroatendiendo());
        txtcantidadespera.setText(""+datos.getCantidadEspera());
        baselimite = datos.getLimite();
        txtsector.setText(datos.getNombreSector());
      //  txtsector.setBackgroundColor(Color.parseColor(datos.getColorSector()));

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
        Adialog.setCancelable(true);

        Adialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                conectarFirebase();
            }
        });


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


    private void validarConfiguracion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("ESTADO", "NO");
        if (estado.equals("NO")){
            regresarConfiguracion();
        }else{
            NOMBREDELDISPOSITIVO = pref.getString("DISPOSITIVO", "NO");
            NOMBRELOCALSELECCIONADO = pref.getString("LOCAL", "NO");
            LOGOLOCAL = pref.getString("LOGOLOCAL","NO");
        }


    }

    private void regresarConfiguracion(){

        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ESTADO", "NO");
        editor.apply();
        Toast.makeText(getApplicationContext(), "No hay registro guardado", Toast.LENGTH_LONG).show();


        Intent intent= new Intent(TabletDispensador.this, InicioOpcionLocal.class);
        startActivity(intent);

        TabletDispensador.this.finish();


    }

    private void leerInicioSectores() {

        try {

            listtemp = db.loadSector();

            if (listtemp!= null || !(listtemp.size() >0) ){
                    if (listtemp.size()>1){
                        //lineartitulo.setVisibility(View.VISIBLE);
                    }else{
                        //lineartitulo.setVisibility(View.GONE);
                    }
            }else{
                regresarConfiguracion();
            }


        } catch (Exception e) {
            Log.e("error", "mensaje error");
            regresarConfiguracion();
        }

    }


}