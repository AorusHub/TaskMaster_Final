package com.example.taskmaster.model;

import java.util.Date;

public class UserStats {

    private int completedTasks;
    private int totalTasks;
    private int pomodoroSessions;
    private int pomodoroMinutes;
    private int achievementCount;
    private int taskCompletionStreak;
    private Date lastTaskCompletionDate;

    // Constructors
    public UserStats() {
        this.completedTasks = 0;
        this.totalTasks = 0;
        this.pomodoroSessions = 0;
        this.pomodoroMinutes = 0;
        this.achievementCount = 0;
        this.taskCompletionStreak = 0;
    }

    // Getters and Setters
    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getPomodoroSessions() {
        return pomodoroSessions;
    }

    public void setPomodoroSessions(int pomodoroSessions) {
        this.pomodoroSessions = pomodoroSessions;
    }

    public int getPomodoroMinutes() {
        return pomodoroMinutes;
    }

    public void setPomodoroMinutes(int pomodoroMinutes) {
        this.pomodoroMinutes = pomodoroMinutes;
    }

    public int getAchievementCount() {
        return achievementCount;
    }

    public void setAchievementCount(int achievementCount) {
        this.achievementCount = achievementCount;
    }

    public int getTaskCompletionStreak() {
        return taskCompletionStreak;
    }

    public void setTaskCompletionStreak(int taskCompletionStreak) {
        this.taskCompletionStreak = taskCompletionStreak;
    }

    public Date getLastTaskCompletionDate() {
        return lastTaskCompletionDate;
    }

    public void setLastTaskCompletionDate(Date lastTaskCompletionDate) {
        this.lastTaskCompletionDate = lastTaskCompletionDate;
    }

    // Helper methods
    public float getTaskCompletionRate() {
        if (totalTasks == 0) {
            return 0;
        }
        return (float) completedTasks / totalTasks;
    }

    public void incrementCompletedTasks() {
        completedTasks++;
        updateLastCompletionDate();
    }

    public void incrementTotalTasks() {
        totalTasks++;
    }

    public void addPomodoroSession(int minutes) {
        pomodoroSessions++;
        pomodoroMinutes += minutes;
    }

    public void incrementAchievements() {
        achievementCount++;
    }

    private void updateLastCompletionDate() {
        Date today = new Date();
        lastTaskCompletionDate = today;
    }
}