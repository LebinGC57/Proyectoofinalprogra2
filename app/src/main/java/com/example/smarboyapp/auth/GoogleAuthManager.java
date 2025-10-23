package com.example.smarboyapp.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.regex.Pattern;

public class GoogleAuthManager {
    private static final String PREF_NAME = "GoogleAuth";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_display_name";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences preferences;
    private Context context;

    public GoogleAuthManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public interface AuthCallback {
        void onSuccess(String email, String name);
        void onError(String error);
    }

    // Simulación de login con Gmail (sin Google Play Services para simplicidad)
    public void signInWithGmail(String email, String password, AuthCallback callback) {
        // Validar formato de email
        if (!isValidGmailAddress(email)) {
            callback.onError("Por favor ingresa una dirección de Gmail válida");
            return;
        }

        // Validar contraseña mínima
        if (password == null || password.length() < 6) {
            callback.onError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // Simular autenticación (en una app real usarías Google Sign-In API)
        simulateGmailAuth(email, password, callback);
    }

    private void simulateGmailAuth(String email, String password, AuthCallback callback) {
        // Simular delay de autenticación
        new android.os.Handler().postDelayed(() -> {
            // Para demostración, aceptamos cualquier email de Gmail válido
            String displayName = extractNameFromEmail(email);
            String userId = generateUserId(email);

            // Guardar datos del usuario
            saveUserData(email, displayName, userId);

            callback.onSuccess(email, displayName);

        }, 1500); // Simular 1.5 segundos de autenticación
    }

    public void signOut() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(context, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "Usuario");
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, "");
    }

    private boolean isValidGmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Patrón para validar email de Gmail
        String gmailPattern = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        return Pattern.matches(gmailPattern, email.toLowerCase().trim());
    }

    private String extractNameFromEmail(String email) {
        // Extraer nombre del email (parte antes del @)
        String localPart = email.substring(0, email.indexOf("@"));

        // Capitalizar primera letra y reemplazar puntos/guiones con espacios
        String name = localPart.replace(".", " ").replace("_", " ").replace("-", " ");

        // Capitalizar cada palabra
        String[] words = name.split("\\s+");
        StringBuilder capitalizedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedName.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    capitalizedName.append(word.substring(1).toLowerCase());
                }
                capitalizedName.append(" ");
            }
        }

        return capitalizedName.toString().trim();
    }

    private String generateUserId(String email) {
        return "user_" + Math.abs(email.hashCode());
    }

    private void saveUserData(String email, String displayName, String userId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, displayName);
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Método para registrar nuevo usuario
    public void registerWithGmail(String email, String password, String confirmPassword, AuthCallback callback) {
        // Validaciones
        if (!isValidGmailAddress(email)) {
            callback.onError("Por favor ingresa una dirección de Gmail válida");
            return;
        }

        if (password == null || password.length() < 6) {
            callback.onError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!password.equals(confirmPassword)) {
            callback.onError("Las contraseñas no coinciden");
            return;
        }

        // Simular registro
        simulateGmailRegistration(email, password, callback);
    }

    private void simulateGmailRegistration(String email, String password, AuthCallback callback) {
        new android.os.Handler().postDelayed(() -> {
            String displayName = extractNameFromEmail(email);
            String userId = generateUserId(email);

            // Guardar datos del nuevo usuario
            saveUserData(email, displayName, userId);

            callback.onSuccess(email, displayName);

        }, 2000); // Simular 2 segundos de registro
    }
}
