package com.example.smarboyapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetworkManager {
    
    private static final String TAG = "NetworkManager";
    
    // URLs de los webhooks de N8N
    public static final String WEBHOOK_QUE_COCINO_HOY = "https://lebincc.app.n8n.cloud/webhook-test/QUECOCINOHOY";
    public static final String WEBHOOK_CHEF_VOZ = "https://lebincc.app.n8n.cloud/webhook-test/chefdevoz";
    public static final String WEBHOOK_CHEF_INNOVADOR = "https://lebincc.app.n8n.cloud/webhook/cheftinnovador";
    public static final String WEBHOOK_GESTOR_INVENTARIO = "https://lebincc.app.n8n.cloud/webhook-test/gestorinventario";
    
    public interface NetworkCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    
    public static void sendToN8N(Context context, String webhookUrl, JSONObject data, NetworkCallback callback) {
        new SendToN8NTask(context, webhookUrl, data, callback).execute();
    }
    
    private static class SendToN8NTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private String webhookUrl;
        private JSONObject data;
        private NetworkCallback callback;
        private String errorMessage;
        
        public SendToN8NTask(Context context, String webhookUrl, JSONObject data, NetworkCallback callback) {
            this.context = context;
            this.webhookUrl = webhookUrl;
            this.data = data;
            this.callback = callback;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Enviando datos a N8N: " + webhookUrl);
        }
        
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                // Configurar la conexión
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000); // 10 segundos
                connection.setReadTimeout(15000); // 15 segundos
                
                // Enviar datos
                String jsonData = data.toString();
                Log.d(TAG, "Datos enviados: " + jsonData);
                
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                // Leer respuesta
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Código de respuesta: " + responseCode);

                BufferedReader reader;
                if (responseCode >= 200 && responseCode < 300) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseBody = response.toString();
                Log.d(TAG, "Respuesta de N8N: " + responseBody);

                if (responseCode >= 200 && responseCode < 300) {
                    return responseBody;
                } else {
                    errorMessage = "Error del servidor: " + responseCode + " - " + responseBody;

                    // Añadir sugerencia específica si el webhook no está registrado (404)
                    if (responseCode == 404 && responseBody != null && responseBody.toLowerCase().contains("webhook")) {
                        errorMessage += "\nSugerencia: Verifica que el webhook esté registrado en n8n y que el workflow esté activo. En modo prueba debes pulsar 'Execute workflow' en la interfaz de n8n antes de enviar llamadas.";
                    }

                    return null;
                }

            } catch (IOException e) {
                Log.e(TAG, "Error de conexión: " + e.getMessage());
                errorMessage = "Error de conexión: " + e.getMessage();
                return null;
            } catch (Exception e) {
                Log.e(TAG, "Error inesperado: " + e.getMessage());
                errorMessage = "Error inesperado: " + e.getMessage();
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            
            if (result != null) {
                Log.d(TAG, "Respuesta exitosa de N8N");
                if (callback != null) {
                    callback.onSuccess(result);
                }
            } else {
                Log.e(TAG, "Error en la respuesta: " + errorMessage);
                if (callback != null) {
                    callback.onError(errorMessage);
                }
            }
        }
    }
    
    // Métodos específicos para cada funcionalidad
    
    public static void sendQueCocinoHoy(Context context, String ingredients, NetworkCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("ingredients", ingredients);
            data.put("timestamp", System.currentTimeMillis());
            data.put("source", "android_app");
            data.put("function", "que_cocino_hoy");
            
            sendToN8N(context, WEBHOOK_QUE_COCINO_HOY, data, callback);
        } catch (JSONException e) {
            Log.e(TAG, "Error creando JSON para ¿Qué cocino hoy?: " + e.getMessage());
            if (callback != null) {
                callback.onError("Error preparando datos: " + e.getMessage());
            }
        }
    }
    
    public static void sendChefVoz(Context context, String voiceData, NetworkCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("voice_data", voiceData);
            data.put("timestamp", System.currentTimeMillis());
            data.put("source", "android_app");
            data.put("function", "chef_voz");
            
            sendToN8N(context, WEBHOOK_CHEF_VOZ, data, callback);
        } catch (JSONException e) {
            Log.e(TAG, "Error creando JSON para Chef Voz: " + e.getMessage());
            if (callback != null) {
                callback.onError("Error preparando datos: " + e.getMessage());
            }
        }
    }
    
    public static void sendChefInnovador(Context context, String ingredients, NetworkCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("ingredients", ingredients);
            data.put("timestamp", System.currentTimeMillis());
            data.put("source", "android_app");
            data.put("function", "chef_innovador");
            
            sendToN8N(context, WEBHOOK_CHEF_INNOVADOR, data, callback);
        } catch (JSONException e) {
            Log.e(TAG, "Error creando JSON para Chef Innovador: " + e.getMessage());
            if (callback != null) {
                callback.onError("Error preparando datos: " + e.getMessage());
            }
        }
    }
    
    public static void sendGestorInventario(Context context, String inventory, String menuObjective, String basicNeeds, NetworkCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("inventory", inventory);
            data.put("menu_objective", menuObjective);
            data.put("basic_needs", basicNeeds);
            data.put("timestamp", System.currentTimeMillis());
            data.put("source", "android_app");
            data.put("function", "gestor_inventario");
            
            sendToN8N(context, WEBHOOK_GESTOR_INVENTARIO, data, callback);
        } catch (JSONException e) {
            Log.e(TAG, "Error creando JSON para Gestor Inventario: " + e.getMessage());
            if (callback != null) {
                callback.onError("Error preparando datos: " + e.getMessage());
            }
        }
    }
}
