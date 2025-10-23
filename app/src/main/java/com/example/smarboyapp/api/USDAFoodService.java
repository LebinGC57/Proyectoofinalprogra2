package com.example.smarboyapp.api;

import android.os.Handler;
import android.os.Looper;

import com.example.smarboyapp.model.USDAResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class USDAFoodService {
    private static final String API_KEY = "Crc2Y2JTpI0C63tWUNyXZ0o0Qgsglpn3AKkMp564";
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/search";

    private OkHttpClient client;
    private Gson gson;

    public USDAFoodService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public interface FoodSearchCallback {
        void onSuccess(USDAResponse response);
        void onError(String error);
    }

    public void searchFood(String query, FoodSearchCallback callback) {
        String url = BASE_URL + "?query=" + query + "&api_key=" + API_KEY + "&pageSize=20";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                    callback.onError(e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    USDAResponse usdaResponse = gson.fromJson(responseBody, USDAResponse.class);

                    new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess(usdaResponse)
                    );
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error en la respuesta del servidor")
                    );
                }
            }
        });
    }

    public void getRecommendationsForCategory(String category, FoodSearchCallback callback) {
        String query;
        switch (category) {
            case "healthy":
                query = "salad vegetables fruit";
                break;
            case "protein":
                query = "chicken beef fish protein";
                break;
            case "vegetarian":
                query = "vegetables tofu beans";
                break;
            case "low_calorie":
                query = "low calorie diet";
                break;
            case "fast_food":
                query = "burger pizza fast food";
                break;
            case "dessert":
                query = "cake dessert sweet";
                break;
            default:
                query = "healthy food";
                break;
        }

        searchFood(query, callback);
    }
}

