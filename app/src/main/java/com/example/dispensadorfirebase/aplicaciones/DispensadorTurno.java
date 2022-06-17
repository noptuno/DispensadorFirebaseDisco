package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dispensadorfirebase.BuildConfig;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.ClaseHistorico;
import com.example.dispensadorfirebase.clase.SectorHistorico;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.printer.sdk.utils.XLog;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DispensadorTurno extends AppCompatActivity{

    public static boolean isConnected = false;
    private Handler m_handler = new Handler(); // Main thread
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private boolean permisosimpresora = false;
    private boolean impresoraactiva = false;
    private boolean impresorapapel = false;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn = null;
    private UsbEndpoint usbEndpointOut = null;
    private Context context = this;
    private AlertDialog Adialog;
    static final int MENSAJERESULT = 0;
    MediaPlayer click, click2;
    Bitmap starLogoImage = null;
    ConstraintLayout constrain;
    ActionBar actionBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView txt_numeroActualDispensdor,txt_nombresector;
    Boolean habilitar_boton_imprimir = true;
    private Button btnsubir;
    private UsbManager usbManager;
    int numeroactual;
    String NOMBRELOCALSELECCIONADO=null;
    String CLIENTE=null;
    String NOMBREDELDISPOSITIVO=null;

    String IDNOMBRELOCALSELECCIONADO=null;
    String LOGOLOCAL=null;
    String LOGOLOCALIMPRE=null;
    String BDCARGADO = null;
    String BDFECHACARGADO = null;
    AdapterDispensador adapter;
    ArrayList<SectorLocal> list;
    ArrayList<SectoresElegidos> listtemp= new ArrayList<>();
    private SectorDB db;
    private SharedPreferences pref;
    private Button configurarnuevamente;
    private ImageView logo;
    private String iddispositivo;
    private  StorageReference mstorage;
    private AlertDialog dialogError;
    private AlertDialog dialogInternet;
    private AlertDialog dialogImpresora;




    @Override
    protected void onPostResume() {
        super.onPostResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensador_turno_recicler);

        inicializarFirebase();

        validarConfiguracion();

        leerSectoresLocales();

        //pedir_permiso_escritura();

        configurarnuevamente = findViewById(R.id.btn_salir);

        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                botonregresar();

            }
        });

        dialogError = ConstruirDialog("PAPEL");

        dialogInternet = ConstruirDialog("INTERNET");

        dialogImpresora = ConstruirDialog("IMPRESORA ERROR");

        logo = findViewById(R.id.imviewlogolocal);

        list = new ArrayList<>();

        adapter = new AdapterDispensador(listtemp.size());

        constrain = findViewById(R.id.constrain);

        //valdiar que el los nombres de sectores en firebase coincidan con los nombres de sercotres locales
        //el que no exista que lo elimine

        click = MediaPlayer.create(DispensadorTurno.this, R.raw.fin);
        click2 = MediaPlayer.create(DispensadorTurno.this, R.raw.ckickk);
        txt_numeroActualDispensdor= findViewById(R.id.txtNumeroActualDispensador);
        txt_nombresector= findViewById(R.id.txtNombreSectorDispensdor);

        /*
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        */

        btnsubir = findViewById(R.id.btnsubir);

        btnsubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //ejecutar

                /*
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://firebasestorage.googleapis.com/v0/b/discotest-c3cc6.appspot.com/o/TEMP%2FDISCO%2FLOCALES%2FCENTRO%2F02-06-2022%2F02-06-2022.txt?alt=media&token=3074e7f7-b0e8-488c-898b-206298f69592")
                        .method("GET", null)
                        .build();
                Response response = client.newCall(request).execute();
*/


                //validar internet

                //VALIDAR QUE YA SE HAYA EJECUTADO ESTE METODO

               // SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
              //  SharedPreferences.Editor editor = pref.edit();
              //  editor.putString("BDCARGADO","false");
             //   editor.apply();

                //METODO SUBIR

                SimpleDateFormat dateFormatcorta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = new Date();
                String fechaCorta = dateFormatcorta.format(date);

                String[] listaArchivos =  context.getFilesDir().list();

                if (listaArchivos != null){

                    int i = 1;
                    for (String nombreArchivoSeleccionado : listaArchivos){


                        Log.e("Ejecutado","cant " + i++ );

                        if (nombreArchivoSeleccionado.toLowerCase(Locale.US).endsWith(".txt")){

                            Log.e("encontrado: ",nombreArchivoSeleccionado );

                            File file = new File(context.getFilesDir(), nombreArchivoSeleccionado);
                            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);

                            StorageReference referenceStorage = mstorage.child(NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child(fechaCorta.replace("/","-")).child(uri.getLastPathSegment());


                           try {

                               referenceStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                   @Override
                                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                       Log.e("Archivo Subido","nombreArchivoSeleccionado: " + "Correcto");
                                       file.delete();

                                   }

                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {

                                       Log.e("Archivo Subido","nombreArchivoSeleccionado: " + "Incorrecto");

                                   }
                               });
                           }catch (Exception e){
                               Log.e("Archivo Subido","nombreArchivoSeleccionado: " + "error");
                           }


                        }

                    }


                }else{
                    Log.e("Archivo","0" );
                }
            }
        });

        adapter.setOnNoteSelectedListener(new AdapterDispensador.OnNoteSelectedListener() {
            @Override
            public void onClick(SectorLocal note) {

                // valdiar internet = true
                // loag

                try{

                    if (isNetDisponible()){
                        if (impresoraactiva){
                            if (getCurrentStatus()){
                                impresorapapel = true;
                                click2.start();
                                habilitar_boton_imprimir = false;
                                GuardarFirebaseTransaccion(note);
                            }else{
                                impresorapapel = false;
                                dialogError.show();
                                note.setLlamarsupervisor(1);
                                databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").child(note.getIdsector()).setValue(note);
                            }
                        }else{
                            usb();
                        }
                    }else{
                        dialogInternet.show();
                    }

                }catch (Exception e){
                    Log.e("Hubo Error",e.toString());
                    note.setLlamarsupervisor(1);
                    databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").child(note.getIdsector()).setValue(note);

                }

                    //dialog mensaje internet
            }

        });

        actionBar = getSupportActionBar();

        context = getApplicationContext();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reciclerviewprincipal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        usb();

        CargarDatos();

        if (!LOGOLOCAL.equals("NO") && !LOGOLOCALIMPRE.equals("NO")){
            cargarLogo(LOGOLOCAL,LOGOLOCALIMPRE);
        }




    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }



    private int escribirimpresora(byte[] printData){
        int ret = 0;
        try {
            if (connection!=null){
                int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                if (result != -1) {
                    ret = result;

                } else {
                    ret = result;


                }
            }
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    private int leerimpresora(byte[] printData){
        int ret = 0;
        try {
            if (connection!=null){
                int result = connection.bulkTransfer(usbEndpointIn, printData, printData.length, 1000);
                if (result != -1) {
                    ret = result;

                } else {
                    ret = result;

                }
            }
        } catch (Exception e) {
            ret = -1;
        }
        return ret;
    }

    public boolean getCurrentStatus() {
       boolean correcto;
        int isSendSuccess = 0;
        try {
            byte[] tempReadBytes = new byte[512];
            int oldDateReadLen = leerimpresora(tempReadBytes);

            if (oldDateReadLen != 0) {

                if (oldDateReadLen < 0){
                    XLog.d("PrinterInstance", "LEER OK negativo= " + oldDateReadLen);

                    //bloquear

                }else{


                    XLog.d("PrinterInstance", "LEER OK  positivo= " + oldDateReadLen);


                }

            }

            for(int i = 0; i < 3; ++i) {
                isSendSuccess = escribirimpresora(new byte[]{29, 40, 72, 6, 0, 48, 48, 49, 50, 51, 52});
                if (isSendSuccess > 0) {
                    XLog.d("PrinterInstance", "ESCRIBIR OK = " + isSendSuccess);
                    break;
                }else{


                }
            }


                byte tapa = this.getDatas(2);
                XLog.d("PrinterInstance", "TAPA" + tapa);

                byte otro = this.getDatas(3);
                XLog.d("PrinterInstance", "OTRO" + otro);

                byte papel = this.getDatas(4);
                XLog.d("PrinterInstance", "PAPEL" + papel);


                if (oldDateReadLen >= 0 && isSendSuccess >= 0){

                    if(tapa == 22 || tapa == 50 || tapa == 54 || papel == 0 || papel ==114 || tapa == 86 ||  tapa == 0 ||tapa == 34 || tapa == 52 ){

                        if (tapa == 0 && otro == 18){
                            correcto = true;

                        }else{
                            correcto = false;
                        }

                    }else{

                        correcto = true;
                    }

                }else{

                    correcto = false;
                }


        } catch (Exception var10) {
            var10.printStackTrace();
            correcto = false;
        }

        return correcto;
    }


    public byte getDatas(int statusType) {

        int readLength = -1;
       // byte[] retStatus = null;
        byte[] command = new byte[]{16, 4, 0};

        try {
            switch(statusType) {
                case 2:
                    command[2] = 2;
                    break;
                case 3:
                    command[2] = 3;
                    break;
                case 4:
                    command[2] = 4;
            }

            for(int m = 0; m < 3; ++m) {
                int sendLength = escribirimpresora(command);
                if (sendLength <= 0) {
                    if (m == 2) {
                        return -2;
                    }
                    Thread.sleep(50L);
                } else {
                    for(int i = 0; i < 10; ++i) {
                        byte[] buffer = new byte[1024];
                        readLength = leerimpresora(buffer);
                        if (readLength > 0) {
                            byte[] retStatus = new byte[readLength];
                            System.arraycopy(buffer, 0, retStatus, 0, readLength);
                            return retStatus[readLength - 1];
                        }

                        Thread.sleep(50L);
                    }

                    if (readLength <= 0) {
                        if (m == 2) {
                            return -1;
                        }
                    }
                }
            }
        } catch (InterruptedException var9) {
            var9.printStackTrace();
            XLog.e("PrinterInstance", "ERROR B" + var9.getMessage());
            return -1;
        }
        return -1;
    }




    private boolean validaryguardar(String pass){
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
            iddispositivo = pref.getString("ID","NO");
            IDNOMBRELOCALSELECCIONADO = pref.getString("IDLOCAL", "NO");
            BDCARGADO = pref.getString("BDCARGADO","NO");
        }

    }

    private void regresarConfiguracion(){
        SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ESTADO", "NO");
        editor.apply();

        Intent intent= new Intent(DispensadorTurno.this, InicioOpcionLocal.class);
        startActivity(intent);

        DispensadorTurno.this.finish();

    }


    private void GuardarFirebaseTransaccion(SectorLocal datos) {


        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatcorta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat horaFormatcorta = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String fechaCompleta = dateFormat.format(date);
        String fechaCorta = dateFormatcorta.format(date);
        String horaCorta = horaFormatcorta.format(date);
        datos.setUltimaFecha(fechaCorta);

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").child(datos.getIdsector()).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                   SectorLocal tabla = mutableData.getValue(SectorLocal.class);

                    if ( tabla == null) {
                        return Transaction.success(mutableData);
                    }
                    String ultimaFecha = tabla.getUltimaFecha();

                    if (!ultimaFecha.equals(fechaCorta)){
                        tabla.setVariableNumero(1);
                    }

                    if(tabla.getCantidadEspera()> tabla.getLimite()){
                        tabla.setNotificacion(1);
                    }

                    tabla.setUltimaFecha(fechaCorta);
                    tabla.sumarDispensdor();


                    mutableData.setValue(tabla);

                    return Transaction.success(mutableData);

                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed,
                                       DataSnapshot currentData) {

                    SectorLocal tabla = currentData.getValue(SectorLocal.class);

                    if(tabla!=null){

                        registrarHistoricoDispensadorFirebase(tabla,fechaCorta,horaCorta);

                        byte[] escpos = PrepararDocumento(tabla,fechaCompleta);

                        if(Imprimir(escpos)){

                            //registrarHistorico(tabla,fechaCorta,horaCorta);

                        }else{

                            impresorapapel=false;
                            //todo dialog mensaje
                        }

                    }
                }
            });
    }


    private void registrarHistoricoDispensadorFirebase(SectorLocal sector,String fecha,String hora) {

        String nombre = (fecha.replace("/","-")).trim();
        int variable = sector.getVariableNumero();
        String idReporte = sector.getIdsector()+"-"+sector.getUltimoNumeroDispensador()+"-"+variable;
        SectorHistorico datos = new SectorHistorico();

        datos.setIdSector(sector.getIdsector());
        datos.setNombreSector(sector.getNombreSector());
        datos.setIdDispositivo(iddispositivo);
        datos.setNombreDispositivo(iddispositivo);
        datos.setNumeroDispensado(sector.getUltimoNumeroDispensador());
        datos.setFecha_entrega(fecha);
        datos.setHora_entrega(hora);
        datos.setFecha_atencion("");
        datos.setHora_atencion("");
        datos.setLimite_superado(sector.getNotificacion());


        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("REPORTE").child(nombre).child(idReporte).setValue(datos);
    }



    private void leerSectoresLocales() {

        db = new SectorDB(this);

        try {
            db = new SectorDB(this);
            listtemp = db.loadSector();
            listtemp.size();

            if ((listtemp == null) || (listtemp.size() == 0) ){
                regresarConfiguracion();
            }

        } catch (Exception e) {
            regresarConfiguracion();
            Log.e("error", "mensaje mostrar bse local");
        }

    }



    private void botonregresar() {

        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
        View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DispensadorTurno.this);

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

                        Intent intent= new Intent(DispensadorTurno.this, InicioOpcionLocal.class);
                        startActivity(intent);

                        DispensadorTurno.this.finish();

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


    AlertDialog ConstruirDialog(String a){


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog));

        builder.setCancelable(false);


        builder.setMessage("ERROR: " + a)
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
        });
        // Create the AlertDialog object and return it
       return builder.create();

    }



    @Override
    public void onBackPressed() {

    }



    private void cargarLogo(String logolocal,String logoimpre) {

        Uri templogolocal = Uri.parse(logolocal);
        Uri templogolocalimpre = Uri.parse(logoimpre);

        Glide.with(getApplicationContext()).load(templogolocal).into(logo);
        Glide.with(getApplicationContext()).load(templogolocalimpre).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();

                if (bitmap!=null){
                    starLogoImage = bitmap;
                }else{
                    starLogoImage = null;
                }
            }
        });

    }



    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getApplicationContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void CargarDatos() {

        setProgressDialog();

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDNOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){

                        for (SectoresElegidos sec : listtemp) {

                            if (sec.getIdSectorFirebase().equals(sectores.getIdsector())){

                                list.add(sectores);
                                break;

                            }

                            Log.i("---> Base de ds: ", sectores.toString());
                        }
                    }
                }
          Adialog.dismiss();
                actualizarReciclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DispensadorTurno.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();

            }
        });


    }

    public void actualizarReciclerView() {
        adapter.setNotes(list);
        adapter.notifyDataSetChanged();
    }



    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        mstorage = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //se creo una actividad para gejecutar este metodo
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();


    }


    //código
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


    private Boolean Imprimir(byte[] printData){
        Boolean ret = false;
        try {
            if (connection!=null){
                int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                if (result != -1) {
                    ret = true;

                } else {
                    ret = false;

                }
            }
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    private byte[] PrepararDocumento(SectorLocal datos,String fechaCompleta) {

        Charset encoding = Charset.forName("CP437");
        byte[] nombresector= (" "+datos.getNombreSector()).getBytes(encoding);
        byte[] nombreproducto= "   Su Turno es: ".getBytes(encoding);
        byte[] numeroimprimir = (" "+datos.getUltimoNumeroDispensador()).getBytes();
        //Bitmap starLogoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.logodiscopeque);
        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPos);
        builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
        builder.beginDocument();
        if (starLogoImage!=null){
            builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
            builder.appendBitmap(starLogoImage, false);
            builder.appendLineFeed();
        }
        //*********************************
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
        builder.appendMultiple(2, 2);
        builder.appendAbsolutePosition(nombresector,0);
        builder.appendLineFeed();
        builder.appendMultiple(1, 1);
        builder.appendAbsolutePosition(nombreproducto,0);
        builder.appendLineFeed();
        builder.appendLineSpace(50);
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);
        builder.appendMultiple(10, 10);
        builder.appendAbsolutePosition(numeroimprimir,0);
        builder.appendLineFeed();
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Left);

        builder.appendMultiple(0, 0);
        builder.appendAbsolutePosition(("   Fecha: " + fechaCompleta).getBytes(),0);
        builder.appendLineFeed();
        //**********************
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();

        return  builder.getCommands();

    }

    private String leerHistorico(String fecha) {

        //Crear Clase de Registro
        Boolean exist = false;

        String nombreArchivo = (fecha.replace("/","-")+".txt").trim();


        String[] archivos = fileList();


        if (existe(archivos, nombreArchivo)){
            exist = true;
        }

        if (exist){
            return nombreArchivo;
        }else{
            return nombreArchivo = null;
        }

    }


    private void registrarHistorico(SectorLocal sector,String fecha,String hora) {

        //Crear Clase de Registro


        String nombre = (fecha.replace("/","-")+".txt").trim();
        SectorHistorico datos = new SectorHistorico();

       // datos.setCliente(CLIENTE);
      //  datos.setLocal(NOMBRELOCALSELECCIONADO);
       // datos.setId(iddispositivo);
        //datos.setNombreDispositivo(iddispositivo);
       // datos.setSector(sector.getNombreSector());
      //  datos.setTicket(sector.getUltimoNumeroDispensador());
       // datos.setFecha_entrega(fecha);
      //  datos.setHora_entrega(hora);
       // datos.setFecha_atencion("");
        //datos.setHora_atencion("");


        String[] archivosEncontrados = context.getFilesDir().list();

        Gson gson = new Gson();

        //valdiar que exista

        if (existe(archivosEncontrados, nombre)){


                try{

                    InputStreamReader archivo = new InputStreamReader(openFileInput(nombre));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String todo = "";
                    while (linea != null) {
                        todo = todo + linea + "\n";
                        linea = br.readLine();
                    }

                    br.close();
                    archivo.close();
                    ClaseHistorico historico = gson.fromJson(todo, ClaseHistorico.class);
                    List<SectorHistorico> tickets = historico.getHistorico();
                    tickets.add(datos);
                    historico.setHistorico(tickets);
                    String JSONn = gson.toJson(historico);
                    grabar(JSONn,nombre);

                    Log.e("Json grabado ",JSONn);


                }catch (Exception e){
                    // notificacion importante realizar
                }

        }else{

            List<SectorHistorico> tickets = new ArrayList<>();
            tickets.add(datos);
            ClaseHistorico historico = new ClaseHistorico(NOMBREDELDISPOSITIVO, tickets);
            String JSON = gson.toJson(historico);
            grabar(JSON,nombre);

        }

    }


    private boolean existe(String[] archivos, String archbusca) {

        Boolean a = false;
        for (int f = 0; f < archivos.length; f++){
            Log.e("Base Archivos: ",archivos[f]);
            if (archbusca.equals(archivos[f])){

                a = true;
                break;
            }

        }

        return a;
    }

    public void grabar(String v,String direccion_nombre) {
        try {

            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(direccion_nombre, Activity.MODE_PRIVATE));
            archivo.write(v);
            archivo.flush();
            archivo.close();


        } catch (IOException e) {
            Log.e("error",e.toString());
        }

        Toast t = Toast.makeText(this, "Los datos fueron grabados",Toast.LENGTH_SHORT);
        t.show();
    }

    private void usb() {

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.e("Dispositivos", device.getDeviceName() + " + " + device.getVendorId() + " + " + device.getProductId());

            if (device.getVendorId() == 1155  && device.getProductId() == 22304) {

                if (usbManager.hasPermission(device)) {
                    permisosimpresora = true;
                    conectarImpresora(device);
                } else {
                    permisosimpresora = false;
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                    DispensadorTurno.this.registerReceiver(usbReceiver, filter);
                    usbManager.requestPermission(device, mPermissionIntent);
                }

            }
        }


    }

    private void conectarImpresora(UsbDevice device) {
        try {

            UsbInterface usbInterface = device.getInterface(0);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint end = usbInterface.getEndpoint(i);
                if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                    usbEndpointIn = end;
                } else {
                    usbEndpointOut = end;
                }
            }
            connection = usbManager.openDevice(device);

            if (connection != null && connection.claimInterface(usbInterface, true)) {
                impresoraactiva = true;
                Toast.makeText(DispensadorTurno.this, "Conectado", Toast.LENGTH_SHORT).show();

            }else{
                impresoraactiva = false;
                //todo dialog mensaje
            }

        } catch (Exception var2) {

            impresoraactiva = false;
            dialogImpresora.show();
            Toast.makeText(DispensadorTurno.this, "ERROR D conectar", Toast.LENGTH_SHORT).show();
        }



    }

    void close(){

        try {
            if (this.connection != null) {
                this.connection.releaseInterface(this.usbInterface);
                this.connection.close();
                this.connection = null;
            }

        } catch (Exception var2) {
            var2.printStackTrace();
        }


    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    DispensadorTurno.this.unregisterReceiver(usbReceiver);
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(DispensadorTurno.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                        permisosimpresora = true;
                        conectarImpresora(device);
                    } else {
                        Toast.makeText(DispensadorTurno.this, "Permiso no aceptado, OBLIGATORIO", Toast.LENGTH_LONG).show();
                        permisosimpresora = false;
                        finish();
                    }

                }
            }
        }
    };



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case MENSAJERESULT: {
                if (resultCode == RESULT_OK) {

                    Log.e("RECIBIDO", "OK");
                }
                break;
            }
        }
    }
/*
    private static String SPACE = "  ";

    public static String formatJson(String json) {
        StringBuffer result = new StringBuffer();

        int length = json.length();
        int number = 0;
        char key = 0;

        // Atraviesa la cadena de entrada.
        for (int i = 0; i < length; i++) {
            // 1. Obtiene el personaje actual.
            key = json.charAt(i);

            // 2. Si el carácter actual es un corchete frontal o un corchete frontal, haga lo siguiente:
            if ((key == '[') || (key == '{')) {
                // (1) Si hay caracteres antes y los caracteres son ":", imprima: avance de línea y sangría de cadena de caracteres.
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append("allow");
                    result.append(":");
                    //result.append(indent(number));
                }

                // (2) Imprimir: carácter actual.
                result.append(key);

                // (3) Deben seguirse los corchetes delanteros, los corchetes delanteros y los saltos de línea. Impresión: avance de línea.
                result.append('\n');

                // (4) Cada vez que aparecen el corchete frontal y el corchete frontal, el número de sangría aumenta en uno. Impresión: sangra la nueva línea.
                number++;
                result.append(indent(number));

                // (5) Pasar al siguiente ciclo.
                continue;
            }

            // 3. Si el carácter actual es un corchete posterior o un corchete posterior, haga lo siguiente:
            if ((key == ']') || (key == '}')) {
                // (1) Los corchetes traseros, los corchetes traseros, deben envolver antes. Impresión: avance de línea.
                result.append('\n');

                // (2) Cada vez que aparecen el corchete trasero y el corchete trasero, el número de sangría se reduce en uno. Impresión: sangría.
                number--;
                result.append(indent(number));

                // (3) Imprimir: carácter actual.
                result.append(key);

                // (4) Si hay caracteres después del carácter actual y el carácter no es ",", imprime: salto de línea.
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    //result.append('\n');
                }

                // (5) Continuar con el siguiente ciclo.
                continue;
            }
            // 4. Si el carácter actual es una coma. Salto de línea después de la coma y sangría sin cambiar el número de sangrías.
            if ((key == ',')) {
                result.append(key);
                //result.append('\n');
                result.append(indent(number));
                continue;
            }
            // 5. Imprimir: carácter actual.
            result.append(key);
        }
        return result.toString();
    }
    private static String indent(int number) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < number; i++) {
            result.append(SPACE);
        }
        return result.toString();
    }

    public static String formatJson2(String json) {
        StringBuffer result = new StringBuffer();
        result.append("{");
        result.append("\n");
        result.append(SPACE);
        // Agregar cadena json
        result.append('"');
        result.append("allow");
        result.append('"');
        result.append(":");
        result.append(json);
        result.append("\n");
        result.append("}");
        return result.toString();
    }
    */
}