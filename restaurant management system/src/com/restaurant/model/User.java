package com.restaurant.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String address;
    private String role; // "USER" or "ADMIN"

    public User(int id, String username, String password, String name, String address, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getRole() { return role; }

    public void setAddress(String address) { this.address = address; }
}
