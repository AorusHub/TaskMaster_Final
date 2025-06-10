package com.example.taskmaster.model;

import com.example.taskmaster.data.local.entity.TaskEntity;
import java.util.Date;

public class FirebaseTask {
    private String id;
    private String firebaseId; // ADD THIS
    private String userId; // ADD THIS
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private String category;
    private boolean isCompleted;
    private String tags;
    private long createdAt;
    private long updatedAt;

    public FirebaseTask() {
        // Required empty constructor for Firebase
    }

    // Convert from TaskEntity
    public static FirebaseTask fromEntity(TaskEntity entity) {
        FirebaseTask task = new FirebaseTask();
        task.setId(String.valueOf(entity.getId()));
        task.setTitle(entity.getTitle());
        task.setDescription(entity.getDescription());
        task.setPriority(entity.getPriorityString());
        task.setCompleted(entity.isCompleted());
        task.setTags(entity.getTags());
        task.setCategory(entity.getCategory());

        if (entity.getDueDate() != null) {
            task.setDueDate(entity.getFormattedDueDate());
        }

        if (entity.getCreatedAt() != null) {
            task.setCreatedAt(entity.getCreatedAt().getTime());
        }

        if (entity.getUpdatedAt() != null) {
            task.setUpdatedAt(entity.getUpdatedAt().getTime());
        }

        return task;
    }

    // Convert to TaskEntity
    public TaskEntity toEntity() {
        TaskEntity entity = new TaskEntity();
        if (id != null) {
            try {
                entity.setId(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                entity.setId(0);
            }
        }

        entity.setTitle(title);
        entity.setDescription(description);
        entity.setCompleted(isCompleted);
        entity.setTags(tags);
        entity.setCategory(category);

        // Convert priority string to int
        if (priority != null) {
            switch (priority.toLowerCase()) {
                case "low": entity.setPriority(1); break;
                case "medium": entity.setPriority(2); break;
                case "high": entity.setPriority(3); break;
                default: entity.setPriority(2); break;
            }
        }

        if (createdAt > 0) {
            entity.setCreatedAt(new Date(createdAt));
        }

        if (updatedAt > 0) {
            entity.setUpdatedAt(new Date(updatedAt));
        }

        return entity;
    }

    // ALL GETTERS AND SETTERS
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // ADD MISSING GETTERS/SETTERS
    public String getFirebaseId() { return firebaseId; }
    public void setFirebaseId(String firebaseId) { this.firebaseId = firebaseId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}