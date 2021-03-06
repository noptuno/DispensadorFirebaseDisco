package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLAERROR;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLAREPORTE;
import static com.example.dispensadorfirebase.app.variables.ROOTINTERNO;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import com.example.dispensadorfirebase.clase.SectorHistorico;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.example.dispensadorfirebase.inicio.InicioOpcionSectores;
import com.example.dispensadorfirebase.principaltemp.Tablet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class TabletDispensador extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ActionBar actionBar;
    ConstraintLayout constrain;
    TextView txtnumeroactual, txtcantidadespera, txtsector;
    MediaPlayer click, click2;
    int baselimite;
    Button btnsupervisor;
    String CLIENTE=null;
    LinearLayout layoutsupervisor;
    LinearLayout layoutrollo;

    int limiteretroceder = 5;
    int retrocesos = 0;
    String IDNOMBRELOCALSELECCIONADO=null;
    private SectorDB db = new SectorDB(this);
    Button sumar,restar,reset;
    private AlertDialog Adialog;
    int  Numero_Actual = 0,Cantidad_Espera = 10, Ultimo_numero = 0, limite_espera = 8;
    SectorLocal datos = new SectorLocal();
    String NOMBRELOCALSELECCIONADO=null;
    String DISPOSITIVO=null;
    private SharedPreferences pref;
    ArrayList<SectoresElegidos> listtemp = new ArrayList<>();;
    private Button configurarnuevamente;
    String LOGOLOCAL=null;
    String COMPLETADO=null;

    int variabletablet = 1;
    int variabledispensador = 1;

    private AlertDialog dialogInternet;

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

       // regresarConfiguracion();
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

                databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").child(datos.getIdsector()).setValue(datos);

                }
        });


        dialogInternet = ConstruirDialog("INTERNET");


        sumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetDisponible()){
                    sumar();

                    if (retrocesos>0){
                        retrocesos--;
                    }
                }else{
                    dialogInternet.show();
                }

            }
        });

        restar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isNetDisponible()){

                    if (retrocesos < limiteretroceder){

                        restar();

                    }else{

                        Toast.makeText(TabletDispensador.this, "El limite es de 10 turnos para retroceder", Toast.LENGTH_LONG).show();

                    }
                }else{

                    dialogInternet.show();
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

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }


    private void cargarLogo(String LinkLogo) {

        Uri fondo = Uri.parse(LinkLogo);
        Glide.with(getApplicationContext()).load(fondo).into(logolocal);


    }

    AlertDialog ConstruirDialog(String a){


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog));

        builder.setCancelable(false);


        builder.setMessage("INTERNET: " + a)
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

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
                        editor.putString("COMPLETADO", "NO");
                        editor.apply();

                        Intent intent= new Intent(TabletDispensador.this, InicioOpcionLocal.class);
                        startActivity(intent);
                        TabletDispensador.this.finish();

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
                                editor.putString("COMPLETADO", "NO");
                                editor.apply();

                                Intent intent= new Intent(TabletDispensador.this, InicioOpcionLocal.class);
                                startActivity(intent);
                                TabletDispensador.this.finish();

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


    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals(ROOTINTERNO)){
            v = true;
        }

        return v;
    }




    void conectarFirebase(){


        setProgressDialog();

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
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



        if (datos.sumarTablet()){


            txtnumeroactual.setText(""+datos.getNumeroatendiendo());
            txtcantidadespera.setText(""+datos.getCantidadEspera());
            sumar.setEnabled(false);
            restar.setEnabled(false);
            reset.setEnabled(false);
            click2.start();
            // setProgressDialog();
            delay();

            Registrar(true);


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

                   Registrar(false);
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

                    String PASSWORD = ROOTINTERNO;
                    String PASSWORDROOT = "dmrmilrollos";

                    if (mPassword.getText().toString().equals(PASSWORD) || mPassword.getText().toString().equals(PASSWORDROOT) ){

                        retrocesos = 0;
                        datos.reset();
                        Registrar(false);
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


    void Registrar(boolean sum){

       // SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatcorta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        SimpleDateFormat horaFormatcorta = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

       String fechaCorta = dateFormatcorta.format(date);
        String horaCorta = horaFormatcorta.format(date);

        if (datos.getCantidadEspera()<baselimite){
            datos.setNotificacion(0);
            datos.setNotificaciondeshabilitar(0);
        }else{
            datos.setNotificacion(1);
        }

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").child(datos.getIdsector()).setValue(datos);

        if (sum){
            registrarHistoricoDispensadorFirebase(datos,horaCorta,fechaCorta);
        }

    }


    private void registrarHistoricoDispensadorFirebase(SectorLocal sector,String hora,String fechatcorta) {

        String nombrefecha = (fechatcorta.replace("/","-")).trim();
        int variable = sector.getVariableNumeroTablet();
        String idReporte = sector.getIdsector()+"-"+sector.getNumeroatendiendo()+"-"+variable;

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBRETABLAREPORTE).child(IDNOMBRELOCALSELECCIONADO).child(nombrefecha).child(idReporte).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                SectorHistorico tablaHistorico = mutableData.getValue(SectorHistorico.class);

                if ( tablaHistorico == null) {
                    return Transaction.success(mutableData);
                }

                tablaHistorico.setFecha_atencion(fechatcorta);
                tablaHistorico.setHora_atencion(hora);
                mutableData.setValue(tablaHistorico);
                return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                SectorLocal tabla = currentData.getValue(SectorLocal.class);

                if(tabla==null){

                    registrarErrorDispensador(sector,fechatcorta,hora);
                }

            }
        });

    }

    private void registrarErrorDispensador(SectorLocal sector,String fecha,String hora) {


        String nombre = (fecha.replace("/","-")).trim();
        int variable = sector.getVariableNumero();
        String idReporte = sector.getIdsector()+"-"+sector.getNumeroatendiendo()+"-"+variable;
        SectorHistorico datostemp = new SectorHistorico();

        datostemp.setIdSector(sector.getIdsector());
        datostemp.setNombreSector(sector.getNombreSector());
        datostemp.setNumeroDispensado(sector.getUltimoNumeroDispensador());
        datostemp.setFecha_entrega(fecha);
        datostemp.setHora_entrega(hora);
        datostemp.setFecha_atencion("");
        datostemp.setHora_atencion("");
        datostemp.setIdLocal(IDNOMBRELOCALSELECCIONADO);
        datostemp.setLimite_superado(sector.getNotificacion());

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBRETABLAERROR).child(IDNOMBRELOCALSELECCIONADO).child(nombre).child("TABLET").child(idReporte).setValue(datostemp);

    }

    void Actualizar(){

        txtnumeroactual.setText(""+datos.getNumeroatendiendo());
        txtcantidadespera.setText(""+datos.getCantidadEspera());
        baselimite = datos.getLimite();
        txtsector.setText(datos.getNombreSector());
      //txtsector.setBackgroundColor(Color.parseColor(datos.getColorSector()));

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

    private void regresarConfiguracion(){

        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("COMPLETADO", "NO");
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