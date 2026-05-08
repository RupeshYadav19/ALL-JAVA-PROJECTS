package models;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private String city;
    private String role;
    private String profilePicPath;
    private Timestamp createdAt;
    private boolean isActive;

    public User() {}

    public User(int userId, String fullName, String email, String phone, String city, String role, boolean isActive) {
        this.userId = userId; this.fullName = fullName; this.email = email;
        this.phone = phone; this.city = city; this.role = role; this.isActive = isActive;
    }

    // Getters & Setters
    public int getUserId()              { return userId; }
    public void setUserId(int v)        { this.userId = v; }
    public String getFullName()         { return fullName; }
    public void setFullName(String v)   { this.fullName = v; }
    public String getEmail()            { return email; }
    public void setEmail(String v)      { this.email = v; }
    public String getPasswordHash()     { return passwordHash; }
    public void setPasswordHash(String v){ this.passwordHash = v; }
    public String getPhone()            { return phone; }
    public void setPhone(String v)      { this.phone = v; }
    public String getCity()             { return city; }
    public void setCity(String v)       { this.city = v; }
    public String getRole()             { return role; }
    public void setRole(String v)       { this.role = v; }
    public String getProfilePicPath()   { return profilePicPath; }
    public void setProfilePicPath(String v){ this.profilePicPath = v; }
    public Timestamp getCreatedAt()     { return createdAt; }
    public void setCreatedAt(Timestamp v){ this.createdAt = v; }
    public boolean isActive()           { return isActive; }
    public void setActive(boolean v)    { this.isActive = v; }
    public String toString()            { return fullName + " <" + email + ">"; }
}
