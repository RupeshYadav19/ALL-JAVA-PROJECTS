package com.pharmacypro.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String retailerCode;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRetailerCode() { return retailerCode; }
    public void setRetailerCode(String retailerCode) { this.retailerCode = retailerCode; }
}
