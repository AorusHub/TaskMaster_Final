package com.example.taskmaster.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "user_progress")
public class UserProgressEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "level")
    private int level;

    @ColumnInfo(name = "experience_points")
    private int experiencePoints;

    @ColumnInfo(name = "total_tasks_created")
    private int totalTasksCreated;

    @ColumnInfo(name = "total_tasks_completed")
    private int totalTasksCompleted;

    @ColumnInfo(name = "current_streak")
    private int currentStreak;

    @ColumnInfo(name = "longest_streak")
    private int longestStreak;

    @ColumnInfo(name = "last_activity_date")
    private Date lastActivityDate;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    // Default Constructor
    public UserProgressEntity() {}

    @Ignore
    public UserProgressEntity(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.level = 1;
        this.experiencePoints = 0;
        this.totalTasksCreated = 0;
        this.totalTasksCompleted = 0;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.createdAt = new Date();
        this.lastActivityDate = new Date();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(int experiencePoints) { this.experiencePoints = experiencePoints; }

    public int getTotalTasksCreated() { return totalTasksCreated; }
    public void setTotalTasksCreated(int totalTasksCreated) { this.totalTasksCreated = totalTasksCreated; }

    public int getTotalTasksCompleted() { return totalTasksCompleted; }
    public void setTotalTasksCompleted(int totalTasksCompleted) { this.totalTasksCompleted = totalTasksCompleted; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public Date getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(Date lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public double getSuccessRate() {
        if (totalTasksCreated == 0) return 0.0;
        return (totalTasksCompleted * 100.0) / totalTasksCreated;
    }

    public int getExpForNextLevel() {
        return level * 100; // Each level requires level * 100 exp
    }

    public int getCurrentLevelExp() {
        int prevLevelExp = (level - 1) * 100;
        return experiencePoints - prevLevelExp;
    }

    public int getExpNeededForNextLevel() {
        return getExpForNextLevel() - getCurrentLevelExp();
    }

    public boolean canLevelUp() {
        return getCurrentLevelExp() >= getExpForNextLevel();
    }
}