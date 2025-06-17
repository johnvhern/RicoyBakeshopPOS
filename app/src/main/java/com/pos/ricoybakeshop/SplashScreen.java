package com.pos.ricoybakeshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1000; // 1 second
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(SplashScreen.this);
            String role = sessionManager.getUserRole();

            if (sessionManager.isLoggedIn()) {
                // User is logged in, go to Dashboard
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
            } else {
                // Not logged in, go to Login
                Intent intent = new Intent(SplashScreen.this, login.class);
                startActivity(intent);
            }

            finish(); // Close SplashActivity so user can't return here

        }, SPLASH_DURATION);
    }
}