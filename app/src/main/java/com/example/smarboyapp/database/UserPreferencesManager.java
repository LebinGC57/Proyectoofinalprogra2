package com.example.smarboyapp.database;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferencesManager {
    private static final String PREF_NAME = "SmartBuyPreferences";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_AGE = "user_age";
    private static final String KEY_USER_WEIGHT = "user_weight";
    private static final String KEY_USER_HEIGHT = "user_height";
    private static final String KEY_USER_BUDGET = "user_budget";
    private static final String KEY_PROFILE_COMPLETED = "profile_completed";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    private SharedPreferences preferences;

    public UserPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserProfile(String name, int age, double weight, double height, double budget) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER_NAME, name);
        editor.putInt(KEY_USER_AGE, age);
        editor.putFloat(KEY_USER_WEIGHT, (float) weight);
        editor.putFloat(KEY_USER_HEIGHT, (float) height);
        editor.putFloat(KEY_USER_BUDGET, (float) budget);
        editor.putBoolean(KEY_PROFILE_COMPLETED, true);
        editor.apply();
    }

    public boolean isProfileCompleted() {
        return preferences.getBoolean(KEY_PROFILE_COMPLETED, false);
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    public int getUserAge() {
        return preferences.getInt(KEY_USER_AGE, 0);
    }

    public double getUserWeight() {
        return preferences.getFloat(KEY_USER_WEIGHT, 0.0f);
    }

    public double getUserHeight() {
        return preferences.getFloat(KEY_USER_HEIGHT, 0.0f);
    }

    public double getUserBudget() {
        return preferences.getFloat(KEY_USER_BUDGET, 0.0f);
    }

    // Métodos para las preferencias del usuario
    public void saveUserPreference(String itemType, boolean liked) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("pref_" + itemType, liked);
        editor.apply();
    }

    public boolean getUserPreference(String itemType, boolean defaultValue) {
        return preferences.getBoolean("pref_" + itemType, defaultValue);
    }

    public void incrementPreferenceCount(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        int currentCount = preferences.getInt(key, 0);
        editor.putInt(key, currentCount + 1);
        editor.apply();
    }

    public int getPreferenceCount(String key) {
        return preferences.getInt(key, 0);
    }

    // Métodos adicionales para compatibilidad
    public void incrementPreference(String itemType) {
        incrementPreferenceCount(itemType + "_liked");
    }

    public void decrementPreference(String itemType) {
        incrementPreferenceCount(itemType + "_disliked");
    }

    public void clearProfile() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    // Métodos para modo oscuro
    public void setDarkModeEnabled(boolean enabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_DARK_MODE, enabled);
        editor.apply();
    }

    public boolean isDarkModeEnabled() {
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }

    // Métodos para idioma
    private static final String KEY_ENGLISH = "english_enabled";

    public void setEnglishEnabled(boolean enabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_ENGLISH, enabled);
        editor.apply();
    }

    public boolean isEnglishEnabled() {
        return preferences.getBoolean(KEY_ENGLISH, false);
    }

    // Métodos para notificaciones
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";

    public void setNotificationsEnabled(boolean enabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_NOTIFICATIONS, enabled);
        editor.apply();
    }

    public boolean areNotificationsEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATIONS, true); // Por defecto activadas
    }

    // Métodos para filtros
    public void saveFilter(String filterType, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("filter_" + filterType, value);
        editor.apply();
    }

    public String getFilter(String filterType) {
        return preferences.getString("filter_" + filterType, "all");
    }

    public void saveFilterBoolean(String filterType, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("filter_" + filterType, value);
        editor.apply();
    }

    public boolean getFilterBoolean(String filterType) {
        return preferences.getBoolean("filter_" + filterType, false);
    }

    public void clearFilters() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("filter_price");
        editor.remove("filter_distance");
        editor.remove("filter_rating");
        editor.remove("filter_healthy");
        editor.remove("filter_fastfood");
        editor.remove("filter_vegetarian");
        editor.remove("filter_dessert");
        editor.apply();
    }

    // Métodos para onboarding
    private static final String KEY_ONBOARDING = "onboarding_completed";

    public void setOnboardingCompleted(boolean completed) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_ONBOARDING, completed);
        editor.apply();
    }

    public boolean isOnboardingCompleted() {
        return preferences.getBoolean(KEY_ONBOARDING, false);
    }
}
