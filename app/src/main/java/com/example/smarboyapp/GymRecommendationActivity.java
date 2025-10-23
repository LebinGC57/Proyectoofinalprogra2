package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.adapter.GymAdapter;
import com.example.smarboyapp.model.GymItem;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.SmartRecommendationEngine;

import java.util.ArrayList;
import java.util.List;

public class GymRecommendationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GymAdapter adapter;
    private List<GymItem> gymList;
    private ProgressBar progressBar;
    private TextView tvLoadingMessage;
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_recommendation);

        recyclerView = findViewById(R.id.recyclerViewGym);
        progressBar = findViewById(R.id.progressBar);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);

        preferencesManager = new UserPreferencesManager(this);

        setupRecyclerView();
        setupButtons(btnBack, btnNext);
        loadGymRecommendations();
    }

    private void setupRecyclerView() {
        gymList = new ArrayList<>();
        adapter = new GymAdapter(gymList, this::onGymItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupButtons(Button btnBack, Button btnNext) {
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (!gymList.isEmpty()) {
                onGymItemClick(gymList.get(0));
            } else {
                Toast.makeText(this, "Cargando recomendaciones...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGymRecommendations() {
        showLoading(true);

        // Crear perfil del usuario
        SmartRecommendationEngine.UserProfile userProfile = new SmartRecommendationEngine.UserProfile();
        userProfile.name = preferencesManager.getUserName();
        userProfile.age = preferencesManager.getUserAge();
        userProfile.weight = preferencesManager.getUserWeight();
        userProfile.height = preferencesManager.getUserHeight();
        userProfile.budget = preferencesManager.getUserBudget();

        String userName = userProfile.name.isEmpty() ? "Usuario" : userProfile.name;
        tvLoadingMessage.setText("Generando recomendaciones de gimnasio para " + userName + "...");

        // Simular carga de 2 segundos
        new Handler().postDelayed(() -> {
            List<GymItem> recommendations = getRecommendations(userProfile);

            showLoading(false);
            gymList.clear();
            gymList.addAll(recommendations);
            adapter.notifyDataSetChanged();

            showPersonalizedTip(userProfile);
        }, 2000);
    }

    private List<GymItem> getRecommendations(SmartRecommendationEngine.UserProfile userProfile) {
        List<GymItem> recommendations = new ArrayList<>();

        String bmiCategory = userProfile.getBMICategory();

        // Recomendaciones basadas en BMI
        switch (bmiCategory) {
            case "underweight":
                recommendations.add(new GymItem(
                        "💪", "ENTRENAMIENTO DE FUERZA", "Q 150", 4.8, "A 5 minutos caminando", "class",
                        "Sesiones de pesas para ganar masa muscular"));
                recommendations.add(new GymItem(
                        "🥤", "BATIDOS PROTEICOS", "Q 45", 4.3, "A 3 minutos caminando", "supplement",
                        "Suplementos para ganar peso saludable"));
                break;
            case "overweight":
            case "obese":
                recommendations.add(new GymItem(
                        "🏃", "CARDIO INTENSIVO", "Q 120", 4.7, "A 6 minutos caminando", "class",
                        "Clases de cardio para quemar grasa"));
                recommendations.add(new GymItem(
                        "🚴", "SPINNING", "Q 100", 4.5, "A 7 minutos caminando", "class",
                        "Ciclismo indoor para perder peso"));
                break;
            default:
                recommendations.add(new GymItem(
                        "🏋️", "GIMNASIO COMPLETO", "Q 180", 4.6, "A 6 minutos caminando", "membership",
                        "Gimnasio con todas las facilidades"));
                recommendations.add(new GymItem(
                        "🤸", "FITNESS FUNCIONAL", "Q 140", 4.5, "A 8 minutos caminando", "class",
                        "Entrenamiento funcional"));
                break;
        }

        // Agregar recomendación por edad
        String ageGroup = userProfile.getAgeGroup();
        if ("senior".equals(ageGroup)) {
            recommendations.add(new GymItem(
                    "🚶", "CAMINATA GRUPAL", "Q 40", 4.3, "A 3 minutos caminando", "class",
                    "Ejercicio suave para adultos mayores"));
        } else if ("young".equals(ageGroup)) {
            recommendations.add(new GymItem(
                    "⚡", "HIIT JOVEN", "Q 130", 4.7, "A 5 minutos caminando", "class",
                    "Entrenamiento de alta intensidad"));
        } else {
            recommendations.add(new GymItem(
                    "🧘", "PILATES", "Q 90", 4.6, "A 7 minutos caminando", "class",
                    "Ejercicio de bajo impacto"));
        }

        return recommendations;
    }

    private void showPersonalizedTip(SmartRecommendationEngine.UserProfile userProfile) {
        String bmiCategory = userProfile.getBMICategory();
        String tip = "¡Hola " + userProfile.name + "! ";

        switch (bmiCategory) {
            case "underweight":
                tip += "Te recomendamos entrenamientos de fuerza para ganar masa muscular.";
                break;
            case "overweight":
            case "obese":
                tip += "El ejercicio cardiovascular te ayudará con tus objetivos de peso.";
                break;
            default:
                tip += "Mantén tu condición física con ejercicios balanceados.";
                break;
        }

        Toast.makeText(this, tip, Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            tvLoadingMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvLoadingMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void onGymItemClick(GymItem gymItem) {
        Intent intent = new Intent(this, DetailedGymRecommendationActivity.class);
        intent.putExtra("gym_icon", gymItem.getIcon());
        intent.putExtra("gym_name", gymItem.getName());
        intent.putExtra("gym_price", gymItem.getPrice());
        intent.putExtra("gym_rating", gymItem.getRating());
        intent.putExtra("gym_distance", gymItem.getDistance());
        intent.putExtra("gym_type", gymItem.getType());
        intent.putExtra("gym_description", gymItem.getDescription());
        startActivity(intent);
    }
}
