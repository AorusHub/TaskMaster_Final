package com.example.taskmaster.model;

import java.util.Date;

public class RecentActivity {
    private int id;
    private String userId;
    private String activityType;
    private String title;
    private String description;
    private String icon;
    private int experienceGained;
    private Date timestamp;

    public RecentActivity() {}

    public RecentActivity(String userId, String activityType, String title,
                          String description, String icon, int experienceGained) {
        this.userId = userId;
        this.activityType = activityType;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.experienceGained = experienceGained;
        this.timestamp = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public int getExperienceGained() { return experienceGained; }
    public void setExperienceGained(int experienceGained) { this.experienceGained = experienceGained; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    // Helper methods
    public String getFormattedTime() {
        if (timestamp == null) return "";

        long diff = System.currentTimeMillis() - timestamp.getTime();
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) return "Just now";
        else if (minutes < 60) return minutes + "m ago";
        else if (hours < 24) return hours + "h ago";
        else if (days < 7) return days + "d ago";
        else return "1w+ ago";
    }

    public String getActivityTypeDisplayName() {
        switch (activityType) {
            case "TASK_CREATED": return "Task Created";
            case "TASK_COMPLETED": return "Task Completed";
            case "ACHIEVEMENT_UNLOCKED": return "Achievement Unlocked";
            case "LEVEL_UP": return "Level Up";
            case "STREAK_MILESTONE": return "Streak Milestone";
            default: return "Activity";
        }
    }
}