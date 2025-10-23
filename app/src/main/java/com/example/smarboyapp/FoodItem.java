package com.example.smarboyapp;

public class FoodItem {
    private String icon;
    private String name;
    private String price;
    private double rating;
    private String distance;
    private String type;
    private String description;
    private float userRating; // Nueva calificación del usuario
    private String id; // ID único para tracking

    // Constructor principal con 7 parámetros
    public FoodItem(String icon, String name, String price, double rating, String distance, String type, String description) {
        this.icon = icon;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.distance = distance;
        this.type = type;
        this.description = description;
        this.userRating = 0.0f;
        this.id = System.currentTimeMillis() + "_" + name.hashCode();
    }

    // Constructor alternativo con 5 parámetros (para compatibilidad)
    public FoodItem(String icon, String name, String price, double rating, String distance) {
        this(icon, name, price, rating, distance, "food", "Deliciosa opción recomendada para ti");
    }

    // Getters
    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public String getDistance() {
        return distance;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // Nuevos getters y setters
    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getId() {
        return id;
    }

    // Setters
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
