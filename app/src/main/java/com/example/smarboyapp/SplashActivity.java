package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.smarboyapp.database.UserPreferencesManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 segundos
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferencesManager = new UserPreferencesManager(this);

        // Aplicar modo oscuro si está activado
        if (preferencesManager.isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        TextView tvAppName = findViewById(R.id.tvAppName);
        TextView tvTagline = findViewById(R.id.tvTagline);
        ImageView ivLogo = findViewById(R.id.ivLogo);

        // Animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);

        tvAppName.startAnimation(fadeIn);
        tvTagline.startAnimation(slideUp);
        ivLogo.startAnimation(fadeIn);

        // Navegar después del splash
        new Handler().postDelayed(() -> {
            Intent intent;

            // Verificar si es la primera vez que abre la app
            if (!preferencesManager.isOnboardingCompleted()) {
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            } else if (preferencesManager.isProfileCompleted()) {
                intent = new Intent(SplashActivity.this, CategorySelectionActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }, SPLASH_DURATION);
    }
}
