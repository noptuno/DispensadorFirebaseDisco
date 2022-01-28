package com.example.dispensadorfirebase.aplicaciones;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.dispensadorfirebase.R;


public class CuadrodeDialogo {

    private finalizarCuadro interfaz;


    public interface finalizarCuadro{

        void ResultadoCuadroDialogo(int numero);
    }



    public CuadrodeDialogo(Context contexcto , finalizarCuadro actividad){

        interfaz = actividad;
        int numeros= 0;
        final Dialog dialogo = new Dialog(contexcto);

        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setCancelable(false);
        dialogo.setCanceledOnTouchOutside(false);
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogo.setContentView(R.layout.dialog_num);

        Button aceptar = dialogo.findViewById(R.id.btnAceptar);
        Button cancelar = dialogo.findViewById(R.id.btnCancelar);
        EditText numero = dialogo.findViewById(R.id.et_numero);
        numero.setText("1");

        numero.setSelectAllOnFocus(true);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    interfaz.ResultadoCuadroDialogo(Integer.parseInt(numero.getText().toString()));
                    dialogo.dismiss();
                }catch (Exception e){
                    interfaz.ResultadoCuadroDialogo(1);
                    dialogo.dismiss();
                }


            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogo.dismiss();

            }
        });

        dialogo.show();
    }

}
