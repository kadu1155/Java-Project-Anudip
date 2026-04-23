package com.aabhushan.model;

import java.sql.Timestamp;

public class Bargain {
    private int id;
    private int userId;
    private int productId;
    private double offeredPrice;
    private String status;
    private Timestamp createdAt;
    
    // Joint fields
    private Product product;

    public Bargain() {}

    public Bargain(int id, int userId, int productId, double offeredPrice, String status, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.offeredPrice = offeredPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public double getOfferedPrice() { return offeredPrice; }
    public void setOfferedPrice(double offeredPrice) { this.offeredPrice = offeredPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
