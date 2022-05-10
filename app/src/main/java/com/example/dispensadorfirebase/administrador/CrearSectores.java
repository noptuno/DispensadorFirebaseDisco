package com.example.dispensadorfirebase.administrador;

import static com.example.dispensadorfirebase.app.variables.BASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.BASEDATOSSECTORESTEMP;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.adapter.AdapterSectorLocal;
import com.example.dispensadorfirebase.adapter.AdapterSectores;
import com.example.dispensadorfirebase.app.variables;
import com.example.dispensadorfirebase.clase.Local;
import com.example.dispensadorfirebase.clase.SectorLocal;
import com.example.dispensadorfirebase.clase.Sectores;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class CrearSectores extends AppCompatActivity {
    String localseleccionado;
    ArrayList<Sectores> listnombresectores;
    private AdapterSectores adapter; //CAMBIAR ADAPTER SECTORES
    private static final int GALERY_INTENT_V = 1;
    private static final int GALERY_INTENT_H = 2;
    private Button btnregistrar, btnnuevosector, btnred, btnblue, btnorange, btngreen, btnpurple,btnblueOscuro,subirh,subirv;
    private EditText nombre, limite;
    private TextView txtcolorseleccionado;

    private String color = "#B30D0D";
    private LinearLayout layoutPrincipal;
    AlertDialog Adialog;

    //referencia firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String CLIENTE;

    private  StorageReference mstorage;
    Uri fondoh = Uri.parse(""), fondov= Uri.parse("");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_sectores);

        //FIREBASE
        inicializarFirebase();
        mstorage = FirebaseStorage.getInstance().getReference();

        subirh = findViewById(R.id.btnsubirH);
        subirv = findViewById(R.id.btnSubirV);
        CLIENTE = getIntent().getStringExtra("CLIENTE");

        listnombresectores = new ArrayList<>();
        layoutPrincipal = findViewById(R.id.layoutbotones);

        botones();

        CloseTeclado();

        btnregistrar= findViewById(R.id.btnAñadirSector);
        nombre= findViewById(R.id.txtnombre);
        limite= findViewById(R.id.txtlimite);
        txtcolorseleccionado = findViewById(R.id.txtcolorseleccionado);

        //btnnuevosector= findViewById(R.id.btnnuevosector);
/*
        btnnuevosector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                limpiar();

            }
        });
*/

        subirh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALERY_INTENT_H);

            }
        });
                subirv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent,GALERY_INTENT_V);
                    }
                });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nombre.getText().toString().isEmpty()) {

                        Sectores sector = new Sectores();
                        sector.setNombre(nombre.getText().toString());
                        sector.setColor(color);
                        sector.setEstado(1);
                        sector.setLimite(Integer.parseInt(limite.getText().toString()));

                        if (!fondoh.toString().isEmpty() && !fondov.toString().isEmpty()){
                            sector.setFondoV(fondov.toString());
                            sector.setFondoH(fondoh.toString());
                        }

                        registrarSector(sector);
                        limpiar();

                    cargarLista();

                }else{
                    Toast.makeText(getApplicationContext(), "Faltan Datos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        adapter = new AdapterSectores();
        adapter.setOnNoteSelectedListener(new AdapterSectores.OnNoteSelectedListener() {
            @Override
            public void onClick(Sectores dectordetalle) {

                final Sectores sector = dectordetalle;
                nombre.setText(sector.getNombre());
                limite.setText(""+sector.getLimite());
                color = (sector.getColor());
                asignarColor(color);
                btnregistrar.setText("MODIFICAR");

            }

        });


        adapter.setOnDetailListener(new AdapterSectores.OnNoteDetailListener() {
            @Override
            public void onDetail(Sectores note) {

                //AL TOCAR LA CASILLA
                //CAMBIAR LA VARIABLE HABILITADO

            }
        });



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSectores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        cargarLista();



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                try {

                //eliminar desde firebase

                } catch (Exception e) {

                }
            }
        }).attachToRecyclerView(recyclerView);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();



        if (requestCode == GALERY_INTENT_V && resultCode == RESULT_OK){


            StorageReference filePath = mstorage.child(NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(BASEDATOSSECTORESTEMP).child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> descargarFoto = taskSnapshot.getStorage().getDownloadUrl();
                    descargarFoto.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            fondov = uri;

                        }
                    });



                }

            });


        }else  if (requestCode == GALERY_INTENT_H && resultCode == RESULT_OK) {


            StorageReference filePath = mstorage.child(NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(BASEDATOSSECTORESTEMP).child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> descargarFoto = taskSnapshot.getStorage().getDownloadUrl();
                    descargarFoto.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            fondoh = uri;

                        }
                    });



                }

            });



        }
    }


    private void CloseTeclado() {

        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public void registrarSector(Sectores sector) {


        databaseReference.child(variables.NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(BASEDATOSSECTORESTEMP).child(sector.getNombre()).setValue(sector);

    }


    private void botones() {
        final String red = "#B30D0D";
        final String blue = "#2196F3";
        final String orange = "#FF9800";
        final String green = "#4CAF50";
        final String purple = "#673AB7";
        final String blueOscuro ="#1741AC";

        btnred= findViewById(R.id.btnred);
        btnblue= findViewById(R.id.btnblue);
        btnorange= findViewById(R.id.btnorange);
        btngreen= findViewById(R.id.btngreen);
        btnpurple= findViewById(R.id.btnpurple);

        btnblueOscuro= findViewById(R.id.btnblueOscuro);

        btnred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtcolorseleccionado.setText("Rojo");
                asignarColor(red);
            }
        });
        btnblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtcolorseleccionado.setText("Azul Claro");
                asignarColor(blue);
            }
        });
        btnorange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtcolorseleccionado.setText("Naranja");
                asignarColor(orange);
            }
        });
        btngreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtcolorseleccionado.setText("Verde");
                asignarColor(green);
            }
        });
        btnpurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtcolorseleccionado.setText("Morado");
                asignarColor(purple);
            }
        });
        btnblueOscuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtcolorseleccionado.setText("Azul Oscuro");
                asignarColor(blueOscuro);
            }
        });
    }


    private void asignarColor(String colorelegido) {
        color = colorelegido;
        layoutPrincipal.setBackgroundColor(Color.parseColor(colorelegido));

    }


    private void cargarLista() {

        setProgressDialog();

        databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(CLIENTE).child(BASEDATOSSECTORESTEMP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listnombresectores.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    Sectores sectores = objSnaptshot.getValue(Sectores.class);
                    listnombresectores.add(sectores);
                    Log.i("---> Lista Sectores: ", sectores.getNombre() + " "+ sectores.getColor());

                }
                Adialog.dismiss();
                actualizarReciclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               // Toast.makeText(AsignarSectoress.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();
            }

        });

    }


    public void actualizarReciclerView() {
        adapter.setNotes(listnombresectores);
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

                cargarLista();

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
        databaseReference = firebaseDatabase.getReference();
    }


    private void limpiar() {

        nombre.setText("");
        color = "#B30D0D";

        //boton mejor
        layoutPrincipal.setBackgroundColor(Color.parseColor(color));

        btnregistrar.setText("AÑADIR");
        CloseTeclado();

    }

    }
