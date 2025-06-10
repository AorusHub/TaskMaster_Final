package com.example.taskmaster.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "tasks")
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "due_date")
    private Date dueDate;

    @ColumnInfo(name = "priority")
    private int priority; // 1=Low, 2=Medium, 3=High

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // NEW FIELDS for enhanced features
    @ColumnInfo(name = "category")
    private String category; // Work, Personal, Study, Health, etc.

    @ColumnInfo(name = "tags")
    private String tags; // Comma-separated tags

    @ColumnInfo(name = "is_recurring")
    private boolean isRecurring;

    @ColumnInfo(name = "recurrence_pattern")
    private String recurrencePattern; // daily, weekly, monthly

    @ColumnInfo(name = "estimated_time")
    private int estimatedTimeMinutes;

    @ColumnInfo(name = "actual_time")
    private int actualTimeMinutes;

    @ColumnInfo(name = "color_tag")
    private String colorTag; // Hex color for visual identification

    // Default Constructor
    public TaskEntity() {}

    // Constructor with basic fields
    public TaskEntity(String title, String description, Date dueDate, int priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isCompleted = false;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.category = "General";
        this.isRecurring = false;
        this.estimatedTimeMinutes = 0;
        this.actualTimeMinutes = 0;
    }

    // EXISTING GETTERS AND SETTERS
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // NEW GETTERS AND SETTERS - YANG KURANG
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(int estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public int getActualTimeMinutes() {
        return actualTimeMinutes;
    }

    public void setActualTimeMinutes(int actualTimeMinutes) {
        this.actualTimeMinutes = actualTimeMinutes;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }

    // Helper Methods
    public boolean isOverdue() {
        if (dueDate == null || isCompleted) return false;
        return dueDate.before(new Date());
    }

    public String getFormattedDueDate() {
        if (dueDate == null) return "No due date";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        return sdf.format(dueDate);
    }

    public String getPriorityString() {
        switch (priority) {
            case 1: return "Low";
            case 2: return "Medium";
            case 3: return "High";
            default: return "Medium";
        }
    }

    public java.util.List<String> getTagList() {
        if (tags == null || tags.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        String[] tagArray = tags.split(",");
        java.util.List<String> tagList = new java.util.ArrayList<>();
        for (String tag : tagArray) {
            tagList.add(tag.trim());
        }
        return tagList;
    }

    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return;

        if (tags == null || tags.trim().isEmpty()) {
            tags = tag.trim();
        } else {
            java.util.List<String> currentTags = getTagList();
            if (!currentTags.contains(tag.trim())) {
                tags = tags + "," + tag.trim();
            }
        }
    }

    public void removeTag(String tag) {
        if (tags == null || tag == null) return;

        java.util.List<String> currentTags = getTagList();
        currentTags.remove(tag.trim());

        if (currentTags.isEmpty()) {
            tags = null;
        } else {
            tags = String.join(",", currentTags);
        }
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", isCompleted=" + isCompleted +
                ", category='" + category + '\'' +
                ", tags='" + tags + '\'' +
                ", isRecurring=" + isRecurring +
                ", recurrencePattern='" + recurrencePattern + '\'' +
                ", estimatedTimeMinutes=" + estimatedTimeMinutes +
                ", actualTimeMinutes=" + actualTimeMinutes +
                ", colorTag='" + colorTag + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}