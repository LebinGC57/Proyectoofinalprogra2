package com.example.smarboyapp.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class EmailSender {
    private static final String TAG = "EmailSender";

    public interface EmailCallback {
        void onSuccess();
        void onError(String error);
    }

    public static void sendWelcomeEmail(String recipientEmail, String userName, EmailCallback callback) {
        // Simulación simple sin dependencias externas
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d(TAG, "Bienvenida simulada para: " + userName + " (" + recipientEmail + ")");
            callback.onSuccess();
        }, 1000);
    }
}
