package com.pharmacypro.models;

public class Doctor {
    private int id;
    private String name;
    private String mobile;
    private String email;
    private String specialization;
    private String address;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
