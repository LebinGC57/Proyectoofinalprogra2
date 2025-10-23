package com.example.smarboyapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RecommendationHistoryManager {
    private static final String PREF_NAME = "RecommendationHistory";
    private static final String KEY_HISTORY = "history";
    private static final int MAX_HISTORY_SIZE = 50;

    private SharedPreferences preferences;

    public RecommendationHistoryManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addToHistory(String itemName, String itemType, float userRating, long timestamp) {
        try {
            JSONArray history = getHistoryArray();

            JSONObject item = new JSONObject();
            item.put("name", itemName);
            item.put("type", itemType);
            item.put("rating", userRating);
            item.put("timestamp", timestamp);

            history.put(item);

            // Mantener solo los últimos MAX_HISTORY_SIZE items
            if (history.length() > MAX_HISTORY_SIZE) {
                JSONArray newHistory = new JSONArray();
                for (int i = history.length() - MAX_HISTORY_SIZE; i < history.length(); i++) {
                    newHistory.put(history.get(i));
                }
                history = newHistory;
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_HISTORY, history.toString());
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<HistoryItem> getHistory() {
        List<HistoryItem> historyList = new ArrayList<>();
        try {
            JSONArray history = getHistoryArray();
            for (int i = history.length() - 1; i >= 0; i--) {
                JSONObject item = history.getJSONObject(i);
                historyList.add(new HistoryItem(
                    item.getString("name"),
                    item.getString("type"),
                    (float) item.getDouble("rating"),
                    item.getLong("timestamp")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return historyList;
    }

    public float getAverageRating(String itemType) {
        float total = 0;
        int count = 0;

        try {
            JSONArray history = getHistoryArray();
            for (int i = 0; i < history.length(); i++) {
                JSONObject item = history.getJSONObject(i);
                if (item.getString("type").equals(itemType)) {
                    total += (float) item.getDouble("rating");
                    count++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return count > 0 ? total / count : 0;
    }

    public int getHistoryCount() {
        return getHistoryArray().length();
    }

    public void clearHistory() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_HISTORY);
        editor.apply();
    }

    private JSONArray getHistoryArray() {
        String historyString = preferences.getString(KEY_HISTORY, "[]");
        try {
            return new JSONArray(historyString);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static class HistoryItem {
        public String name;
        public String type;
        public float rating;
        public long timestamp;

        public HistoryItem(String name, String type, float rating, long timestamp) {
            this.name = name;
            this.type = type;
            this.rating = rating;
            this.timestamp = timestamp;
        }
    }
}

