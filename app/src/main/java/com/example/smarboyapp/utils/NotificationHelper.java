package com.example.smarboyapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.smarboyapp.CategorySelectionActivity;
import com.example.smarboyapp.R;

public class NotificationHelper {

    private static final String CHANNEL_ID = "smartboy_channel";
    private static final String CHANNEL_NAME = "SmartBoy Notifications";
    private static final int NOTIFICATION_ID = 1001;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notificaciones de recomendaciones diarias");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void sendDailyReminder(Context context, String title, String message) {
        Intent intent = new Intent(context, CategorySelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public static void sendHealthyEatingReminder(Context context) {
        sendDailyReminder(
                context,
                "🍎 Hora de comer saludable",
                "¡No olvides mantener una alimentación balanceada hoy!"
        );
    }

    public static void sendWorkoutReminder(Context context) {
        sendDailyReminder(
                context,
                "💪 Hora de ejercitarte",
                "¡Es momento de moverte! Revisa tus recomendaciones de gimnasio"
        );
    }

    public static void sendMotivationalMessage(Context context) {
        String[] messages = {
                "¡Sigue así! Estás haciendo un gran trabajo 🌟",
                "Tu salud es tu mayor riqueza 💚",
                "Cada día es una nueva oportunidad 🚀",
                "¡Tú puedes lograrlo! 💪",
                "Pequeños pasos, grandes resultados ⭐"
        };

        int random = (int) (Math.random() * messages.length);
        sendDailyReminder(context, "✨ Mensaje del día", messages[random]);
    }
}

