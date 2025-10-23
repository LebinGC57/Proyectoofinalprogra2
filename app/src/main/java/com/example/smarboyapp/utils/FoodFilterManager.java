package com.example.smarboyapp.utils;

import com.example.smarboyapp.FoodItem;
import java.util.ArrayList;
import java.util.List;

public class FoodFilterManager {

    public enum PriceRange {
        ECONOMICO(0, 30),      // Q 0-30
        MODERADO(31, 60),      // Q 31-60
        PREMIUM(61, 100);      // Q 61+

        private final int min;
        private final int max;

        PriceRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public boolean isInRange(String price) {
            try {
                int priceValue = Integer.parseInt(price.replaceAll("[^0-9]", ""));
                return priceValue >= min && (max == 100 ? true : priceValue <= max);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public enum DistanceRange {
        CERCA(0, 5),           // 0-5 minutos
        MODERADO(6, 10),       // 6-10 minutos
        LEJOS(11, 20);         // 11+ minutos

        private final int min;
        private final int max;

        DistanceRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public boolean isInRange(String distance) {
            try {
                String[] parts = distance.split(" ");
                for (String part : parts) {
                    if (part.matches("\\d+")) {
                        int minutes = Integer.parseInt(part);
                        return minutes >= min && (max == 20 ? true : minutes <= max);
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }
    }

    public static List<FoodItem> filterByPrice(List<FoodItem> foodList, PriceRange priceRange) {
        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : foodList) {
            if (priceRange.isInRange(item.getPrice())) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    public static List<FoodItem> filterByRating(List<FoodItem> foodList, double minRating) {
        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : foodList) {
            if (item.getRating() >= minRating) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    public static List<FoodItem> filterByDistance(List<FoodItem> foodList, DistanceRange distanceRange) {
        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : foodList) {
            if (distanceRange.isInRange(item.getDistance())) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    public static List<FoodItem> filterByKeyword(List<FoodItem> foodList, String keyword) {
        List<FoodItem> filtered = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (FoodItem item : foodList) {
            if (item.getName().toLowerCase().contains(lowerKeyword)) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    public static List<FoodItem> sortByPrice(List<FoodItem> foodList, boolean ascending) {
        List<FoodItem> sorted = new ArrayList<>(foodList);
        sorted.sort((item1, item2) -> {
            int price1 = extractPrice(item1.getPrice());
            int price2 = extractPrice(item2.getPrice());
            return ascending ? Integer.compare(price1, price2) : Integer.compare(price2, price1);
        });
        return sorted;
    }

    public static List<FoodItem> sortByRating(List<FoodItem> foodList, boolean ascending) {
        List<FoodItem> sorted = new ArrayList<>(foodList);
        sorted.sort((item1, item2) -> {
            return ascending ? Double.compare(item1.getRating(), item2.getRating()) :
                             Double.compare(item2.getRating(), item1.getRating());
        });
        return sorted;
    }

    private static int extractPrice(String price) {
        try {
            return Integer.parseInt(price.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
