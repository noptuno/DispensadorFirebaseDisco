package com.example.dispensadorfirebase.inicio;

import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;
import static com.example.dispensadorfirebase.app.variables.NOMBRETABLACLIENTES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterLocal;
import com.example.dispensadorfirebase.adapter.AdapterSectorLocal;
import com.example.dispensadorfirebase.administrador.AsignarSectoress;
import com.example.dispensadorfirebase.administrador.CrearLocalDialog;
import com.example.dispensadorfirebase.administrador.ListaLocales;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.DisplayPequeño;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Principal;
import com.example.dispensadorfirebase.basedatossectoreselegidos.SectorDB;
import com.example.dispensadorfirebase.clase.Local;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.Sectores;
import com.example.dispensadorfirebase.clase.SectoresElegidos;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InicioOpcionSectores extends AppCompatActivity {

    ArrayList<Sectores> listnombresectores;
    ArrayList<SectorLocal> listsectoreslocal;
    Button RegistroSectores,btnAsignar;
    AdapterSectorLocal adapter;
    AlertDialog Adialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencesectores;
    private SectorDB db;
private Button configurar;
    TextView cantidadsectoreselegidos;
    private int cantidadelegida = 0;
    private int cantidadmaxima = 0;
    private TextView maximoSectores;
    ActionBar actionBar;
private TextView localseleccionado, dispositivoseleccionado;
    String NOMBRELOCALSELECCIONADO=null;
    String DISPOSITIVO=null;
    String CLIENTE=null;
    String IDLOCALSELECCIONADO=null;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_sectores);

        //FIREBASE
        inicializarFirebase();
        ocultarbarra();

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        CLIENTE= pref.getString("CLIENTE", "NO");
        DISPOSITIVO = pref.getString("DISPOSITIVO", "NO");
        NOMBRELOCALSELECCIONADO = pref.getString("NOMBRELOCALSELECCIONADO", "NO");
        IDLOCALSELECCIONADO = pref.getString("IDLOCAL", "NO");

        if (CLIENTE.equals("NO") || DISPOSITIVO.equals("NO") || IDLOCALSELECCIONADO.equals("NO")){
            finish();
        }


        configurar = findViewById(R.id.btnGuardarConfig);
        localseleccionado = findViewById(R.id.txtlocal);
        dispositivoseleccionado= findViewById(R.id.txtdispositivo);
        localseleccionado.setText(NOMBRELOCALSELECCIONADO);
        dispositivoseleccionado.setText(DISPOSITIVO);

        maximoSectores= findViewById(R.id.txtmaximosectores);
       cantidadsectoreselegidos= findViewById(R.id.txtcantelegidos);

        listnombresectores = new ArrayList<>();
        listsectoreslocal = new ArrayList<>();

        adapter = new AdapterSectorLocal();

        //eliminar base datos local
        eliminarSectoresElegidos();

        configurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cantidadmaxima>= cantidadelegida){

                    Intent intent = null;

                    if (DISPOSITIVO.equals("DISPLAY 21PLG")){
                        intent  = new Intent(InicioOpcionSectores.this, DisplayGrande.class);

                    } else if (DISPOSITIVO.equals("DISPLAY 15PLG")){
                         intent = new Intent(InicioOpcionSectores.this, DisplayPequeño.class);

                    }else if (DISPOSITIVO.equals("TABLET 10PLG")){

                         intent = new Intent(InicioOpcionSectores.this, TabletDispensador.class);
                    }
                    else if (DISPOSITIVO.equals("DISPENSADOR")){
                         intent = new Intent(InicioOpcionSectores.this, DispensadorTurno.class);

                    } else if (DISPOSITIVO.equals("SUPERVISOR")){

                         intent = new Intent(InicioOpcionSectores.this, Supervisor_Principal.class);
                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                        try {

                            String id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
                            SharedPreferences pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("COMPLETADO","SI");
                            editor.putString("ID",id);

                            editor.apply();


                            // guardar base de datos Firebase
                           // Toast.makeText(InicioOpcionSectores.this, "Guardo: " + id , Toast.LENGTH_LONG).show();

                            InicioOpcionSectores.this.finish();

                        }catch (Exception e){

                            Toast.makeText(InicioOpcionSectores.this, "Hubo un Error", Toast.LENGTH_LONG).show();
                        }

                }else{
                    Toast.makeText(InicioOpcionSectores.this, "Debe Elegir menos Sectores para Este Dispositivo", Toast.LENGTH_LONG).show();
                }


            }
        });

        adapter.setOnNoteSelectedListener(new AdapterSectorLocal.OnNoteSelectedListener() {
            @Override
            public void onClick(SectorLocal note) {

                //habilitar y registrar en firebase , crear hijo con este nombre sector con todos sus datos
            }
        });

        adapter.setOnDetailListener(new AdapterSectorLocal.OnNoteDetailListener() {
            @Override
            public void onDetail(SectorLocal note) {

                //crear base datos local

                SectoresElegidos sectorelegido = new SectoresElegidos();

                sectorelegido.setIdSectorFirebase(note.getIdsector());
                sectorelegido.setUltimonumero(note.getNumeroatendiendo());
                registrarSectorElegido(sectorelegido);
                mostrarBaseLocalSectoresElegidos();

                //aqui uso el habilitador para guardar en el dispositivo los sectores que va a utilizar

               // databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRELOCALSELECCIONADO).child("SECTORES").child(note.getNombreSector()).setValue(note);

            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerelegirsector);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        //tempral:

        // crear todos los sectores en la tabla del lcoal en firebase

        cargarListaSectoresLocales();
        limitesectores();
    }
    private void ocultarbarra() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private void limitesectores() {


        if (DISPOSITIVO.equals("DISPLAY 21PLG")){
            cantidadmaxima = 3;
            maximoSectores.setText("3");

        } else if (DISPOSITIVO.equals("DISPLAY 15PLG")){
            cantidadmaxima = 1;
            maximoSectores.setText("1");

        }else if (DISPOSITIVO.equals("TABLET 10PLG")){
            cantidadmaxima = 1;
            maximoSectores.setText("1");

        }
        else if (DISPOSITIVO.equals("DISPENSADOR")){
            cantidadmaxima = 3;
            maximoSectores.setText("3");

        } else if (DISPOSITIVO.equals("SUPERVISOR")){
            cantidadmaxima = 6;
            maximoSectores.setText("6");

        }


    }

    private void cargarListaSectoresLocales() {

        setProgressDialog();

        this.databaseReferencesectores.child(NOMBREBASEDEDATOSFIREBASE).child(NOMBRETABLACLIENTES).child(CLIENTE).child(NOMBREBASEDATOSLOCALES).child(IDLOCALSELECCIONADO).child("SECTORES").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listsectoreslocal.clear();
                eliminarSectoresElegidos();
                cantidadelegida=0;

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    SectorLocal sectores = objSnaptshot.getValue(SectorLocal.class);


                    if (sectores!=null){

                        if (sectores.getEstado()==1){

                            SectoresElegidos sectorElegido = new SectoresElegidos();

                            String idsector = sectores.getIdsector();
                            sectorElegido.setIdSectorFirebase(idsector);
                            int ultimonumero = sectores.getUltimoNumeroDispensador();
                            sectorElegido.setUltimonumero(ultimonumero);
                            registrarSectorElegido(sectorElegido);


                            mostrarBaseLocalSectoresElegidos();
                            listsectoreslocal.add(sectores);
                        }
                    }
                }

                Adialog.dismiss();
                actualizarReciclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InicioOpcionSectores.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();
            }

        });

    }

    public boolean registrarSectorElegido(SectoresElegidos sectorElegido) {

        try {
            db = new SectorDB(this);

            if (db.validar(sectorElegido.getIdSectorFirebase())){
                db.eliminarSector(sectorElegido.getIdSectorFirebase());
                cantidadelegida--;
            }else{
                cantidadelegida++;
                db.insertarSector(sectorElegido);
            }
            cantidadsectoreselegidos.setText("" + cantidadelegida);

            return true;

        } catch (Exception e) {
            Log.e("error", "mensaje registro o eliminar");
            return false;
        }

    }
    public boolean eliminarSectoresElegidos() {

        try {
            db = new SectorDB(this);
                db.eliminarAll();

            return true;

        } catch (Exception e) {
            Log.e("error", "mensaje eliminar all");
            return false;
        }

    }


    void mostrarBaseLocalSectoresElegidos(){

        try {
            db = new SectorDB(this);
            ArrayList<SectoresElegidos> list = db.loadSector();
            for (SectoresElegidos sectores : list) {

                Log.i("---> Base de datos: ", sectores.toString());

            }


        } catch (Exception e) {
            Log.e("error", "mensaje mostrar bse local");
        }
    }

    public void actualizarReciclerView() {

        cantidadsectoreselegidos.setText("" + cantidadelegida);
        adapter.setNotes(listsectoreslocal);
        adapter.notifyDataSetChanged();
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
        Adialog.show();
        Adialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                cargarListaSectoresLocales();

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



    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferencesectores = firebaseDatabase.getReference();
    }



}