package com.example.smarboyapp.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.smarboyapp.FoodItem;

public class USDAApiClient {
    private static final String API_KEY = "Crc2Y2JTpI0C63tWUNyXZ0o0Qgsglpn3AKkMp564";
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/search";
    private static final String TAG = "USDAApiClient";

    public interface ApiCallback {
        void onSuccess(List<FoodItem> foods);
        void onError(String error);
    }

    public static void searchFoods(String query, int pageSize, ApiCallback callback) {
        new SearchFoodsTask(callback).execute(query, String.valueOf(pageSize));
    }

    private static class SearchFoodsTask extends AsyncTask<String, Void, List<FoodItem>> {
        private ApiCallback callback;
        private String errorMessage;

        public SearchFoodsTask(ApiCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<FoodItem> doInBackground(String... params) {
            String query = params[0];
            String pageSize = params[1];
            List<FoodItem> foodItems = new ArrayList<>();

            try {
                String urlString = BASE_URL + "?query=" + query +
                                  "&api_key=" + API_KEY +
                                  "&pageSize=" + pageSize;

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    foodItems = parseResponse(response.toString());
                } else {
                    errorMessage = "Error: " + responseCode;
                }

                connection.disconnect();

            } catch (IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                errorMessage = "Error de conexión: " + e.getMessage();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error: " + e.getMessage());
                errorMessage = "Error inesperado: " + e.getMessage();
            }

            return foodItems;
        }

        @Override
        protected void onPostExecute(List<FoodItem> result) {
            if (errorMessage != null) {
                callback.onError(errorMessage);
            } else {
                callback.onSuccess(result);
            }
        }

        private List<FoodItem> parseResponse(String jsonResponse) {
            List<FoodItem> foodItems = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray foods = jsonObject.getJSONArray("foods");

                for (int i = 0; i < foods.length() && i < 10; i++) { // Limitar a 10 elementos
                    JSONObject food = foods.getJSONObject(i);

                    String description = food.optString("description", "Alimento");
                    String brandOwner = food.optString("brandOwner", "");

                    // Generar icono basado en el tipo de alimento
                    String icon = getFoodIcon(description);

                    // Generar precio simulado (entre Q 25 y Q 80)
                    int price = 25 + (i * 7) % 55;

                    // Generar calificación simulada (entre 3.5 y 5.0)
                    double rating = 3.5 + (i * 0.2) % 1.5;
                    rating = Math.round(rating * 10.0) / 10.0;

                    // Generar distancia simulada (entre 3 y 15 minutos)
                    int walkTime = 3 + (i * 2) % 12;

                    String name = formatFoodName(description, brandOwner);
                    String priceString = "Q " + price;
                    String distance = "A " + walkTime + " minutos caminando";

                    FoodItem foodItem = new FoodItem(icon, name, priceString, rating, distance);
                    foodItems.add(foodItem);
                }

            } catch (JSONException e) {
                Log.e(TAG, "JSON parsing error: " + e.getMessage());
            }

            return foodItems;
        }

        private String getFoodIcon(String description) {
            String lowerDesc = description.toLowerCase();

            if (lowerDesc.contains("chicken") || lowerDesc.contains("pollo")) return "🍗";
            if (lowerDesc.contains("beef") || lowerDesc.contains("carne")) return "🥩";
            if (lowerDesc.contains("fish") || lowerDesc.contains("pescado") || lowerDesc.contains("salmon")) return "🐟";
            if (lowerDesc.contains("pizza")) return "🍕";
            if (lowerDesc.contains("burger") || lowerDesc.contains("hamburguesa")) return "🍔";
            if (lowerDesc.contains("taco") || lowerDesc.contains("burrito")) return "🌮";
            if (lowerDesc.contains("salad") || lowerDesc.contains("ensalada")) return "🥗";
            if (lowerDesc.contains("pasta") || lowerDesc.contains("spaghetti")) return "🍝";
            if (lowerDesc.contains("rice") || lowerDesc.contains("arroz")) return "🍚";
            if (lowerDesc.contains("bread") || lowerDesc.contains("pan")) return "🍞";
            if (lowerDesc.contains("fruit") || lowerDesc.contains("fruta")) return "🍎";
            if (lowerDesc.contains("vegetable") || lowerDesc.contains("vegetal")) return "🥕";

            return "🍽️"; // Icono por defecto
        }

        private String formatFoodName(String description, String brandOwner) {
            String name = description.toUpperCase();

            // Limitar la longitud del nombre
            if (name.length() > 30) {
                name = name.substring(0, 27) + "...";
            }

            return name;
        }
    }
}
