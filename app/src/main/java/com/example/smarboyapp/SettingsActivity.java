package com.example.smarboyapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.NotificationHelper;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode, switchLanguage, switchNotifications;
    private Button btnEditProfile, btnAbout, btnBack;
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesManager = new UserPreferencesManager(this);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchLanguage = findViewById(R.id.switchLanguage);
        switchNotifications = findViewById(R.id.switchNotifications);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnAbout = findViewById(R.id.btnAbout);
        btnBack = findViewById(R.id.btnBack);

        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this);

        // Cargar estado actual del modo oscuro
        boolean isDarkMode = preferencesManager.isDarkModeEnabled();
        switchDarkMode.setChecked(isDarkMode);

        // Cargar idioma actual (false = español, true = inglés)
        boolean isEnglish = preferencesManager.isEnglishEnabled();
        switchLanguage.setChecked(isEnglish);

        // Cargar estado de notificaciones
        boolean notificationsEnabled = preferencesManager.areNotificationsEnabled();
        switchNotifications.setChecked(notificationsEnabled);

        // Listener para el switch de modo oscuro
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setDarkModeEnabled(isChecked);

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, isEnglish ? "🌙 Dark mode enabled" : "🌙 Modo oscuro activado", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, isEnglish ? "☀️ Light mode enabled" : "☀️ Modo claro activado", Toast.LENGTH_SHORT).show();
            }

            recreate();
        });

        // Listener para el switch de idioma
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setEnglishEnabled(isChecked);

            // Cambiar idioma
            String languageCode = isChecked ? "en" : "es";
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());

            Toast.makeText(this, isChecked ? "🌍 Language changed to English" : "🌍 Idioma cambiado a Español", Toast.LENGTH_SHORT).show();

            // Reiniciar la actividad para aplicar cambios
            recreate();
        });

        // Listener para el switch de notificaciones
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setNotificationsEnabled(isChecked);

            if (isChecked) {
                Toast.makeText(this, "🔔 Notificaciones activadas", Toast.LENGTH_SHORT).show();
                // Enviar notificación de prueba
                NotificationHelper.sendMotivationalMessage(this);
            } else {
                Toast.makeText(this, "🔕 Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para editar perfil
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Botón para ver información de la app
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
