package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.smarboyapp.api.N8nWebhookService;
import com.example.smarboyapp.database.UserPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class PreferencesActivity extends AppCompatActivity {

    private ChipGroup chipGroupFood, chipGroupGym, chipGroupBudget;
    private Button btnContinue, btnAiAssistant, btnBack, btnGoogleSignIn;
    private Handler autoRecommendHandler = new Handler();
    private Runnable autoRecommendRunnable;
    private UserPreferencesManager userPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        userPreferencesManager = new UserPreferencesManager(this);

        initializeViews();
        setupListeners();
        setupDefaultSelections();
        setupAutoRecommendation();
    }

    private void initializeViews() {
        chipGroupFood = findViewById(R.id.chipGroupFood);
        chipGroupGym = findViewById(R.id.chipGroupGym);
        chipGroupBudget = findViewById(R.id.chipGroupBudget);
        btnContinue = findViewById(R.id.btnContinue);
        btnAiAssistant = findViewById(R.id.btnAiAssistant);
        btnBack = findViewById(R.id.btnBack);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
    }

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> handleContinue());
        btnAiAssistant.setOnClickListener(v -> openAiAssistant());
        btnBack.setOnClickListener(v -> handleBack());
        btnGoogleSignIn.setOnClickListener(v -> openGoogleAuth());
    }

    private void setupAutoRecommendation() {
        // Configurar listeners para detectar cambios en las selecciones
        ChipGroup.OnCheckedStateChangeListener autoRecommendListener = (group, checkedIds) -> {
            // Cancelar cualquier recomendación pendiente
            if (autoRecommendRunnable != null) {
                autoRecommendHandler.removeCallbacks(autoRecommendRunnable);
            }

            // Si hay al menos una preferencia seleccionada, esperar 1 segundo y generar recomendaciones
            if (hasSelectedPreferences()) {
                autoRecommendRunnable = () -> generateAutoRecommendations();
                autoRecommendHandler.postDelayed(autoRecommendRunnable, 1000); // Esperar 1 segundo después de la última selección
            }
        };

        chipGroupFood.setOnCheckedStateChangeListener(autoRecommendListener);
        chipGroupGym.setOnCheckedStateChangeListener(autoRecommendListener);
        chipGroupBudget.setOnCheckedStateChangeListener(autoRecommendListener);
    }

    private void generateAutoRecommendations() {
        // Guardar preferencias
        savePreferences();

        // Mostrar toast con las preferencias seleccionadas
        List<String> preferences = getSelectedPreferences();
        Toast.makeText(this, "🎯 Generando recomendaciones basadas en tus preferencias...",
            Toast.LENGTH_SHORT).show();

        // Determinar si el usuario está más interesado en comida o gimnasio
        boolean hasFoodPreferences = chipGroupFood.getCheckedChipIds().size() > 0;
        boolean hasGymPreferences = chipGroupGym.getCheckedChipIds().size() > 0;

        // Esperar medio segundo y luego navegar
        new Handler().postDelayed(() -> {
            Intent intent;

            if (hasFoodPreferences && !hasGymPreferences) {
                // Solo tiene preferencias de comida
                intent = new Intent(this, FoodRecommendationActivity.class);
            } else if (hasGymPreferences && !hasFoodPreferences) {
                // Solo tiene preferencias de gimnasio
                intent = new Intent(this, GymRecommendationActivity.class);
            } else if (hasFoodPreferences && hasGymPreferences) {
                // Tiene ambas, ir a CategorySelectionActivity para que elija
                intent = new Intent(this, CategorySelectionActivity.class);
            } else {
                // Solo tiene presupuesto, mostrar comida por defecto
                intent = new Intent(this, FoodRecommendationActivity.class);
            }

            // Pasar las preferencias seleccionadas
            intent.putStringArrayListExtra("selected_preferences", (ArrayList<String>) preferences);
            startActivity(intent);
        }, 500);
    }

    private void setupDefaultSelections() {
        // Seleccionar chip de comida saludable por defecto
        Chip chipHealthy = findViewById(R.id.chipHealthy);
        if (chipHealthy != null) {
            chipHealthy.setChecked(true);
        }

        // Seleccionar presupuesto medio por defecto
        Chip chipBudgetMedium = findViewById(R.id.chipBudgetMedium);
        if (chipBudgetMedium != null) {
            chipBudgetMedium.setChecked(true);
        }
    }

    private void handleContinue() {
        // Validar que al menos una preferencia esté seleccionada
        if (!hasSelectedPreferences()) {
            Toast.makeText(this, "Por favor selecciona al menos una preferencia", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cancelar auto-recomendación si está pendiente
        if (autoRecommendRunnable != null) {
            autoRecommendHandler.removeCallbacks(autoRecommendRunnable);
        }

        // Generar recomendaciones inmediatamente
        generateAutoRecommendations();
    }

    private boolean hasSelectedPreferences() {
        return chipGroupFood.getCheckedChipIds().size() > 0 ||
               chipGroupGym.getCheckedChipIds().size() > 0 ||
               chipGroupBudget.getCheckedChipIds().size() > 0;
    }

    private List<String> getSelectedPreferences() {
        List<String> preferences = new ArrayList<>();

        // Obtener preferencias de comida seleccionadas
        for (int id : chipGroupFood.getCheckedChipIds()) {
            Chip chip = findViewById(id);
            if (chip != null) {
                preferences.add("food_" + chip.getText().toString());
            }
        }

        // Obtener preferencias de gym seleccionadas
        for (int id : chipGroupGym.getCheckedChipIds()) {
            Chip chip = findViewById(id);
            if (chip != null) {
                preferences.add("gym_" + chip.getText().toString());
            }
        }

        // Obtener presupuesto seleccionado
        for (int id : chipGroupBudget.getCheckedChipIds()) {
            Chip chip = findViewById(id);
            if (chip != null) {
                preferences.add("budget_" + chip.getText().toString());
            }
        }

        return preferences;
    }

    private void savePreferences() {
        // Guardar las preferencias seleccionadas
        List<String> preferences = getSelectedPreferences();

        // Toast más discreto
        if (preferences.size() > 0) {
            Toast.makeText(this, "✅ " + preferences.size() + " preferencias guardadas",
                Toast.LENGTH_SHORT).show();

            // Enviar preferencias al webhook de n8n
            syncPreferencesToN8n(preferences);
        }
    }

    /**
     * Sincronizar preferencias con n8n webhook
     */
    private void syncPreferencesToN8n(List<String> preferences) {
        String userName = userPreferencesManager.getUserName();
        if (userName == null || userName.isEmpty()) {
            userName = "Usuario Anónimo";
        }

        N8nWebhookService.sendUserPreferences(userName, preferences, new N8nWebhookService.WebhookCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(PreferencesActivity.this,
                        "🔄 Preferencias sincronizadas con éxito",
                        Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // No mostrar error al usuario, solo registrar en logs
                    android.util.Log.e("PreferencesActivity", "Error sync n8n: " + error);
                });
            }
        });
    }

    private void openAiAssistant() {
        Intent intent = new Intent(this, AiAssistantActivity.class);
        startActivity(intent);
    }

    private void handleBack() {
        // Cancelar cualquier auto-recomendación pendiente
        if (autoRecommendRunnable != null) {
            autoRecommendHandler.removeCallbacks(autoRecommendRunnable);
        }
        finish(); // Regresar a la actividad anterior
    }

    private void openGoogleAuth() {
        Intent intent = new Intent(this, GoogleAuthActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar handlers para evitar memory leaks
        if (autoRecommendRunnable != null) {
            autoRecommendHandler.removeCallbacks(autoRecommendRunnable);
        }
    }
}
