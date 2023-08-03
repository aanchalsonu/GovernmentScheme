package com.example.governmentscheme;

public class User {
    private String role; // "admin" or "user"

    public User() {
        // Required empty constructor for Firebase Realtime Database
    }

    public User(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
