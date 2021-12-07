package com.example.dispensadorfirebase.principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.clase.Datos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class Dispensador extends AppCompatActivity {

    public static boolean isConnected = false;
    private Handler m_handler = new Handler(); // Main thread
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private boolean permisosimpresora = false;
    private boolean impresoraactiva = false;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn = null;
    private UsbEndpoint usbEndpointOut = null;
    private Context context;
    private AlertDialog Adialog;
    static final int MENSAJERESULT = 0;
    MediaPlayer click, click2;

    String Local = "Datos";
    String Dispensador = "dispensador1";

    ConstraintLayout constrain;
    ActionBar actionBar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout layout;
    TextView txt_numeroActualDispensdor,txt_nombresector;
    int baselimite;
    Datos datos ;

    private UsbManager usbManager;

    int numeroactual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensador);

        inicializarFirebase();

        datos = new Datos(0,0,0,0,1,"otros","#2196F3",0,0,0);

        layout = findViewById(R.id.layoutsec);


        click = MediaPlayer.create(Dispensador.this, R.raw.fin);
        click2 = MediaPlayer.create(Dispensador.this, R.raw.ckickk);


        txt_numeroActualDispensdor= findViewById(R.id.txtNumeroActualDispensador);
        txt_nombresector= findViewById(R.id.txtNombreSectorDispensdor);

        constrain = findViewById(R.id.constrain);


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                click2.start();
                mostrarEspera();
                sumar();
            }
        });


        actionBar = getSupportActionBar();
        hidebarras();
        constrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidebarras();
            }
        });
        context = getApplicationContext();

        usb();


        CargarDatos();



    }



    private void mostrarEspera() {
        Intent v = new Intent(Dispensador.this, MensajeActivity.class);
        v.putExtra("numeroSector", datos.getNumeroDispensador());
        v.putExtra("nombreSector", datos.getNombreSector());
        v.putExtra("colorSector", datos.getColorSector());
        startActivityForResult(v, MENSAJERESULT);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }




    private void CargarDatos() {

        setProgressDialog();
        databaseReference.child("Datos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              //  EnableDialog(true,"true");
                        for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {

                            datos = objSnaptshot.getValue(Datos.class);
                            baselimite = datos.getLimite();
                            txt_nombresector.setText(""+datos.getNombreSector());
                            txt_numeroActualDispensdor.setText(""+datos.getNumeroDispensador());
                            layout.setBackgroundColor(Color.parseColor(datos.getColorSector()));

                        }

          Adialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Dispensador.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();

            }
        });


    }

    void ajustarclase (){


    }

    void sumar(){

        imprimirNumero();

    }


    void registrar(){

        m_handler.post(new Runnable() {
            @Override
            public void run() {
                try {


                } catch (Exception e) {
                    Log.e("errorW", "mensaje");
                }
            }
        });




    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //se creo una actividad para gejecutar este metodo
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
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

    private void imprimirNumero() {

        byte[] printData = {0};

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat.format(date);


        Charset encoding = Charset.forName("CP437");
        byte[] nombresector= datos.getNombreSector().getBytes(encoding);
        byte[] nombreproducto= "Su Turno es: ".getBytes(encoding);
        byte[] numeroimprimir = (""+datos.getNumeroDispensador()).getBytes();

        Bitmap starLogoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.logodiscopeque);

        ICommandBuilder builder = StarIoExt.createCommandBuilder(StarIoExt.Emulation.EscPos);
        builder.appendCodePage(ICommandBuilder.CodePageType.UTF8);
        builder.beginDocument();
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendBitmap(starLogoImage, true);
        builder.appendLineFeed();

        //*********************************
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendMultiple(2, 2);
        builder.appendAbsolutePosition(nombresector,0);
        builder.appendLineFeed();
        builder.appendMultiple(1, 1);
        builder.appendAbsolutePosition(nombreproducto,0);
        builder.appendLineFeed();
        builder.appendLineSpace(50);
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);
        builder.appendMultiple(10, 10);
        builder.appendAbsolutePosition(numeroimprimir,0);
        builder.appendLineFeed();
        builder.appendAlignment(ICommandBuilder.AlignmentPosition.Center);

        builder.appendMultiple(0, 0);
        builder.appendAbsolutePosition(("Fecha: " + fecha).getBytes(),0);
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
                    txt_numeroActualDispensdor.setText(datos.getNumeroDispensador()+"");

                    if (datos.getCantidadEspera()>baselimite){

                        datos.setNotificacion(1);

                    }else{

                        datos.setNotificacion(0);
                        datos.setNotificaciondeshabilitar(0);

                    }
                    
                    databaseReference.child(Local).child(Dispensador).setValue(datos);

                } else {
                    Toast.makeText(Dispensador.this, "ERROR A: imprimir", Toast.LENGTH_LONG).show();
                }
            }


        } catch (Exception e) {
            Toast.makeText(Dispensador.this, "ERROR B: imprimir", Toast.LENGTH_LONG).show();


        }

    }


    private void cargarLista2() {

        m_handler.post(new Runnable() {
            @Override
            public void run() {
                try {



                } catch (Exception e) {
                    Log.e("errorW", "mensaje");
                }
            }
        });

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
                    Dispensador.this.registerReceiver(usbReceiver, filter);
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
                Toast.makeText(Dispensador.this, "Conectado", Toast.LENGTH_SHORT).show();
            }else{
                impresoraactiva = false;
                Toast.makeText(Dispensador.this, "ERROR C conectar", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception var2) {

            impresoraactiva = false;
            Toast.makeText(Dispensador.this, "ERROR D conectar", Toast.LENGTH_SHORT).show();
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
                    Dispensador.this.unregisterReceiver(usbReceiver);
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(Dispensador.this, "Permiso aceptado", Toast.LENGTH_SHORT).show();
                        permisosimpresora = true;
                        conectarImpresora(device);
                    } else {
                        Toast.makeText(Dispensador.this, "Permiso no aceptado, OBLIGATORIO", Toast.LENGTH_LONG).show();
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