package com.example.dispensadorfirebase.aplicaciones;

import static com.example.dispensadorfirebase.app.variables.BASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterDispensador;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.Local;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;
import com.example.dispensadorfirebase.inicio.InicioOpcionLocal;
import com.example.dispensadorfirebase.principaltemp.MensajeActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class DispensadorTurno extends AppCompatActivity{

    public static boolean isConnected = false;
    private Handler m_handler = new Handler(); // Main thread
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private boolean permisosimpresora = false;
    private boolean impresoraactiva = false;
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

    private UsbManager usbManager;
    int numeroactual;
    String NOMBRELOCALSELECCIONADO=null;
    String NOMBREDELDISPOSITIVO=null;
    String LOGOLOCAL=null;
    String LOGOLOCALIMPRE=null;
    AdapterDispensador adapter;
    ArrayList<SectorLocal> list;
    ArrayList<SectoresElegidos> listtemp= new ArrayList<>();
    private SectorDB db;
    private SharedPreferences pref;
    private Button configurarnuevamente;
    private ImageView logo;

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

        configurarnuevamente = findViewById(R.id.btn_salir);

        configurarnuevamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                botonregresar();


            }
        });

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

        adapter.setOnNoteSelectedListener(new AdapterDispensador.OnNoteSelectedListener() {
            @Override
            public void onClick(SectorLocal note) {

                click2.start();
                //mostrarEspera(note);
                sumar(note);

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

    private void mostrarEspera(SectorLocal note) {
        Intent v = new Intent(DispensadorTurno.this, MensajeActivity.class);
        v.putExtra("numeroSector", note.getNumeroDispensador());
        v.putExtra("nombreSector", note.getNombreSector());
        v.putExtra("colorSector", note.getColorSector());
        startActivityForResult(v, MENSAJERESULT);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    void sumar(SectorLocal note){

        imprimirNumero(note);

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


        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(BASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);

                    if (sectores.getEstado()==1){

                        for (SectoresElegidos sec : listtemp) {

                            if (sec.getNombre().equals(sectores.getNombreSector())){

                                list.add(sectores);

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

    private void imprimirNumero(SectorLocal datos) {

        byte[] printData = {0};

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);
        int limite = datos.getLimite();

        Charset encoding = Charset.forName("CP437");
        byte[] nombresector= (" "+datos.getNombreSector()).getBytes(encoding);
        byte[] nombreproducto= "   Su Turno es: ".getBytes(encoding);
        byte[] numeroimprimir = (" "+datos.getNumeroDispensador()).getBytes();


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
        builder.appendAbsolutePosition(("   Fecha: " + fecha).getBytes(),0);
        builder.appendLineFeed();
        //**********************

        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);


        builder.endDocument();
        printData = builder.getCommands();


        try {

            if (connection!=null){
                int result = connection.bulkTransfer(usbEndpointOut, printData, printData.length, 1000);
                if (result != -1) {


                    datos.sumarDispensdor();
                    //txt_numeroActualDispensdor.setText(datos.getNumeroDispensador()+"");

                    if (datos.getCantidadEspera()>limite){

                        datos.setNotificacion(1);

                    }else{

                        datos.setNotificacion(0);
                        datos.setNotificaciondeshabilitar(0);

                    }

                    databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(BASEDATOSLOCALES).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(datos.getNombreSector()).setValue(datos);

                } else {
                    Toast.makeText(DispensadorTurno.this, "Impreso desconectada, volver a iniciar la app", Toast.LENGTH_LONG).show();
                }
            }


        } catch (Exception e) {
            Toast.makeText(DispensadorTurno.this, "ERROR Codigo de impresión", Toast.LENGTH_LONG).show();


        }

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
                Toast.makeText(DispensadorTurno.this, "ERROR C conectar", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception var2) {

            impresoraactiva = false;
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


}