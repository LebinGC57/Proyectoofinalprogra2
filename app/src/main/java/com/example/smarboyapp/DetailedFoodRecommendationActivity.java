package com.example.smarboyapp;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.database.FavoritesManager;
import com.example.smarboyapp.database.RecommendationHistoryManager;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.ShareUtils;
import com.example.smarboyapp.utils.SmartRecommendationEngine;
import com.example.smarboyapp.utils.N8nSyncManager;

public class DetailedFoodRecommendationActivity extends AppCompatActivity {

    private TextView tvFoodIcon, tvFoodName, tvFoodPrice, tvFoodRating, tvFoodDistance, tvDescription, tvRatingValue;
    private RatingBar ratingBar;
    private Button btnSave, btnIgnore, btnBack, btnShare;
    private FavoritesManager favoritesManager;
    private UserPreferencesManager preferencesManager;
    private RecommendationHistoryManager historyManager;
    private N8nSyncManager syncManager;
    private FoodItem currentFoodItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_food_recommendation);

        // Aplicar animación de entrada
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(android.R.id.content).startAnimation(fadeIn);

        // Initialize managers
        favoritesManager = new FavoritesManager(this);
        preferencesManager = new UserPreferencesManager(this);
        historyManager = new RecommendationHistoryManager(this);
        syncManager = new N8nSyncManager(this);

        // Initialize views
        tvFoodIcon = findViewById(R.id.tvFoodIcon);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvFoodPrice = findViewById(R.id.tvFoodPrice);
        tvFoodRating = findViewById(R.id.tvFoodRating);
        tvFoodDistance = findViewById(R.id.tvFoodDistance);
        tvDescription = findViewById(R.id.tvDescription);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        ratingBar = findViewById(R.id.ratingBar);
        btnSave = findViewById(R.id.btnSave);
        btnIgnore = findViewById(R.id.btnIgnore);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);

        // Get data from intent
        String foodIcon = getIntent().getStringExtra("food_icon");
        String foodName = getIntent().getStringExtra("food_name");
        String foodPrice = getIntent().getStringExtra("food_price");
        double foodRating = getIntent().getDoubleExtra("food_rating", 0.0);
        String foodDistance = getIntent().getStringExtra("food_distance");
        String foodDescription = getIntent().getStringExtra("food_description");

        // Create current food item
        currentFoodItem = new FoodItem(foodIcon, foodName, foodPrice, foodRating, foodDistance, "food", foodDescription);

        // Set data to views
        tvFoodIcon.setText(foodIcon);
        tvFoodName.setText(foodName);
        tvFoodPrice.setText(foodPrice);
        tvFoodRating.setText("★★★★★ (" + foodRating + ")");
        tvFoodDistance.setText("📍 " + foodDistance);
        tvDescription.setText(foodDescription);

        // Configurar RatingBar
        setupRatingBar();

        // Set click listeners
        btnSave.setOnClickListener(v -> saveRecommendation());
        btnIgnore.setOnClickListener(v -> ignoreRecommendation());
        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareRecommendation());
    }

    private void setupRatingBar() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            tvRatingValue.setText(String.format("%.1f / 5.0", rating));

            if (fromUser) {
                // Guardar calificación del usuario
                currentFoodItem.setUserRating(rating);

                // Agregar al historial
                historyManager.addToHistory(
                    currentFoodItem.getName(),
                    currentFoodItem.getType(),
                    rating,
                    System.currentTimeMillis()
                );

                // Mostrar feedback
                String feedback = getFeedbackMessage(rating);
                Toast.makeText(this, feedback, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFeedbackMessage(float rating) {
        if (rating >= 4.5f) {
            return "¡Excelente! 🌟 Buscaremos más como esta";
        } else if (rating >= 3.5f) {
            return "¡Muy bien! 👍 Tomaremos nota";
        } else if (rating >= 2.5f) {
            return "Entendido 📝 Mejoraremos las sugerencias";
        } else if (rating >= 1.0f) {
            return "Gracias por tu feedback 💭";
        } else {
            return "Califica esta recomendación ⭐";
        }
    }

    private void shareRecommendation() {
        if (currentFoodItem != null) {
            ShareUtils.shareFood(
                this,
                currentFoodItem.getName(),
                currentFoodItem.getPrice(),
                currentFoodItem.getRating(),
                currentFoodItem.getDescription()
            );
        }
    }

    private void saveRecommendation() {
        if (currentFoodItem != null) {
            favoritesManager.addToFavorites(currentFoodItem);

            // Sincronizar con n8n webhook
            syncManager.syncFavoriteAdded(
                currentFoodItem.getName(),
                currentFoodItem.getType(),
                currentFoodItem.getPrice()
            );

            Toast.makeText(this, "¡Guardado en favoritos! 🔄 Sincronizado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void ignoreRecommendation() {
        if (currentFoodItem != null) {
            // Learn from user rejection
            SmartRecommendationEngine.learnFromUserChoice(
                    preferencesManager,
                    currentFoodItem.getType(),
                    false
            );

            // Si no tiene calificación, asignar una baja
            if (currentFoodItem.getUserRating() == 0) {
                currentFoodItem.setUserRating(2.0f);
                historyManager.addToHistory(
                    currentFoodItem.getName(),
                    currentFoodItem.getType(),
                    2.0f,
                    System.currentTimeMillis()
                );
            }

            Toast.makeText(this, "Recomendación ignorada ❌", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
