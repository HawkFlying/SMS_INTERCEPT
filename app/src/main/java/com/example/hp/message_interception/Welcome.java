package com.example.hp.message_interception;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;


public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                 Intent intent=new Intent(Welcome.this,Sign_in.class);
                 startActivity(intent);
                 finish();
            }
        };
        timer.schedule(task,3000);

    }
}
