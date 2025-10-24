package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.database.UserPreferencesManager;

import java.util.ArrayList;

public class CategorySelectionActivity extends AppCompatActivity {

    private static final String TAG = "CategorySelection";
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        preferencesManager = new UserPreferencesManager(this);

        Log.d(TAG, "CategorySelectionActivity iniciada");

        // Aplicar animación de entrada
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(android.R.id.content).startAnimation(fadeIn);

        Button btnFood = findViewById(R.id.btnFood);
        Button btnGym = findViewById(R.id.btnGym);
        Button btnAI = findViewById(R.id.btnAI);
        Button btnChef = findViewById(R.id.btnChef);
        Button btnStats = findViewById(R.id.btnStats);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnFavorites = findViewById(R.id.btnFavorites);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnGoals = findViewById(R.id.btnGoals);
        Button btnFilters = findViewById(R.id.btnFilters);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);

        btnFood.setOnClickListener(v -> {
            Log.d(TAG, "Botón ALIMENTOS presionado");
            Toast.makeText(this, "Cargando recomendaciones de alimentos...", Toast.LENGTH_SHORT).show();

            // Ir directo a recomendaciones de comida
            Intent intent = new Intent(CategorySelectionActivity.this, FoodRecommendationActivity.class);
            ArrayList<String> defaultPreferences = new ArrayList<>();
            defaultPreferences.add("Comida Saludable");
            intent.putStringArrayListExtra("selected_preferences", defaultPreferences);

            Log.d(TAG, "Iniciando FoodRecommendationActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnGym.setOnClickListener(v -> {
            Log.d(TAG, "Botón GIMNASIO presionado");
            Toast.makeText(this, "Cargando recomendaciones de gimnasio...", Toast.LENGTH_SHORT).show();

            // Ir directo a recomendaciones de gimnasio
            Intent intent = new Intent(CategorySelectionActivity.this, GymRecommendationActivity.class);

            Log.d(TAG, "Iniciando GymRecommendationActivity");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnAI.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, AiAssistantActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnChef.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, ChefConscienteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, ProgressActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, FavoritesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnGoals.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, GoalsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnFilters.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, FiltersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(CategorySelectionActivity.this, PreferencesActivity.class);
            intent.putExtra("category", "general");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
