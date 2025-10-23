package com.example.smarboyapp.model;

public class Review {
    private String itemId;
    private String itemName;
    private String userName;
    private float rating;
    private String comment;
    private long timestamp;
    private int helpfulCount;

    public Review(String itemId, String itemName, String userName, float rating, String comment, long timestamp) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.helpfulCount = 0;
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public long getTimestamp() { return timestamp; }
    public int getHelpfulCount() { return helpfulCount; }

    // Setters
    public void setHelpfulCount(int count) { this.helpfulCount = count; }
}

