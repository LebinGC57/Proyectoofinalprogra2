package com.example.smarboyapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.api.USDAFoodService;
import com.example.smarboyapp.model.USDAResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView recyclerViewResults;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private Button btnBack;
    private FoodAdapter adapter;
    private List<FoodItem> searchResults;
    private USDAFoodService usdaFoodService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        usdaFoodService = new USDAFoodService();

        etSearch = findViewById(R.id.etSearch);
        recyclerViewResults = findViewById(R.id.recyclerViewResults);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        setupRecyclerView();
        setupSearch();

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        searchResults = new ArrayList<>();
        adapter = new FoodAdapter(searchResults, foodItem -> {
            // Aquí puedes abrir los detalles
        });
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResults.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    showEmptyState("🔍 Escribe algo para buscar");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        showLoading(true);

        usdaFoodService.searchFood(query, new USDAFoodService.FoodSearchCallback() {
            @Override
            public void onSuccess(USDAResponse response) {
                showLoading(false);

                if (response != null && response.getFoods() != null && !response.getFoods().isEmpty()) {
                    displayResults(response);
                } else {
                    showEmptyState("😔 No se encontraron resultados para \"" + query + "\"");
                }
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                showEmptyState("⚠️ Error al buscar. Intenta nuevamente.");
            }
        });
    }

    private void displayResults(USDAResponse response) {
        searchResults.clear();
        Random random = new Random();
        String[] foodIcons = {"🍽️", "🥗", "🍎", "🥑", "🍗", "🥕", "🍌", "🥒", "🍊", "🥩"};

        int maxItems = Math.min(response.getFoods().size(), 20);

        for (int i = 0; i < maxItems; i++) {
            USDAResponse.Food food = response.getFoods().get(i);

            String icon = foodIcons[random.nextInt(foodIcons.length)];
            String name = food.getDescription();

            if (name.length() > 50) {
                name = name.substring(0, 47) + "...";
            }

            String price = "Q" + (15 + random.nextInt(35));
            double rating = 3.5 + (random.nextDouble() * 1.5);
            String distance = (1 + random.nextInt(10)) + " min";
            String description = "Alimento nutritivo y saludable";

            searchResults.add(new FoodItem(icon, name, price, rating, distance, "food", description));
        }

        adapter.notifyDataSetChanged();
        tvEmptyState.setVisibility(View.GONE);
        recyclerViewResults.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewResults.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(String message) {
        tvEmptyState.setText(message);
        tvEmptyState.setVisibility(View.VISIBLE);
        recyclerViewResults.setVisibility(View.GONE);
    }
}

