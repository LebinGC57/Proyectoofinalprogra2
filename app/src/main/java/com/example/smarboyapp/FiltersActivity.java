package com.example.smarboyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.database.UserPreferencesManager;

public class FiltersActivity extends AppCompatActivity {

    private EditText etMinPrice, etMaxPrice;
    private SeekBar seekBarDistance;
    private TextView tvDistanceValue;
    private RatingBar ratingBarFilter;
    private Button btnApplyFilters, btnClearFilters, btnBack;
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        preferencesManager = new UserPreferencesManager(this);

        initializeViews();
        loadSavedFilters();
        setupListeners();
    }

    private void initializeViews() {
        etMinPrice = findViewById(R.id.etMinPrice);
        etMaxPrice = findViewById(R.id.etMaxPrice);
        seekBarDistance = findViewById(R.id.seekBarDistance);
        tvDistanceValue = findViewById(R.id.tvDistanceValue);
        ratingBarFilter = findViewById(R.id.ratingBarFilter);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadSavedFilters() {
        // Cargar filtros guardados
        String minPrice = preferencesManager.getFilter("minPrice");
        String maxPrice = preferencesManager.getFilter("maxPrice");
        String distance = preferencesManager.getFilter("distance");
        String rating = preferencesManager.getFilter("rating");

        if (minPrice != null && !minPrice.isEmpty()) {
            etMinPrice.setText(minPrice);
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            etMaxPrice.setText(maxPrice);
        }
        if (distance != null && !distance.isEmpty()) {
            try {
                int distanceValue = Integer.parseInt(distance);
                seekBarDistance.setProgress(distanceValue);
                tvDistanceValue.setText(distanceValue + " minutos");
            } catch (NumberFormatException e) {
                seekBarDistance.setProgress(10);
                tvDistanceValue.setText("10 minutos");
            }
        }
        if (rating != null && !rating.isEmpty()) {
            try {
                float ratingValue = Float.parseFloat(rating);
                ratingBarFilter.setRating(ratingValue);
            } catch (NumberFormatException e) {
                ratingBarFilter.setRating(0);
            }
        }
    }

    private void setupListeners() {
        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDistanceValue.setText(progress + " minutos");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());
        btnBack.setOnClickListener(v -> finish());
    }

    private void applyFilters() {
        // Guardar filtros
        String minPrice = etMinPrice.getText().toString();
        String maxPrice = etMaxPrice.getText().toString();
        String distance = String.valueOf(seekBarDistance.getProgress());
        String rating = String.valueOf(ratingBarFilter.getRating());

        preferencesManager.saveFilter("minPrice", minPrice);
        preferencesManager.saveFilter("maxPrice", maxPrice);
        preferencesManager.saveFilter("distance", distance);
        preferencesManager.saveFilter("rating", rating);

        Toast.makeText(this, "Filtros aplicados correctamente", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void clearFilters() {
        etMinPrice.setText("");
        etMaxPrice.setText("");
        seekBarDistance.setProgress(10);
        tvDistanceValue.setText("10 minutos");
        ratingBarFilter.setRating(0);

        preferencesManager.clearFilters();
        Toast.makeText(this, "Filtros limpiados", Toast.LENGTH_SHORT).show();
    }
}
