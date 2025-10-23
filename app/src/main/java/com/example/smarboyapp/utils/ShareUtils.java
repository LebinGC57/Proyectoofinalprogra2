package com.example.smarboyapp.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ShareUtils {

    public static void shareFood(Context context, String foodName, String price, double rating, String description) {
        String shareText = "🍽️ *" + foodName + "*\n\n" +
                "💰 Precio: " + price + "\n" +
                "⭐ Calificación: " + rating + "/5.0\n\n" +
                "📝 " + description + "\n\n" +
                "¡Descubierto con SmartBoy App! 🚀";

        shareContent(context, shareText, "Compartir Recomendación");
    }

    public static void shareGym(Context context, String gymName, String price, double rating, String type, String description) {
        String shareText = "💪 *" + gymName + "*\n\n" +
                "💰 Precio: " + price + "\n" +
                "⭐ Calificación: " + rating + "/5.0\n" +
                "🏷️ Tipo: " + type + "\n\n" +
                "📝 " + description + "\n\n" +
                "¡Descubierto con SmartBoy App! 🚀";

        shareContent(context, shareText, "Compartir Gimnasio");
    }

    public static void shareProgress(Context context, int totalRecommendations, float avgRating, String favoriteCategory) {
        String shareText = "📊 *Mi Progreso en SmartBoy*\n\n" +
                "✅ Recomendaciones exploradas: " + totalRecommendations + "\n" +
                "⭐ Calificación promedio: " + String.format("%.1f", avgRating) + "/5.0\n" +
                "❤️ Categoría favorita: " + favoriteCategory + "\n\n" +
                "¡Descubre recomendaciones personalizadas con SmartBoy App! 🚀";

        shareContent(context, shareText, "Compartir Progreso");
    }

    private static void shareContent(Context context, String text, String title) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SmartBoy App");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);

            Intent chooser = Intent.createChooser(shareIntent, title);
            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        } catch (Exception e) {
            Toast.makeText(context, "Error al compartir", Toast.LENGTH_SHORT).show();
        }
    }
}

