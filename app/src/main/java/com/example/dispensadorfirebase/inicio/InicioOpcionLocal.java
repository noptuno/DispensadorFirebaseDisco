package com.example.dispensadorfirebase.inicio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.dispensadorfirebase.adapter.AdapterLocal;
import com.example.dispensadorfirebase.administrador.AsignarSectoress;
import com.example.dispensadorfirebase.administrador.CrearLocalDialog;
import com.example.dispensadorfirebase.administrador.ListaLocales;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.aplicaciones.DisplayGrande;
import com.example.dispensadorfirebase.aplicaciones.DisplayPequeño;
import com.example.dispensadorfirebase.aplicaciones.TabletDispensador;
import com.example.dispensadorfirebase.aplicaciones.supervisor.Supervisor_Flash;
import com.example.dispensadorfirebase.app.variables;
import com.example.dispensadorfirebase.clase.Local;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InicioOpcionLocal extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ArrayList<Local> list;

    AdapterLocal adapter;
    AlertDialog Adialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferencelocales;
    ActionBar actionBar;
    String NOMBREDELDISPOSITIVO=null;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_opcion_local);


        inicializarFirebase();
        //ocultarbarra();
        list = new ArrayList<>();
        adapter = new AdapterLocal();


        NOMBREDELDISPOSITIVO = getIntent().getStringExtra("DISPOSITIVO");
        //Acciones layout


        // funcionaldiades

        adapter.setOnNoteSelectedListener(new AdapterLocal.OnNoteSelectedListener() {
            @Override
            public void onClick(Local note) {


                Intent intent = new Intent(InicioOpcionLocal.this, InicioOpcionSectores.class);
                intent.putExtra("LOCAL", note.getNombreLocal());
                intent.putExtra("DISPOSITIVO", NOMBREDELDISPOSITIVO);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                //  idtablaserie.setText(note.getId());
                // numeroserie.setText(note.getNserie());
            }

        });


        abriraplicacion();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerElegirLocal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        cargarLista();

    }



    private void abriraplicacion() {

        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        String estado = pref.getString("ESTADO", "NO");
        String configuracion = pref.getString("CONFIGURACIONDMR", "NO");

        if (configuracion.equals("SI")){


            NOMBREDELDISPOSITIVO = pref.getString("DISPOSITIVO", "NO");

            if (NOMBREDELDISPOSITIVO!="NO"){


                if (estado.equals("SI")) {

                    Intent intent = null;

                    if (NOMBREDELDISPOSITIVO.equals("DISPLAY 21PLG")) {
                        intent = new Intent(InicioOpcionLocal.this, DisplayGrande.class);

                    } else if (NOMBREDELDISPOSITIVO.equals("DISPLAY 15PLG")) {
                        intent = new Intent(InicioOpcionLocal.this, DisplayPequeño.class);

                    } else if (NOMBREDELDISPOSITIVO.equals("TABLET 10PLG")) {

                        intent = new Intent(InicioOpcionLocal.this, TabletDispensador.class);
                    } else if (NOMBREDELDISPOSITIVO.equals("DISPENSADOR")) {
                        intent = new Intent(InicioOpcionLocal.this, DispensadorTurno.class);

                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    InicioOpcionLocal.this.finish();

                }

            }




        }



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(this);

                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        actualizarReciclerView();
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });

                break;

            case R.id.volver:


        botonregresar();


                break;
        }
    return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {


        botonregresar();
        // super.onBackPressed();


    }


    private void botonregresar() {


        if(NOMBREDELDISPOSITIVO.equals("SUPERVISOR")){
            finish();
        }else{
            // load the dialog_promt_user.xml layout and inflate to view
            LayoutInflater layoutinflater = LayoutInflater.from(getApplicationContext());
            View promptUserView = layoutinflater.inflate(R.layout.dialog_activity_pass, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InicioOpcionLocal.this);

            alertDialogBuilder.setView(promptUserView);

            final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.username);

            alertDialogBuilder.setTitle("Usuario Administrador: ");

            // prompt for username
            alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // and display the username on main activity layout


                    if (!userAnswer.equals("") && userAnswer.getText().length()>0){

                        if (validaryguardar(userAnswer.getText().toString())){
                            guardarSharePreferencePrincipal();
                            Intent intent = new Intent(InicioOpcionLocal.this, InicioOpcionDispositivo.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            InicioOpcionLocal.this.finish();
                        }else{
                            Toast.makeText(InicioOpcionLocal.this, "Contraseña Incorrecta", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });

            // all set and time to build and show up!
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            userAnswer.requestFocus();

        }

    }


    private boolean validaryguardar(String pass){
        boolean v = false;
        if (pass.equals("dmr")){
            v = true;
        }

        return v;
    }

    private void guardarSharePreferencePrincipal() {


        pref = getSharedPreferences("CONFIGURAR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CONFIGURACIONDMR", "NO");
        editor.putString("DISPOSITIVO", "NO");
        editor.apply();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buscador, menu);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Local> filteredModelList = filter(list, newText);
        adapter.setNotes(filteredModelList);
        adapter.notifyDataSetChanged();
        return false;
    }

    private List<Local> filter(List<Local> models, String query) {
        query = query.toLowerCase();

        final List<Local> listafiltrada = new ArrayList<>();
        for (Local model : models) {
            final String text = model.getNombreLocal().toLowerCase();
            if (text.contains(query)) {
                listafiltrada.add(model);
            }
        }


        return listafiltrada;
    }

    private void ocultarbarra() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    private void cargarLista() {

        setProgressDialog();

        databaseReferencelocales.child(variables.NOMBREBASEDEDATOSFIREBASE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){

                    Local local = objSnaptshot.getValue(Local.class);
                    list.add(local);
                }

                Adialog.dismiss();
                actualizarReciclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InicioOpcionLocal.this, "Hubo un Problema con la red", Toast.LENGTH_LONG).show();
                Adialog.dismiss();
            }

        });

    }


    public void actualizarReciclerView() {
        adapter.setNotes(list);
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
        databaseReferencelocales = firebaseDatabase.getReference();
    }



}