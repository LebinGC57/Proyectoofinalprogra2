package com.example.smarboyapp.model;

import java.util.List;

public class USDAResponse {
    private String totalHits;
    private int currentPage;
    private int totalPages;
    private List<Food> foods;

    // Constructor vacío
    public USDAResponse() {}

    // Getters y setters
    public String getTotalHits() { return totalHits; }
    public void setTotalHits(String totalHits) { this.totalHits = totalHits; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<Food> getFoods() { return foods; }
    public void setFoods(List<Food> foods) { this.foods = foods; }

    // Clase interna para representar cada alimento
    public static class Food {
        private int fdcId;
        private String description;
        private String dataType;
        private String gtinUpc;
        private String publishedDate;
        private String brandOwner;
        private List<FoodNutrient> foodNutrients;

        // Constructor vacío
        public Food() {}

        // Getters y setters
        public int getFdcId() { return fdcId; }
        public void setFdcId(int fdcId) { this.fdcId = fdcId; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }

        public String getGtinUpc() { return gtinUpc; }
        public void setGtinUpc(String gtinUpc) { this.gtinUpc = gtinUpc; }

        public String getPublishedDate() { return publishedDate; }
        public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }

        public String getBrandOwner() { return brandOwner; }
        public void setBrandOwner(String brandOwner) { this.brandOwner = brandOwner; }

        public List<FoodNutrient> getFoodNutrients() { return foodNutrients; }
        public void setFoodNutrients(List<FoodNutrient> foodNutrients) { this.foodNutrients = foodNutrients; }
    }

    // Clase interna para representar los nutrientes
    public static class FoodNutrient {
        private int nutrientId;
        private String nutrientName;
        private String nutrientNumber;
        private String unitName;
        private double value;

        // Constructor vacío
        public FoodNutrient() {}

        // Getters y setters
        public int getNutrientId() { return nutrientId; }
        public void setNutrientId(int nutrientId) { this.nutrientId = nutrientId; }

        public String getNutrientName() { return nutrientName; }
        public void setNutrientName(String nutrientName) { this.nutrientName = nutrientName; }

        public String getNutrientNumber() { return nutrientNumber; }
        public void setNutrientNumber(String nutrientNumber) { this.nutrientNumber = nutrientNumber; }

        public String getUnitName() { return unitName; }
        public void setUnitName(String unitName) { this.unitName = unitName; }

        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }
}
