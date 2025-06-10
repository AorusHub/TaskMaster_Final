package com.example.taskmaster.model;

import java.util.Date;

public class Pomodoro {

    private int id;
    private Integer taskId;
    private Date startTime;
    private Date endTime;
    private int durationMinutes;
    private boolean isCompleted;
    private String sessionType; // "WORK", "SHORT_BREAK", "LONG_BREAK"

    // Constructors
    public Pomodoro() {
        this.startTime = new Date();
    }

    public Pomodoro(Integer taskId, int durationMinutes, String sessionType) {
        this.taskId = taskId;
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
        this.startTime = new Date();
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    // Helper methods
    public void complete() {
        this.endTime = new Date();
        this.isCompleted = true;
    }
}