package com.example.dispensadorfirebase.principaltemp.notificador;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.dispensadorfirebase.R;

public class FlashSupervisor extends AppCompatActivity {

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_supervisor);
        actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


                Intent mainIntent = new Intent(FlashSupervisor.this, Supervisor.class);
                FlashSupervisor.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                FlashSupervisor.this.finish();

            }


        }, 2000);



    }
}