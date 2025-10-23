package com.example.smarboyapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.smarboyapp.api.N8nWebhookService;
import com.example.smarboyapp.database.UserPreferencesManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager para sincronizar eventos de la app con n8n
 */
public class N8nSyncManager {

    private static final String TAG = "N8nSyncManager";
    private final Context context;
    private final UserPreferencesManager preferencesManager;

    public N8nSyncManager(Context context) {
        this.context = context;
        this.preferencesManager = new UserPreferencesManager(context);
    }

    /**
     * Sincronizar login del usuario
     */
    public void syncUserLogin(String email) {
        String userName = preferencesManager.getUserName();
        Map<String, Object> data = new HashMap<>();
        data.put("event_type", "user_login");
        data.put("user_name", userName != null ? userName : "Usuario");
        data.put("email", email);
        data.put("timestamp", System.currentTimeMillis());
        data.put("device", "Android");

        N8nWebhookService.sendToWebhook(data, new N8nWebhookService.WebhookCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Login sincronizado: " + response);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error sincronizando login: " + error);
            }
        });
    }

    /**
     * Sincronizar cuando el usuario guarda un favorito
     */
    public void syncFavoriteAdded(String itemName, String itemType, String itemPrice) {
        String userName = preferencesManager.getUserName();
        Map<String, Object> data = new HashMap<>();
        data.put("event_type", "favorite_added");
        data.put("user_name", userName != null ? userName : "Usuario");
        data.put("item_name", itemName);
        data.put("item_type", itemType);
        data.put("item_price", itemPrice);
        data.put("timestamp", System.currentTimeMillis());

        N8nWebhookService.sendToWebhook(data, new N8nWebhookService.WebhookCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Favorito sincronizado: " + response);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error sincronizando favorito: " + error);
            }
        });
    }

    /**
     * Sincronizar cuando el usuario actualiza su perfil
     */
    public void syncProfileUpdate(String name, int age, double weight, double height) {
        Map<String, Object> data = new HashMap<>();
        data.put("event_type", "profile_update");
        data.put("user_name", name);
        data.put("age", age);
        data.put("weight", weight);
        data.put("height", height);
        data.put("bmi", calculateBMI(weight, height));
        data.put("timestamp", System.currentTimeMillis());

        N8nWebhookService.sendToWebhook(data, new N8nWebhookService.WebhookCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Perfil sincronizado: " + response);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error sincronizando perfil: " + error);
            }
        });
    }

    /**
     * Sincronizar interacción con el asistente de IA
     */
    public void syncAiInteraction(String userMessage, String aiResponse) {
        String userName = preferencesManager.getUserName();
        Map<String, Object> data = new HashMap<>();
        data.put("event_type", "ai_interaction");
        data.put("user_name", userName != null ? userName : "Usuario");
        data.put("user_message", userMessage);
        data.put("ai_response", aiResponse);
        data.put("timestamp", System.currentTimeMillis());

        N8nWebhookService.sendToWebhook(data, new N8nWebhookService.WebhookCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Interacción AI sincronizada: " + response);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error sincronizando AI: " + error);
            }
        });
    }

    /**
     * Sincronizar preferencias del usuario
     */
    public void syncPreferences(List<String> preferences) {
        String userName = preferencesManager.getUserName();
        N8nWebhookService.sendUserPreferences(
            userName != null ? userName : "Usuario",
            preferences,
            new N8nWebhookService.WebhookCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "Preferencias sincronizadas: " + response);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error sincronizando preferencias: " + error);
                }
            }
        );
    }

    /**
     * Sincronizar búsqueda del usuario
     */
    public void syncSearch(String searchQuery, int resultsCount) {
        String userName = preferencesManager.getUserName();
        Map<String, Object> data = new HashMap<>();
        data.put("event_type", "search");
        data.put("user_name", userName != null ? userName : "Usuario");
        data.put("search_query", searchQuery);
        data.put("results_count", resultsCount);
        data.put("timestamp", System.currentTimeMillis());

        N8nWebhookService.sendToWebhook(data, new N8nWebhookService.WebhookCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Búsqueda sincronizada: " + response);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error sincronizando búsqueda: " + error);
            }
        });
    }

    /**
     * Enviar evento genérico personalizado
     */
    public void syncCustomEvent(String eventType, Map<String, Object> eventData) {
        String userName = preferencesManager.getUserName();
        eventData.put("event_type", eventType);
        eventData.put("user_name", userName != null ? userName : "Usuario");
        eventData.put("timestamp", System.currentTimeMillis());

        N8nWebhookService.sendToWebhook(eventData, new N8nWebhookService.WebhookCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Evento personalizado sincronizado: " + response);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error sincronizando evento: " + error);
            }
        });
    }

    private double calculateBMI(double weight, double height) {
        if (height > 0) {
            double heightInMeters = height / 100.0;
            return weight / (heightInMeters * heightInMeters);
        }
        return 0;
    }
}
