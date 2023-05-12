package com.lit.litnotes.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lit.litnotes.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_TIME = 500;
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this,HomeScreen.class));
            finish();
        }, SPLASH_TIME);
    }
}