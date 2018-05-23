package com.hvantage2.money4driveeee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.util.AppPreference;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Fabric.with(this, new Crashlytics());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreference.isInstalled(SplashActivity.this) == true) {
                    if (AppPreference.isLoggedIn(SplashActivity.this) == true) {
                        startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
