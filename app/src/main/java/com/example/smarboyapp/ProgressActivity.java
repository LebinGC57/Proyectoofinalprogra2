package com.example.smarboyapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.database.RecommendationHistoryManager;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.ShareUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressActivity extends AppCompatActivity {

    private TextView tvTotalRecommendations, tvAverageRating, tvFavoriteCategory, tvBestRated, tvRecentActivity;
    private Button btnShareProgress, btnClearHistory, btnBack;
    private RecommendationHistoryManager historyManager;
    private UserPreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Initialize managers
        historyManager = new RecommendationHistoryManager(this);
        preferencesManager = new UserPreferencesManager(this);

        // Initialize views
        tvTotalRecommendations = findViewById(R.id.tvTotalRecommendations);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvFavoriteCategory = findViewById(R.id.tvFavoriteCategory);
        tvBestRated = findViewById(R.id.tvBestRated);
        tvRecentActivity = findViewById(R.id.tvRecentActivity);
        btnShareProgress = findViewById(R.id.btnShareProgress);
        btnClearHistory = findViewById(R.id.btnClearHistory);
        btnBack = findViewById(R.id.btnBack);

        // Load statistics
        loadStatistics();

        // Set button listeners
        btnShareProgress.setOnClickListener(v -> shareProgress());
        btnClearHistory.setOnClickListener(v -> clearHistory());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadStatistics() {
        List<RecommendationHistoryManager.HistoryItem> history = historyManager.getHistory();

        if (history.isEmpty()) {
            showEmptyState();
            return;
        }

        // Total de recomendaciones
        int total = history.size();
        tvTotalRecommendations.setText(String.valueOf(total));

        // Calificación promedio
        float totalRating = 0;
        for (RecommendationHistoryManager.HistoryItem item : history) {
            totalRating += item.rating;
        }
        float avgRating = totalRating / total;
        tvAverageRating.setText(String.format("%.1f / 5.0", avgRating));

        // Categoría favorita
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Float> categoryRatings = new HashMap<>();

        for (RecommendationHistoryManager.HistoryItem item : history) {
            String category = item.type;
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            categoryRatings.put(category, categoryRatings.getOrDefault(category, 0f) + item.rating);
        }

        String favoriteCategory = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                favoriteCategory = entry.getKey();
            }
        }

        tvFavoriteCategory.setText(getCategoryDisplayName(favoriteCategory) + " (" + maxCount + " veces)");

        // Mejor calificado
        RecommendationHistoryManager.HistoryItem bestRated = history.get(0);
        for (RecommendationHistoryManager.HistoryItem item : history) {
            if (item.rating > bestRated.rating) {
                bestRated = item;
            }
        }
        tvBestRated.setText(bestRated.name + " ⭐ " + bestRated.rating);

        // Actividad reciente
        StringBuilder recentActivity = new StringBuilder();
        int recentCount = Math.min(5, history.size());
        for (int i = 0; i < recentCount; i++) {
            RecommendationHistoryManager.HistoryItem item = history.get(i);
            recentActivity.append("• ").append(item.name)
                    .append(" - ").append(getStars(item.rating))
                    .append("\n");
        }
        tvRecentActivity.setText(recentActivity.toString().trim());
    }

    private void showEmptyState() {
        tvTotalRecommendations.setText("0");
        tvAverageRating.setText("0.0 / 5.0");
        tvFavoriteCategory.setText("Sin datos");
        tvBestRated.setText("Ninguna todavía");
        tvRecentActivity.setText("¡Comienza a explorar recomendaciones para ver tus estadísticas! 🚀");
    }

    private String getCategoryDisplayName(String category) {
        if (category.startsWith("gym_")) {
            category = category.substring(4);
            switch (category) {
                case "membership": return "Gimnasio (Membresía)";
                case "class": return "Gimnasio (Clases)";
                case "supplement": return "Gimnasio (Suplementos)";
                default: return "Gimnasio";
            }
        } else {
            return "Comida";
        }
    }

    private String getStars(float rating) {
        int stars = Math.round(rating);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            sb.append("⭐");
        }
        return sb.toString();
    }

    private void shareProgress() {
        List<RecommendationHistoryManager.HistoryItem> history = historyManager.getHistory();

        if (history.isEmpty()) {
            Toast.makeText(this, "No hay datos para compartir aún", Toast.LENGTH_SHORT).show();
            return;
        }

        float totalRating = 0;
        for (RecommendationHistoryManager.HistoryItem item : history) {
            totalRating += item.rating;
        }
        float avgRating = totalRating / history.size();

        // Encontrar categoría favorita
        Map<String, Integer> categoryCount = new HashMap<>();
        for (RecommendationHistoryManager.HistoryItem item : history) {
            categoryCount.put(item.type, categoryCount.getOrDefault(item.type, 0) + 1);
        }

        String favoriteCategory = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                favoriteCategory = entry.getKey();
            }
        }

        ShareUtils.shareProgress(this, history.size(), avgRating, getCategoryDisplayName(favoriteCategory));
    }

    private void clearHistory() {
        historyManager.clearHistory();
        Toast.makeText(this, "Historial eliminado 🗑️", Toast.LENGTH_SHORT).show();
        loadStatistics();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

