package com.example.taskmaster.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class Category {

    private int id;
    private String name;
    private String color;
    private String icon;
    private boolean isDefault;

    // Constructors
    public Category() {
    }

    public Category(String name, String color, String icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.isDefault = false;
    }

    public Category(int id, String name, String color, String icon, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}