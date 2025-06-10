package com.example.taskmaster.util;

/**
 * Constants used throughout the TaskMaster application.
 */
public final class Constants {
    // Prevent instantiation
    private Constants() {}

    // Shared Preferences
    public static final String PREFS_NAME = "taskmaster_preferences";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_THEME = "app_theme";
    public static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";

    // Intent extras
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";

    // Notification channels
    public static final String CHANNEL_TASKS = "task_reminders";
    public static final String CHANNEL_POMODORO = "pomodoro_alerts";

    // Task priorities
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_HIGH = 3;

    // Pomodoro session types
    public static final String SESSION_WORK = "WORK";
    public static final String SESSION_SHORT_BREAK = "SHORT_BREAK";
    public static final String SESSION_LONG_BREAK = "LONG_BREAK";

    // Request codes
    public static final int REQUEST_TASK_DETAIL = 1001;
    public static final int REQUEST_PERMISSIONS = 1002;

    // Achievement types
    public static final String ACHIEVEMENT_TASK_COMPLETION = "TASK_COMPLETION";
    public static final String ACHIEVEMENT_POMODORO = "POMODORO";
    public static final String ACHIEVEMENT_CATEGORY = "CATEGORY";


}