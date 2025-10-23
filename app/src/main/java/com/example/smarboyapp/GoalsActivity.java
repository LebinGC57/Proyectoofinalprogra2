package com.example.smarboyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.smarboyapp.database.RecommendationHistoryManager;
import com.example.smarboyapp.database.UserPreferencesManager;

public class GoalsActivity extends AppCompatActivity {

    private TextView tvDailyGoal, tvWeeklyGoal, tvMonthlyGoal;
    private TextView tvDaysActive, tvTotalRecommendations, tvAchievements;
    private ProgressBar progressDaily, progressWeekly, progressMonthly;
    private Button btnBack;
    private UserPreferencesManager preferencesManager;
    private RecommendationHistoryManager historyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        preferencesManager = new UserPreferencesManager(this);
        historyManager = new RecommendationHistoryManager(this);

        initializeViews();
        loadGoals();
        calculateAchievements();

        btnBack.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        tvDailyGoal = findViewById(R.id.tvDailyGoal);
        tvWeeklyGoal = findViewById(R.id.tvWeeklyGoal);
        tvMonthlyGoal = findViewById(R.id.tvMonthlyGoal);
        tvDaysActive = findViewById(R.id.tvDaysActive);
        tvTotalRecommendations = findViewById(R.id.tvTotalRecommendations);
        tvAchievements = findViewById(R.id.tvAchievements);
        progressDaily = findViewById(R.id.progressDaily);
        progressWeekly = findViewById(R.id.progressWeekly);
        progressMonthly = findViewById(R.id.progressMonthly);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadGoals() {
        int totalRecommendations = historyManager.getHistoryCount();

        // Meta diaria: 3 recomendaciones
        int dailyProgress = Math.min(totalRecommendations % 3, 3);
        progressDaily.setMax(3);
        progressDaily.setProgress(dailyProgress);
        tvDailyGoal.setText(dailyProgress + " / 3 completadas");

        // Meta semanal: 15 recomendaciones
        int weeklyProgress = Math.min(totalRecommendations % 15, 15);
        progressWeekly.setMax(15);
        progressWeekly.setProgress(weeklyProgress);
        tvWeeklyGoal.setText(weeklyProgress + " / 15 completadas");

        // Meta mensual: 50 recomendaciones
        int monthlyProgress = Math.min(totalRecommendations, 50);
        progressMonthly.setMax(50);
        progressMonthly.setProgress(monthlyProgress);
        tvMonthlyGoal.setText(monthlyProgress + " / 50 completadas");
    }

    private void calculateAchievements() {
        int totalRecommendations = historyManager.getHistoryCount();
        float avgRating = historyManager.getAverageRating("food");

        tvTotalRecommendations.setText(String.valueOf(totalRecommendations));

        // Calcular días activos (simulado)
        int daysActive = Math.min(totalRecommendations / 2, 30);
        tvDaysActive.setText(daysActive + " días");

        // Mostrar logros
        StringBuilder achievements = new StringBuilder();

        if (totalRecommendations >= 1) {
            achievements.append("🎉 Primer Explorador\n");
        }
        if (totalRecommendations >= 10) {
            achievements.append("⭐ Usuario Activo\n");
        }
        if (totalRecommendations >= 25) {
            achievements.append("🏆 Experto en Recomendaciones\n");
        }
        if (totalRecommendations >= 50) {
            achievements.append("👑 Maestro SmartBoy\n");
        }
        if (avgRating >= 4.0f) {
            achievements.append("💚 Crítico Exigente\n");
        }

        if (achievements.length() == 0) {
            achievements.append("¡Empieza a explorar para desbloquear logros!");
        }

        tvAchievements.setText(achievements.toString().trim());
    }
}

