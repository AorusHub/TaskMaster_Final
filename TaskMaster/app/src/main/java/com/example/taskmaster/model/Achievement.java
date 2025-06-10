package com.example.taskmaster.model;

import java.util.Date;

public class Achievement {

    private int id;
    private String title;
    private String description;
    private String iconResource;
    private boolean isCompleted;
    private int progress;
    private int targetValue;
    private String achievementType;
    private Date unlockDate;

    // Constructors
    public Achievement() {
    }

    public Achievement(String title, String description, int targetValue, String achievementType) {
        this.title = title;
        this.description = description;
        this.targetValue = targetValue;
        this.achievementType = achievementType;
        this.progress = 0;
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconResource() {
        return iconResource;
    }

    public void setIconResource(String iconResource) {
        this.iconResource = iconResource;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Add compatibility methods for SharedViewModel
    public boolean isUnlocked() {
        return isCompleted;
    }

    public void setUnlocked(boolean unlocked) {
        this.isCompleted = unlocked;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        checkCompletion();
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public String getAchievementType() {
        return achievementType;
    }

    public void setAchievementType(String achievementType) {
        this.achievementType = achievementType;
    }

    public Date getUnlockDate() {
        return unlockDate;
    }

    public void setUnlockDate(Date unlockDate) {
        this.unlockDate = unlockDate;
    }

    // Helper methods
    private void checkCompletion() {
        if (progress >= targetValue && !isCompleted) {
            isCompleted = true;
            unlockDate = new Date();
        }
    }

    public void incrementProgress() {
        setProgress(progress + 1);
    }

    public void incrementProgress(int amount) {
        setProgress(progress + amount);
    }
}