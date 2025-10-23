package com.example.smarboyapp;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarboyapp.database.RecommendationHistoryManager;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.model.GymItem;
import com.example.smarboyapp.utils.ShareUtils;
import com.example.smarboyapp.utils.SmartRecommendationEngine;

public class DetailedGymRecommendationActivity extends AppCompatActivity {

    private TextView tvGymIcon, tvGymName, tvGymPrice, tvGymRating, tvGymDistance, tvGymType, tvDescription, tvRatingValue;
    private RatingBar ratingBar;
    private Button btnSave, btnIgnore, btnBack, btnShare;
    private UserPreferencesManager preferencesManager;
    private RecommendationHistoryManager historyManager;
    private GymItem currentGymItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_gym_recommendation);

        // Aplicar animación de entrada
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(android.R.id.content).startAnimation(fadeIn);

        // Initialize managers
        preferencesManager = new UserPreferencesManager(this);
        historyManager = new RecommendationHistoryManager(this);

        // Initialize views
        tvGymIcon = findViewById(R.id.tvGymIcon);
        tvGymName = findViewById(R.id.tvGymName);
        tvGymPrice = findViewById(R.id.tvGymPrice);
        tvGymRating = findViewById(R.id.tvGymRating);
        tvGymDistance = findViewById(R.id.tvGymDistance);
        tvGymType = findViewById(R.id.tvGymType);
        tvDescription = findViewById(R.id.tvDescription);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        ratingBar = findViewById(R.id.ratingBar);
        btnSave = findViewById(R.id.btnSave);
        btnIgnore = findViewById(R.id.btnIgnore);
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);

        // Get data from intent
        String gymIcon = getIntent().getStringExtra("gym_icon");
        String gymName = getIntent().getStringExtra("gym_name");
        String gymPrice = getIntent().getStringExtra("gym_price");
        double gymRating = getIntent().getDoubleExtra("gym_rating", 0.0);
        String gymDistance = getIntent().getStringExtra("gym_distance");
        String gymType = getIntent().getStringExtra("gym_type");
        String gymDescription = getIntent().getStringExtra("gym_description");

        // Create current gym item
        currentGymItem = new GymItem(gymIcon, gymName, gymPrice, gymRating, gymDistance, gymType, gymDescription);

        // Set data to views
        tvGymIcon.setText(gymIcon);
        tvGymName.setText(gymName);
        tvGymPrice.setText(gymPrice);
        tvGymRating.setText("★★★★★ (" + gymRating + ")");
        tvGymDistance.setText("📍 " + gymDistance);
        tvGymType.setText(getTypeIcon(gymType) + " " + getTypeDisplayName(gymType));

        // Generate personalized description
        generatePersonalizedDescription(gymName, gymType, gymDescription);

        // Configurar RatingBar
        setupRatingBar();

        // Set button listeners
        btnSave.setOnClickListener(v -> handleSaveAction());
        btnIgnore.setOnClickListener(v -> handleIgnoreAction());
        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareRecommendation());
    }

    private void setupRatingBar() {
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            tvRatingValue.setText(String.format("%.1f / 5.0", rating));

            if (fromUser) {
                // Guardar calificación del usuario
                currentGymItem.setUserRating(rating);

                // Agregar al historial
                historyManager.addToHistory(
                    currentGymItem.getName(),
                    "gym_" + currentGymItem.getType(),
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
            return "¡Excelente! 💪 Buscaremos más opciones similares";
        } else if (rating >= 3.5f) {
            return "¡Muy bien! 👍 Tomaremos nota de tu preferencia";
        } else if (rating >= 2.5f) {
            return "Entendido 📝 Ajustaremos las sugerencias";
        } else if (rating >= 1.0f) {
            return "Gracias por tu opinión 💭";
        } else {
            return "Califica esta recomendación ⭐";
        }
    }

    private void shareRecommendation() {
        if (currentGymItem != null) {
            ShareUtils.shareGym(
                this,
                currentGymItem.getName(),
                currentGymItem.getPrice(),
                currentGymItem.getRating(),
                getTypeDisplayName(currentGymItem.getType()),
                currentGymItem.getDescription()
            );
        }
    }

    private void generatePersonalizedDescription(String gymName, String gymType, String baseDescription) {
        // Crear perfil del usuario
        SmartRecommendationEngine.UserProfile userProfile = new SmartRecommendationEngine.UserProfile();
        userProfile.name = preferencesManager.getUserName();
        userProfile.age = preferencesManager.getUserAge();
        userProfile.weight = preferencesManager.getUserWeight();
        userProfile.height = preferencesManager.getUserHeight();
        userProfile.budget = preferencesManager.getUserBudget();

        // Generar descripción personalizada
        double bmi = userProfile.calculateBMI();
        String bmiCategory = userProfile.getBMICategory();

        StringBuilder description = new StringBuilder();
        description.append("¡Hola ").append(userProfile.name).append("! ");
        description.append("Basado en tu IMC de ").append(String.format("%.1f", bmi));
        description.append(" y tus objetivos de fitness, ");

        switch (bmiCategory) {
            case "underweight":
                description.append("este ").append(getTypeDisplayName(gymType).toLowerCase())
                          .append(" te ayudará a ganar masa muscular y fuerza de manera saludable. ");
                if (gymType.equals("class")) {
                    description.append("Las clases grupales te mantendrán motivado mientras desarrollas músculo.");
                } else {
                    description.append("El entrenamiento de fuerza es ideal para tu objetivo de ganar peso saludable.");
                }
                break;
            case "overweight":
            case "obese":
                description.append("esta opción es excelente para ayudarte a quemar calorías y mejorar tu condición física. ");
                if (gymType.equals("class")) {
                    description.append("Las clases grupales te proporcionarán el cardio necesario de forma divertida.");
                } else {
                    description.append("El ejercicio regular te ayudará a alcanzar tus objetivos de peso de forma segura.");
                }
                break;
            default: // normal
                description.append("esta opción es perfecta para mantener tu excelente condición física actual. ");
                description.append("Te ayudará a mantenerte en forma y saludable.");
                break;
        }

        // Agregar información sobre presupuesto
        if (userProfile.budget > 0) {
            int price = extractPrice(getIntent().getStringExtra("gym_price"));
            double budgetPercentage = (price / userProfile.budget) * 100;

            if (budgetPercentage < 5) {
                description.append(" Además, este ").append(getTypeDisplayName(gymType).toLowerCase())
                          .append(" se ajusta perfectamente a tu presupuesto mensual.");
            } else if (budgetPercentage < 10) {
                description.append(" El costo representa un porcentaje razonable de tu presupuesto mensual.");
            } else {
                description.append(" Considera que esto representará un porcentaje significativo de tu presupuesto mensual, pero la inversión en tu salud siempre vale la pena.");
            }
        }

        description.append("\n\n").append(baseDescription);

        tvDescription.setText(description.toString());
    }

    private String getTypeIcon(String type) {
        switch (type) {
            case "membership": return "💳";
            case "class": return "👥";
            case "supplement": return "🥤";
            case "equipment": return "⚙️";
            default: return "💼";
        }
    }

    private String getTypeDisplayName(String type) {
        switch (type) {
            case "membership": return "Membresía";
            case "class": return "Clase";
            case "supplement": return "Suplemento";
            case "equipment": return "Equipo";
            default: return "Servicio";
        }
    }

    private void handleSaveAction() {
        if (currentGymItem != null) {
            // Si no tiene calificación, asignar una positiva
            if (currentGymItem.getUserRating() == 0) {
                currentGymItem.setUserRating(4.5f);
                historyManager.addToHistory(
                    currentGymItem.getName(),
                    "gym_" + currentGymItem.getType(),
                    4.5f,
                    System.currentTimeMillis()
                );
            }

            Toast.makeText(this, "Opción de gimnasio guardada ✅", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleIgnoreAction() {
        if (currentGymItem != null) {
            // Si no tiene calificación, asignar una baja
            if (currentGymItem.getUserRating() == 0) {
                currentGymItem.setUserRating(2.0f);
                historyManager.addToHistory(
                    currentGymItem.getName(),
                    "gym_" + currentGymItem.getType(),
                    2.0f,
                    System.currentTimeMillis()
                );
            }

            Toast.makeText(this, "Recomendación ignorada ❌", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private int extractPrice(String priceString) {
        try {
            return Integer.parseInt(priceString.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
