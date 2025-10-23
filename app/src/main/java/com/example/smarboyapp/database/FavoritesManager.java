package com.example.smarboyapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.smarboyapp.FoodItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final String PREF_NAME = "SmartBuyFavorites";
    private static final String KEY_FAVORITES = "favorites_list";
    private static final String KEY_HISTORY = "food_history";

    private SharedPreferences preferences;

    public FavoritesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addToFavorites(FoodItem foodItem) {
        List<FoodItem> favorites = getFavorites();

        // Evitar duplicados
        for (FoodItem item : favorites) {
            if (item.getName().equals(foodItem.getName())) {
                return;
            }
        }

        favorites.add(foodItem);
        saveFavorites(favorites);
    }

    public void removeFromFavorites(String foodName) {
        List<FoodItem> favorites = getFavorites();
        favorites.removeIf(item -> item.getName().equals(foodName));
        saveFavorites(favorites);
    }

    public void removeFromFavorites(FoodItem foodItem) {
        removeFromFavorites(foodItem.getName());
    }

    public void clearAllFavorites() {
        preferences.edit().putString(KEY_FAVORITES, "[]").apply();
    }

    public List<FoodItem> getFavorites() {
        String favoritesJson = preferences.getString(KEY_FAVORITES, "[]");
        return parseFoodList(favoritesJson);
    }

    public void addToHistory(FoodItem foodItem) {
        List<FoodItem> history = getHistory();

        // Mantener solo los últimos 20 elementos
        if (history.size() >= 20) {
            history.remove(0);
        }

        history.add(foodItem);
        saveHistory(history);
    }

    public List<FoodItem> getHistory() {
        String historyJson = preferences.getString(KEY_HISTORY, "[]");
        return parseFoodList(historyJson);
    }

    public boolean isFavorite(String foodName) {
        List<FoodItem> favorites = getFavorites();
        for (FoodItem item : favorites) {
            if (item.getName().equals(foodName)) {
                return true;
            }
        }
        return false;
    }

    private void saveFavorites(List<FoodItem> favorites) {
        String json = foodListToJson(favorites);
        preferences.edit().putString(KEY_FAVORITES, json).apply();
    }

    private void saveHistory(List<FoodItem> history) {
        String json = foodListToJson(history);
        preferences.edit().putString(KEY_HISTORY, json).apply();
    }

    private String foodListToJson(List<FoodItem> foodList) {
        JSONArray jsonArray = new JSONArray();

        for (FoodItem item : foodList) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("icon", item.getIcon());
                jsonObject.put("name", item.getName());
                jsonObject.put("price", item.getPrice());
                jsonObject.put("rating", item.getRating());
                jsonObject.put("distance", item.getDistance());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray.toString();
    }

    private List<FoodItem> parseFoodList(String json) {
        List<FoodItem> foodList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FoodItem item = new FoodItem(
                    jsonObject.getString("icon"),
                    jsonObject.getString("name"),
                    jsonObject.getString("price"),
                    jsonObject.getDouble("rating"),
                    jsonObject.getString("distance")
                );
                foodList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return foodList;
    }
}
