package com.example.smarboyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        TextView tvAppInfo = findViewById(R.id.tvAppInfo);
        Button btnBack = findViewById(R.id.btnBack);

        // Información de la app
        tvAppVersion.setText("Versión 1.0.0");
        tvAppInfo.setText(
            "SmartBoy App 🎯\n\n" +
            "Tu asistente inteligente para decisiones de alimentación y fitness.\n\n" +
            "✨ Características:\n" +
            "• Recomendaciones personalizadas con IA\n" +
            "• Búsqueda en tiempo real\n" +
            "• Sistema de favoritos\n" +
            "• Filtros avanzados\n" +
            "• Estadísticas y metas\n" +
            "• Modo oscuro\n" +
            "• Múltiples idiomas\n\n" +
            "📧 Contacto: support@smartboyapp.com\n" +
            "🌐 Web: www.smartboyapp.com\n\n" +
            "Desarrollado con ❤️ en Guatemala 🇬🇹\n\n" +
            "© 2025 SmartBoy App. Todos los derechos reservados."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}

