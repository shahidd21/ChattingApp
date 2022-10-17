package com.example.chattingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIMER = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // creating handler to handle the splash time

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // starting the main screen now

                Intent intent = new Intent(SplashActivity.this, SendOtp.class);
                startActivity(intent);

                finish();         // we doesn't want to come to splash screen again



            }
        }, SPLASH_TIMER);
    }
}