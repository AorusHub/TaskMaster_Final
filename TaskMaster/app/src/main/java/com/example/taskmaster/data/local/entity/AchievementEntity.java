package com.example.taskmaster.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "achievements")
public class AchievementEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private String userId; // For per-account data

    @ColumnInfo(name = "achievement_type")
    private String achievementType;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "icon")
    private String icon;

    @ColumnInfo(name = "is_unlocked")
    private boolean isUnlocked;

    @ColumnInfo(name = "unlock_date")
    private Date unlockDate;

    @ColumnInfo(name = "requirement_value")
    private int requirementValue;

    @ColumnInfo(name = "current_progress")
    private int currentProgress;

    @ColumnInfo(name = "experience_points")
    private int experiencePoints;

    // Default Constructor
    public AchievementEntity() {}

    @Ignore
    public AchievementEntity(String userId, String achievementType, String title, String description,
                             String icon, int requirementValue, int experiencePoints) {
        this.userId = userId;
        this.achievementType = achievementType;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.requirementValue = requirementValue;
        this.experiencePoints = experiencePoints;
        this.isUnlocked = false;
        this.currentProgress = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAchievementType() { return achievementType; }
    public void setAchievementType(String achievementType) { this.achievementType = achievementType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }

    public Date getUnlockDate() { return unlockDate; }
    public void setUnlockDate(Date unlockDate) { this.unlockDate = unlockDate; }

    public int getRequirementValue() { return requirementValue; }
    public void setRequirementValue(int requirementValue) { this.requirementValue = requirementValue; }

    public int getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int currentProgress) { this.currentProgress = currentProgress; }

    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }

    // Helper methods
    public int getProgressPercentage() {
        if (requirementValue == 0) return 0;
        return Math.min(100, (currentProgress * 100) / requirementValue);
    }

    public boolean isCompleted() {
        return currentProgress >= requirementValue;
    }
}