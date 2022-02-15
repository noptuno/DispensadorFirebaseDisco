package com.example.dispensadorfirebase.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.dispensadorfirebase.inicio.InicioOpcionDispositivo;

public class bootapp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, InicioOpcionDispositivo.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}