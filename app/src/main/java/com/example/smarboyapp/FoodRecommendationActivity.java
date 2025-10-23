package com.example.smarboyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.database.FavoritesManager;
import com.example.smarboyapp.database.UserPreferencesManager;
import com.example.smarboyapp.utils.SmartRecommendationEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodRecommendationActivity extends AppCompatActivity {

    private static final String TAG = "FoodRecommendation";
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<FoodItem> foodList;
    private ProgressBar progressBar;
    private TextView tvLoadingMessage;
    private UserPreferencesManager preferencesManager;
    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate iniciado");

        setContentView(R.layout.activity_food_recommendation);

        Log.d(TAG, "Layout establecido");

        recyclerView = findViewById(R.id.recyclerViewFood);
        progressBar = findViewById(R.id.progressBar);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnNext = findViewById(R.id.btnNext);

        Log.d(TAG, "Vistas inicializadas");

        preferencesManager = new UserPreferencesManager(this);
        favoritesManager = new FavoritesManager(this);

        setupRecyclerView();
        setupButtons(btnBack, btnNext);

        Log.d(TAG, "Iniciando carga de recomendaciones");
        loadPersonalizedRecommendations();
    }

    private void setupRecyclerView() {
        Log.d(TAG, "Configurando RecyclerView");
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(foodList, foodItem -> {
            Log.d(TAG, "Item clickeado: " + foodItem.getName());
            // Al hacer clic en un item, abrir los detalles
            Intent intent = new Intent(this, DetailedFoodRecommendationActivity.class);
            intent.putExtra("food_icon", foodItem.getIcon());
            intent.putExtra("food_name", foodItem.getName());
            intent.putExtra("food_price", foodItem.getPrice());
            intent.putExtra("food_rating", foodItem.getRating());
            intent.putExtra("food_distance", foodItem.getDistance());
            intent.putExtra("food_description", foodItem.getDescription());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "RecyclerView configurado correctamente");
    }

    private void setupButtons(Button btnBack, Button btnNext) {
        btnBack.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        });
    }

    private void loadPersonalizedRecommendations() {
        Log.d(TAG, "loadPersonalizedRecommendations iniciado");
        showLoading(true);
        tvLoadingMessage.setText("🔍 Buscando recomendaciones personalizadas...");

        // Obtener preferencias del usuario
        ArrayList<String> selectedPreferences = getIntent().getStringArrayListExtra("selected_preferences");
        Log.d(TAG, "Preferencias recibidas: " + (selectedPreferences != null ? selectedPreferences.toString() : "null"));

        String category = determineMainCategory(selectedPreferences);
        Log.d(TAG, "Categoría determinada: " + category);

        // Simular carga con delay y mostrar recomendaciones locales
        new Handler().postDelayed(() -> {
            Log.d(TAG, "Delay completado, cargando recomendaciones");
            showLoading(false);
            loadLocalRecommendations(category);

            Log.d(TAG, "Recomendaciones cargadas: " + foodList.size() + " items");
            Toast.makeText(FoodRecommendationActivity.this,
                "¡Encontramos " + foodList.size() + " recomendaciones para ti!",
                Toast.LENGTH_SHORT).show();
        }, 2000);
    }

    private String determineMainCategory(ArrayList<String> preferences) {
        if (preferences != null) {
            for (String pref : preferences) {
                if (pref.contains("Saludable")) return "healthy";
                if (pref.contains("Proteína")) return "protein";
                if (pref.contains("Vegetariano")) return "vegetarian";
                if (pref.contains("Bajas Calorías")) return "low_calorie";
                if (pref.contains("Comida Rápida")) return "fast_food";
                if (pref.contains("Postres")) return "dessert";
            }
        }
        return "healthy"; // Por defecto
    }

    private void loadLocalRecommendations(String category) {
        Log.d(TAG, "loadLocalRecommendations para categoría: " + category);
        foodList.clear();

        // Recomendaciones locales personalizadas
        switch (category) {
            case "healthy":
                addHealthyFoods();
                break;
            case "protein":
                addProteinFoods();
                break;
            case "vegetarian":
                addVegetarianFoods();
                break;
            case "low_calorie":
                addLowCalorieFoods();
                break;
            case "fast_food":
                addFastFoods();
                break;
            case "dessert":
                addDesserts();
                break;
            default:
                addHealthyFoods();
                break;
        }

        Log.d(TAG, "Se agregaron " + foodList.size() + " items a la lista");
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Adapter notificado de cambios");
    }

    private void addHealthyFoods() {
        foodList.add(new FoodItem("🥗", "Ensalada César Saludable", "Q25", 4.5, "5 min", "food", "Ensalada fresca con pollo a la parrilla, lechuga romana, parmesano y aderezo ligero."));
        foodList.add(new FoodItem("🍎", "Bowl de Quinoa y Vegetales", "Q30", 4.7, "8 min", "food", "Quinoa orgánica con verduras de temporada, aguacate y semillas de girasol."));
        foodList.add(new FoodItem("🥑", "Tostada de Aguacate", "Q22", 4.3, "3 min", "food", "Pan integral tostado con aguacate fresco, tomate cherry y sal marina."));
        foodList.add(new FoodItem("🍗", "Pechuga a la Plancha", "Q35", 4.6, "12 min", "food", "Pechuga de pollo marinada con hierbas, acompañada de vegetales al vapor."));
        foodList.add(new FoodItem("🥕", "Smoothie Verde Detox", "Q18", 4.4, "2 min", "food", "Batido con espinacas, manzana verde, apio, limón y jengibre."));
    }

    private void addProteinFoods() {
        foodList.add(new FoodItem("🥩", "Steak de Res Premium", "Q65", 4.8, "15 min", "food", "Corte premium de res a la parrilla con especias especiales."));
        foodList.add(new FoodItem("🍗", "Pollo Teriyaki", "Q40", 4.5, "10 min", "food", "Pollo jugoso con salsa teriyaki casera y vegetales."));
        foodList.add(new FoodItem("🐟", "Salmón Grillado", "Q55", 4.7, "12 min", "food", "Salmón fresco a la parrilla con limón y hierbas."));
        foodList.add(new FoodItem("🥚", "Revuelto de Claras", "Q20", 4.2, "5 min", "food", "Claras de huevo con vegetales y queso bajo en grasa."));
    }

    private void addVegetarianFoods() {
        foodList.add(new FoodItem("🥦", "Curry de Vegetales", "Q28", 4.4, "15 min", "food", "Curry aromático con vegetales mixtos y leche de coco."));
        foodList.add(new FoodItem("🍄", "Hamburguesa de Portobello", "Q32", 4.3, "8 min", "food", "Hongo portobello a la parrilla con vegetales frescos."));
        foodList.add(new FoodItem("🌯", "Wrap Vegano", "Q24", 4.1, "5 min", "food", "Tortilla integral con hummus, vegetales y germinados."));
    }

    private void addLowCalorieFoods() {
        foodList.add(new FoodItem("🥒", "Gazpacho Andaluz", "Q15", 4.0, "3 min", "food", "Sopa fría de vegetales, perfecta para dietas bajas en calorías."));
        foodList.add(new FoodItem("🥬", "Ensalada de Kale", "Q22", 4.2, "4 min", "food", "Kale massageado con limón, aceite de oliva y semillas."));
        foodList.add(new FoodItem("🍅", "Caprese Light", "Q20", 4.1, "3 min", "food", "Tomate, mozzarella light y albahaca fresca."));
    }

    private void addFastFoods() {
        foodList.add(new FoodItem("🍔", "Burger Clásica", "Q35", 4.2, "8 min", "food", "Hamburguesa con carne de res, lechuga, tomate y papas."));
        foodList.add(new FoodItem("🍕", "Pizza Margarita", "Q40", 4.4, "12 min", "food", "Pizza tradicional con tomate, mozzarella y albahaca."));
        foodList.add(new FoodItem("🌭", "Hot Dog Especial", "Q18", 3.8, "5 min", "food", "Salchicha premium con cebolla caramelizada y mostaza."));
    }

    private void addDesserts() {
        foodList.add(new FoodItem("🍰", "Cheesecake de Fresa", "Q28", 4.6, "5 min", "food", "Cheesecake cremoso con fresas frescas y coulis."));
        foodList.add(new FoodItem("🍫", "Brownie con Helado", "Q25", 4.5, "7 min", "food", "Brownie de chocolate tibio con helado de vainilla."));
        foodList.add(new FoodItem("🧁", "Cupcake Red Velvet", "Q15", 4.3, "3 min", "food", "Cupcake esponjoso con frosting de queso crema."));
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvLoadingMessage.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
