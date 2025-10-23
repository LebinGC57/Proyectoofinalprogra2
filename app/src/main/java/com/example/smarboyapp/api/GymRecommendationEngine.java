package com.example.smarboyapp.api;

import com.example.smarboyapp.model.GymItem;
import com.example.smarboyapp.utils.SmartRecommendationEngine;

import java.util.ArrayList;
import java.util.List;

public class GymRecommendationEngine {

    public static List<GymItem> getPersonalizedGymRecommendations(SmartRecommendationEngine.UserProfile userProfile) {
        List<GymItem> recommendations = new ArrayList<>();

        String bmiCategory = userProfile.getBMICategory();
        String ageGroup = userProfile.getAgeGroup();
        double budget = userProfile.budget;

        // Recomendaciones basadas en BMI
        switch (bmiCategory) {
            case "underweight":
                recommendations.addAll(getWeightGainRecommendations(budget));
                break;
            case "overweight":
            case "obese":
                recommendations.addAll(getWeightLossRecommendations(budget));
                break;
            default: // normal
                recommendations.addAll(getMaintenanceRecommendations(budget));
                break;
        }

        // Agregar recomendaciones basadas en edad
        recommendations.addAll(getAgeAppropriateRecommendations(ageGroup, budget));

        return recommendations;
    }

    private static List<GymItem> getWeightGainRecommendations(double budget) {
        List<GymItem> items = new ArrayList<>();

        items.add(new GymItem("💪", "ENTRENAMIENTO DE FUERZA", "Q 150", 4.8, "A 5 minutos caminando",
            "class", "Sesiones de pesas para ganar masa muscular"));
        items.add(new GymItem("🏋️", "GIMNASIO POWERLIFTING", "Q 300", 4.6, "A 8 minutos caminando",
            "membership", "Gimnasio especializado en levantamiento de pesas"));
        items.add(new GymItem("🥤", "BATIDOS PROTEICOS", "Q 45", 4.3, "A 3 minutos caminando",
            "supplement", "Suplementos para ganar peso saludable"));

        return filterByBudget(items, budget);
    }

    private static List<GymItem> getWeightLossRecommendations(double budget) {
        List<GymItem> items = new ArrayList<>();

        items.add(new GymItem("🏃", "CARDIO INTENSIVO", "Q 120", 4.7, "A 6 minutos caminando",
            "class", "Clases de cardio para quemar grasa"));
        items.add(new GymItem("🚴", "SPINNING", "Q 100", 4.5, "A 7 minutos caminando",
            "class", "Clases de ciclismo indoor"));
        items.add(new GymItem("🏊", "NATACIÓN", "Q 200", 4.9, "A 12 minutos caminando",
            "membership", "Acceso a piscina para ejercicio cardiovascular"));
        items.add(new GymItem("🧘", "YOGA DINÁMICO", "Q 80", 4.4, "A 5 minutos caminando",
            "class", "Yoga activo para flexibilidad y quema de calorías"));

        return filterByBudget(items, budget);
    }

    private static List<GymItem> getMaintenanceRecommendations(double budget) {
        List<GymItem> items = new ArrayList<>();

        items.add(new GymItem("🏋️", "ENTRENAMIENTO COMPLETO", "Q 180", 4.6, "A 6 minutos caminando",
            "membership", "Gimnasio con todas las facilidades"));
        items.add(new GymItem("🤸", "FITNESS FUNCIONAL", "Q 140", 4.5, "A 8 minutos caminando",
            "class", "Entrenamiento funcional para mantenimiento"));
        items.add(new GymItem("🏃", "CROSSFIT", "Q 220", 4.8, "A 10 minutos caminando",
            "class", "Entrenamiento de alta intensidad"));

        return filterByBudget(items, budget);
    }

    private static List<GymItem> getAgeAppropriateRecommendations(String ageGroup, double budget) {
        List<GymItem> items = new ArrayList<>();

        switch (ageGroup) {
            case "young":
                items.add(new GymItem("⚡", "HIIT JOVEN", "Q 130", 4.7, "A 5 minutos caminando",
                    "class", "Entrenamiento de alta intensidad para jóvenes"));
                break;
            case "adult":
                items.add(new GymItem("🏃", "RUNNING CLUB", "Q 60", 4.4, "A 4 minutos caminando",
                    "class", "Grupo de corredores adultos"));
                break;
            case "middle_age":
                items.add(new GymItem("🧘", "PILATES", "Q 90", 4.6, "A 7 minutos caminando",
                    "class", "Ejercicio de bajo impacto ideal para adultos"));
                break;
            case "senior":
                items.add(new GymItem("🚶", "CAMINATA GRUPAL", "Q 40", 4.3, "A 3 minutos caminando",
                    "class", "Ejercicio suave para adultos mayores"));
                break;
        }

        return filterByBudget(items, budget);
    }

    private static List<GymItem> filterByBudget(List<GymItem> items, double budget) {
        if (budget <= 0) return items;

        List<GymItem> filtered = new ArrayList<>();
        for (GymItem item : items) {
            int price = extractPrice(item.getPrice());
            // Mostrar solo opciones que no excedan el 10% del presupuesto mensual
            if (price <= budget * 0.1) {
                filtered.add(item);
            }
        }

        // Si no hay opciones dentro del presupuesto, mostrar las más baratas
        if (filtered.isEmpty() && !items.isEmpty()) {
            filtered.add(items.get(items.size() - 1)); // La última suele ser la más barata
        }

        return filtered.isEmpty() ? items : filtered;
    }

    private static int extractPrice(String price) {
        try {
            return Integer.parseInt(price.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static List<GymItem> getAllGymOptions() {
        List<GymItem> allOptions = new ArrayList<>();

        // Membresías de gimnasio
        allOptions.add(new GymItem("🏋️", "GIMNASIO BÁSICO", "Q 200", 4.2, "A 5 minutos caminando",
            "membership", "Acceso básico a equipos de gimnasio"));
        allOptions.add(new GymItem("💎", "GIMNASIO PREMIUM", "Q 400", 4.8, "A 8 minutos caminando",
            "membership", "Gimnasio completo con todas las facilidades"));

        // Clases grupales
        allOptions.add(new GymItem("🤸", "ZUMBA", "Q 80", 4.5, "A 6 minutos caminando",
            "class", "Baile fitness divertido y energético"));
        allOptions.add(new GymItem("🧘", "YOGA", "Q 70", 4.6, "A 4 minutos caminando",
            "class", "Relajación y flexibilidad"));
        allOptions.add(new GymItem("🚴", "SPINNING", "Q 100", 4.4, "A 7 minutos caminando",
            "class", "Ciclismo indoor de alta energía"));

        // Entrenamientos especializados
        allOptions.add(new GymItem("🥊", "BOXEO", "Q 150", 4.7, "A 10 minutos caminando",
            "class", "Entrenamiento de boxeo y defensa personal"));
        allOptions.add(new GymItem("🏃", "CROSSFIT", "Q 220", 4.9, "A 12 minutos caminando",
            "class", "Entrenamiento funcional de alta intensidad"));

        return allOptions;
    }
}
