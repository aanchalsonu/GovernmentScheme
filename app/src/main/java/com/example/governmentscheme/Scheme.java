package com.example.governmentscheme;

public class Scheme {
    private String schemeName;
    private String description;

    public Scheme() {
        // Required empty constructor for Firebase Realtime Database
    }

    public Scheme(String schemeName, String description) {
        this.schemeName = schemeName;
        this.description = description;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public String getDescription() {
        return description;
    }
}
