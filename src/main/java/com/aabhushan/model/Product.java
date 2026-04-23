package com.aabhushan.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private String category;
    private String mood;
    private String occasion;
    private String personality;
    private String imageUrl;
    private String description;
    private int stock;

    public Product() {}

    public Product(int id, String name, double price, String category, String mood, String occasion, String personality, String imageUrl, String description, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.mood = mood;
        this.occasion = occasion;
        this.personality = personality;
        this.imageUrl = imageUrl;
        this.description = description;
        this.stock = stock;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getOccasion() { return occasion; }
    public void setOccasion(String occasion) { this.occasion = occasion; }
    public String getPersonality() { return personality; }
    public void setPersonality(String personality) { this.personality = personality; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
