package com.example.smarboyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarboyapp.database.FavoritesManager;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private TextView tvEmptyState;
    private Button btnBack, btnClearAll;
    private FoodAdapter adapter;
    private FavoritesManager favoritesManager;
    private List<FoodItem> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesManager = new FavoritesManager(this);

        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnBack = findViewById(R.id.btnBack);
        btnClearAll = findViewById(R.id.btnClearAll);

        setupRecyclerView();
        loadFavorites();

        btnBack.setOnClickListener(v -> finish());
        btnClearAll.setOnClickListener(v -> showClearAllDialog());
    }

    private void setupRecyclerView() {
        favoritesList = favoritesManager.getFavorites();
        adapter = new FoodAdapter(favoritesList, foodItem -> {
            // Mostrar opciones al hacer clic en un favorito
            showFavoriteOptions(foodItem);
        });
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFavorites.setAdapter(adapter);
    }

    private void loadFavorites() {
        favoritesList.clear();
        favoritesList.addAll(favoritesManager.getFavorites());
        adapter.notifyDataSetChanged();

        if (favoritesList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewFavorites.setVisibility(View.GONE);
            btnClearAll.setEnabled(false);
            btnClearAll.setAlpha(0.5f);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewFavorites.setVisibility(View.VISIBLE);
            btnClearAll.setEnabled(true);
            btnClearAll.setAlpha(1.0f);
        }
    }

    private void showFavoriteOptions(FoodItem foodItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(foodItem.getName());
        builder.setItems(new CharSequence[]{"Ver Detalles", "Eliminar de Favoritos"}, (dialog, which) -> {
            if (which == 0) {
                // Ver detalles (puedes implementar navegación aquí)
                Toast.makeText(this, "Función próximamente", Toast.LENGTH_SHORT).show();
            } else {
                // Eliminar
                removeFavorite(foodItem);
            }
        });
        builder.show();
    }

    private void removeFavorite(FoodItem foodItem) {
        favoritesManager.removeFromFavorites(foodItem);
        loadFavorites();
        Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
    }

    private void showClearAllDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar todos los favoritos")
            .setMessage("¿Estás seguro de que quieres eliminar todos tus favoritos?")
            .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                favoritesManager.clearAllFavorites();
                loadFavorites();
                Toast.makeText(this, "Todos los favoritos eliminados", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }
}

