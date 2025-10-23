package com.example.smarboyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MapsActivity extends AppCompatActivity {

    private TextView tvPlaceName, tvPlaceInfo;
    private Button btnBack;

    private String placeName;
    private double placeLat;
    private double placeLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtener datos del lugar
        placeName = getIntent().getStringExtra("place_name");
        placeLat = getIntent().getDoubleExtra("place_lat", 14.6349);
        placeLng = getIntent().getDoubleExtra("place_lng", -90.5069);

        tvPlaceName = findViewById(R.id.tvPlaceName);
        tvPlaceInfo = findViewById(R.id.tvPlaceInfo);
        btnBack = findViewById(R.id.btnBack);

        // Configurar información
        tvPlaceName.setText(placeName != null ? placeName : "Ubicación");
        tvPlaceInfo.setText(String.format(
            "📍 Coordenadas:\nLatitud: %.4f\nLongitud: %.4f\n\n" +
            "💡 Tip: Esta ubicación está cerca de ti.\n" +
            "Puedes usar Google Maps para obtener direcciones.",
            placeLat, placeLng
        ));

        btnBack.setOnClickListener(v -> finish());
    }
}
