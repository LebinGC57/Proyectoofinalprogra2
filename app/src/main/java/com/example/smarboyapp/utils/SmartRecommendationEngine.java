package com.example.smarboyapp.utils;

import com.example.smarboyapp.database.UserPreferencesManager;

public class SmartRecommendationEngine {

    public static class UserProfile {
        public String name;
        public int age;
        public double weight;
        public double height;
        public double budget;

        public double calculateBMI() {
            double heightInMeters = height / 100.0;
            return weight / (heightInMeters * heightInMeters);
        }

        public String getBMICategory() {
            double bmi = calculateBMI();
            if (bmi < 18.5) return "underweight";
            if (bmi < 25) return "normal";
            if (bmi < 30) return "overweight";
            return "obese";
        }

        public String getAgeGroup() {
            if (age < 25) return "young";
            if (age < 40) return "adult";
            if (age < 60) return "middle_age";
            return "senior";
        }
    }

    public static String[] getRecommendedQueries(UserProfile profile) {
        String bmiCategory = profile.getBMICategory();
        String ageGroup = profile.getAgeGroup();

        // Recomendaciones basadas en BMI
        switch (bmiCategory) {
            case "underweight":
                return new String[]{"protein rich foods", "nuts", "healthy fats", "quinoa", "avocado"};
            case "normal":
                return new String[]{"balanced meals", "chicken breast", "fish", "vegetables", "whole grains"};
            case "overweight":
            case "obese":
                return new String[]{"low calorie foods", "salad", "steamed vegetables", "lean protein", "fiber rich foods"};
            default:
                return FoodSearchHelper.getHealthyFoodQueries();
        }
    }

    public static String getBudgetAppropriateQuery(UserProfile profile) {
        double budget = profile.budget;

        if (budget < 100) {
            // Comidas económicas
            return "rice beans eggs bread";
        } else if (budget < 300) {
            // Comidas moderadas
            return "chicken vegetables pasta";
        } else {
            // Sin restricciones de presupuesto
            return "salmon organic quinoa premium";
        }
    }

    public static String getPersonalizedRecommendation(UserProfile profile) {
        String[] recommendations = getRecommendedQueries(profile);
        int randomIndex = (int) (Math.random() * recommendations.length);
        return recommendations[randomIndex];
    }

    public static String generateHealthTip(UserProfile profile) {
        String bmiCategory = profile.getBMICategory();
        double bmi = profile.calculateBMI();

        String tip = "Basado en tu IMC de " + String.format("%.1f", bmi) + " (" + bmiCategory + "), ";

        switch (bmiCategory) {
            case "underweight":
                tip += "te recomendamos alimentos ricos en proteínas y grasas saludables para aumentar tu peso de forma saludable.";
                break;
            case "normal":
                tip += "mantienes un peso saludable. Continúa con una dieta balanceada y ejercicio regular.";
                break;
            case "overweight":
                tip += "considera reducir las calorías y aumentar la actividad física. Opta por alimentos bajos en grasa.";
                break;
            case "obese":
                tip += "es importante consultar con un profesional de la salud y enfocarte en alimentos bajos en calorías.";
                break;
            default:
                tip += "mantén una alimentación balanceada.";
        }

        return tip;
    }

    public static void learnFromUserChoice(
            UserPreferencesManager preferencesManager,
            String itemType,
            boolean liked
    ) {
        // Aprender de la elección del usuario para futuras recomendaciones
        preferencesManager.saveUserPreference(itemType, liked);

        // Guardar estadísticas adicionales
        if (liked) {
            preferencesManager.incrementPreferenceCount(itemType + "_liked");
        } else {
            preferencesManager.incrementPreferenceCount(itemType + "_disliked");
        }
    }
}
