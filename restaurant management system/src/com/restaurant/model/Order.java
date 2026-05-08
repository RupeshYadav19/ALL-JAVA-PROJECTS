package com.restaurant.model;

public class Order {
    private int id;
    private int userId;
    private String items; // Simplified as comma-separated or JSON
    private double totalPrice;
    private String status; // "PENDING", "COMPLETED", "CANCELLED"

    public Order(int id, int userId, String items, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }
}
