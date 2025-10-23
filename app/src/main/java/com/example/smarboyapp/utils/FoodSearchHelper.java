package com.example.smarboyapp.utils;

public class FoodSearchHelper {

    public static String[] getHealthyFoodQueries() {
        return new String[]{
            "healthy foods", "nutritious meals", "fresh vegetables",
            "lean protein", "whole grains", "fruits", "organic food"
        };
    }

    public static String[] getProteinQueries() {
        return new String[]{
            "chicken breast", "fish", "eggs", "beans", "tofu", "protein powder"
        };
    }

    public static String[] getVegetarianQueries() {
        return new String[]{
            "vegetarian meals", "plant based", "vegetables", "legumes", "quinoa"
        };
    }

    public static String[] getLowCalorieQueries() {
        return new String[]{
            "low calorie foods", "diet foods", "light meals", "steamed vegetables"
        };
    }
}
