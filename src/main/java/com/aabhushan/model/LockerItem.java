package com.aabhushan.model;

import java.sql.Date;

public class LockerItem {
    private int id;
    private int userId;
    private int productId;
    private String eventName;
    private Date eventDate;
    private Product product;

    public LockerItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
