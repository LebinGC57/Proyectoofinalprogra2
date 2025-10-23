package com.example.smarboyapp.model;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private long timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getMessage() { return message; }
    public boolean isUser() { return isUser; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setMessage(String message) { this.message = message; }
    public void setUser(boolean user) { isUser = user; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
