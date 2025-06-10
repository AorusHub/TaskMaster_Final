package com.example.taskmaster.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class User {
    @DocumentId
    private String id;
    private String name;
    private String email;
    private Date createdAt;
    private Date lastLogin;
    private String profilePhotoUrl;
    private String fcmToken; // Firebase Cloud Messaging untuk notifikasi

    // Empty constructor for Firebase
    public User() {
        this.createdAt = new Date();
        this.lastLogin = new Date();
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = new Date();
        this.lastLogin = new Date();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateLastLogin() {
        this.lastLogin = new Date();
    }
}