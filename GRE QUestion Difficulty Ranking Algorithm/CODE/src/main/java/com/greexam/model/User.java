package com.greexam.model;

import java.sql.Timestamp;

/**
 * Represents a user (Teacher or Student) in the system.
 */
public class User {
    private int id;
    private String name;
    private String username;
    private String passwordHash;
    private String role; // "teacher" or "student"
    private String email;
    private String secretQuestion;
    private String secretAnswer;
    private Timestamp createdAt;

    public User() {}

    public User(String name, String username, String passwordHash, String role, String email) {
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSecretQuestion() { return secretQuestion; }
    public void setSecretQuestion(String secretQuestion) { this.secretQuestion = secretQuestion; }

    public String getSecretAnswer() { return secretAnswer; }
    public void setSecretAnswer(String secretAnswer) { this.secretAnswer = secretAnswer; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isTeacher() { return "teacher".equalsIgnoreCase(role); }
    public boolean isStudent() { return "student".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return name + " (" + username + ")";
    }
}
