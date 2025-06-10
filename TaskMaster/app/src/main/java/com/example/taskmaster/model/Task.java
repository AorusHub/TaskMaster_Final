package com.example.taskmaster.model;

public class Task {
    private long id;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private boolean isCompleted;

    // NEW FIELDS
    private String category;
    private String tags;
    private boolean isRecurring;
    private String recurrencePattern;
    private int estimatedTimeMinutes;
    private int actualTimeMinutes;
    private String colorTag;

    // Constructors
    public Task() {
        // Initialize with default values
        this.category = "General";
        this.tags = "";
        this.isRecurring = false;
        this.recurrencePattern = "";
        this.estimatedTimeMinutes = 0;
        this.actualTimeMinutes = 0;
        this.colorTag = "";
        this.isCompleted = false;
    }

    public Task(String title, String description, String dueDate, String priority) {
        this(); // Call default constructor first
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    // FIXED: Constructor untuk compatibility dengan StatisticsViewModel
    public Task(int id, String title, String description, String category, String priority, String dueDate, boolean isCompleted) {
        this(); // Call default constructor first
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
    }

    // Alternative constructor dengan long id (untuk flexibility)
    public Task(long id, String title, String description, String category, String priority, String dueDate, boolean isCompleted) {
        this(); // Call default constructor first
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
    }

    // Full constructor dengan semua field
    public Task(long id, String title, String description, String dueDate, String priority,
                boolean isCompleted, String category, String tags, boolean isRecurring,
                String recurrencePattern, int estimatedTimeMinutes, int actualTimeMinutes, String colorTag) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.category = category != null ? category : "General";
        this.tags = tags != null ? tags : "";
        this.isRecurring = isRecurring;
        this.recurrencePattern = recurrencePattern != null ? recurrencePattern : "";
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.actualTimeMinutes = actualTimeMinutes;
        this.colorTag = colorTag != null ? colorTag : "";
    }

    // Existing getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    // NEW GETTERS AND SETTERS dengan null safety
    public String getCategory() {
        return category != null ? category : "General";
    }

    public void setCategory(String category) {
        this.category = category != null ? category : "General";
    }

    public String getTags() {
        return tags != null ? tags : "";
    }

    public void setTags(String tags) {
        this.tags = tags != null ? tags : "";
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurrencePattern() {
        return recurrencePattern != null ? recurrencePattern : "";
    }

    public void setRecurrencePattern(String recurrencePattern) {
        this.recurrencePattern = recurrencePattern != null ? recurrencePattern : "";
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(int estimatedTimeMinutes) {
        this.estimatedTimeMinutes = Math.max(0, estimatedTimeMinutes); // Ensure non-negative
    }

    public int getActualTimeMinutes() {
        return actualTimeMinutes;
    }

    public void setActualTimeMinutes(int actualTimeMinutes) {
        this.actualTimeMinutes = Math.max(0, actualTimeMinutes); // Ensure non-negative
    }

    public String getColorTag() {
        return colorTag != null ? colorTag : "";
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag != null ? colorTag : "";
    }

    // Helper Methods
    public String[] getTagArray() {
        if (tags == null || tags.trim().isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }

    public boolean hasTag(String tag) {
        if (tag == null || tags == null) return false;
        String[] tagArray = getTagArray();
        for (String t : tagArray) {
            if (t.trim().equalsIgnoreCase(tag.trim())) {
                return true;
            }
        }
        return false;
    }

    public String getFormattedTags() {
        if (tags == null || tags.trim().isEmpty()) {
            return "";
        }
        return "#" + tags.replace(",", " #");
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", priority='" + priority + '\'' +
                ", isCompleted=" + isCompleted +
                ", category='" + category + '\'' +
                ", tags='" + tags + '\'' +
                ", isRecurring=" + isRecurring +
                ", recurrencePattern='" + recurrencePattern + '\'' +
                ", estimatedTimeMinutes=" + estimatedTimeMinutes +
                ", actualTimeMinutes=" + actualTimeMinutes +
                ", colorTag='" + colorTag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}