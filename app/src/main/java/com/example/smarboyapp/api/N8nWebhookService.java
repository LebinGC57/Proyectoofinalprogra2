package com.example.smarboyapp.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class N8nWebhookService {

    private static final String TAG = "N8nWebhookService";
    private static final String WEBHOOK_URL = "https://lebincc.app.n8n.cloud/webhook-test/android-synck";
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface WebhookCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    /**
     * Enviar datos al webhook de n8n
     * @param data Mapa con los datos a enviar
     * @param callback Callback para manejar la respuesta
     */
    public static void sendToWebhook(Map<String, Object> data, WebhookCallback callback) {
        executorService.execute(() -> {
            try {
                // Crear objeto JSON con los datos
                JSONObject jsonData = new JSONObject(data);

                // Configurar la conexión
                URL url = new URL(WEBHOOK_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                // Enviar datos
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Leer respuesta
                int responseCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                    }

                    Log.d(TAG, "Webhook Success: " + response.toString());
                    if (callback != null) {
                        callback.onSuccess(response.toString());
                    }
                } else {
                    String error = "Error HTTP: " + responseCode;
                    Log.e(TAG, error);
                    if (callback != null) {
                        callback.onError(error);
                    }
                }

                connection.disconnect();

            } catch (Exception e) {
                String error = "Error al conectar con webhook: " + e.getMessage();
                Log.e(TAG, error, e);
                if (callback != null) {
                    callback.onError(error);
                }
            }
        });
    }

    /**
     * Enviar evento de usuario al webhook
     */
    public static void sendUserEvent(String eventType, String userName, String details, WebhookCallback callback) {
        try {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("event_type", eventType);
            data.put("user_name", userName);
            data.put("details", details);
            data.put("timestamp", System.currentTimeMillis());
            data.put("app_version", "1.0");

            sendToWebhook(data, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error creando evento de usuario", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    /**
     * Enviar preferencias del usuario al webhook
     */
    public static void sendUserPreferences(String userName, java.util.List<String> preferences, WebhookCallback callback) {
        try {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("event_type", "user_preferences");
            data.put("user_name", userName);
            data.put("preferences", preferences);
            data.put("timestamp", System.currentTimeMillis());

            sendToWebhook(data, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error enviando preferencias", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    /**
     * Enviar interacción con recomendación
     */
    public static void sendRecommendationInteraction(String userName, String itemName, String itemType,
                                                     String action, WebhookCallback callback) {
        try {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("event_type", "recommendation_interaction");
            data.put("user_name", userName);
            data.put("item_name", itemName);
            data.put("item_type", itemType);
            data.put("action", action); // "viewed", "saved", "ignored"
            data.put("timestamp", System.currentTimeMillis());

            sendToWebhook(data, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error enviando interacción", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    /**
     * Enviar calificación/review
     */
    public static void sendReview(String userName, String itemName, float rating,
                                   String reviewText, WebhookCallback callback) {
        try {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("event_type", "user_review");
            data.put("user_name", userName);
            data.put("item_name", itemName);
            data.put("rating", rating);
            data.put("review_text", reviewText);
            data.put("timestamp", System.currentTimeMillis());

            sendToWebhook(data, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error enviando review", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }
}
