package com.example.taskmaster.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.Date;

@Entity(
        tableName = "pomodoros",
        foreignKeys = @ForeignKey(
                entity = TaskEntity.class,
                parentColumns = "id",
                childColumns = "task_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("task_id")}
)
public class PomodoroEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "task_id")
    private Integer taskId;

    @ColumnInfo(name = "start_time")
    private Date startTime;

    @ColumnInfo(name = "end_time")
    private Date endTime;

    @ColumnInfo(name = "duration")
    private int durationMinutes;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "session_type")
    private String sessionType; // "WORK", "SHORT_BREAK", "LONG_BREAK"

    @Query("SELECT SUM(duration) FROM pomodoros WHERE task_id = :taskId")
    long getTotalPomodoroTimeByTaskId(int taskId) {
        return 0;
    }

    // Constructors
    public PomodoroEntity() {
        this.startTime = new Date();
    }

    public PomodoroEntity(Integer taskId, int durationMinutes, String sessionType) {
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

    /**
     * Completes this Pomodoro session by setting the end time and completed status
     */
    public void complete() {
        this.endTime = new Date();
        this.isCompleted = true;
    }
}