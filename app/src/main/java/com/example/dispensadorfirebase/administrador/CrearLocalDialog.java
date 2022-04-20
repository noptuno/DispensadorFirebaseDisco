package com.example.dispensadorfirebase.administrador;

import static com.example.dispensadorfirebase.app.variables.BASEDATOSLOCALES;
import static com.example.dispensadorfirebase.app.variables.NOMBREBASEDEDATOSFIREBASE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispensadorfirebase.R;
import com.example.dispensadorfirebase.aplicaciones.DispensadorTurno;
import com.example.dispensadorfirebase.clase.Datos;
import com.example.dispensadorfirebase.clase.Local;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.dispensadorfirebase.app.variables;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class CrearLocalDialog extends AppCompatActivity {


    private  StorageReference mstorage;


    //referencia firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    //varaibles layout
    EditText NombreLocal,NumeroLocal;
    TextView EstadoLocal;
    Button Guardar,Cancelar,subir;

    Uri descargarFoto= Uri.parse("");
    //variables lcoales

    Local local;

    private static final int GALERY_INTENT = 1;

    private Uri linkUriLogo = Uri.parse("");
    private Uri contentlogo = Uri.parse("");
private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_local_dialog);

      NombreLocal = findViewById(R.id.txtNombreLocal);
      NumeroLocal= findViewById(R.id.txtNumeroLocal);
      EstadoLocal= findViewById(R.id.txtEstadoLocal);
      Guardar= findViewById(R.id.btnGuardar);
      Cancelar= findViewById(R.id.btnCancelar);
        img = findViewById(R.id.imgview);
        subir = findViewById(R.id.btnSubir);

      inicializarFirebase();

        mstorage = FirebaseStorage.getInstance().getReference();

        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALERY_INTENT);
            }
        });





        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //validar limite de locales

                //validar llenar datos


                String nom = NombreLocal.getText().toString();
                String val1= NumeroLocal.getText().toString();
                int num=Integer.parseInt(val1);
                String est = "true";
                String logo = "null";
                Local local=new Local(nom,num,est,logo);
                RegistroFirebase(local);


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERY_INTENT && resultCode == RESULT_OK){

            linkUriLogo = data.getData();



        }

    }

    private void mostrarimagen(Uri linkUriLogo) {



        String link = linkUriLogo.toString();
        Uri linkuri = Uri.parse(link);


        File f = new File(getRealPathFromURI(linkuri));
        Drawable d = Drawable.createFromPath(f.getAbsolutePath());
       // img.setImageURI(linkUriLogo);
        img.setImageDrawable(d);





    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }



    private void RegistroFirebase(Local local) {


            StorageReference filePath = mstorage.child(NOMBREBASEDEDATOSFIREBASE).child(BASEDATOSLOCALES).child(local.getNombreLocal()).child(linkUriLogo.getLastPathSegment());
            filePath.putFile(linkUriLogo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> descargarFoto = taskSnapshot.getStorage().getDownloadUrl();
                    descargarFoto.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            img.setImageURI(uri);

                            //mostrarimagen(uri);
                          //  local.setLogo(uri.toString());

                        }
                    });


                   // databaseReference.child(NOMBREBASEDEDATOSFIREBASE).child(BASEDATOSLOCALES).child(local.getNombreLocal()).setValue(local);
                   // finish();
                }

            });


    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }



}