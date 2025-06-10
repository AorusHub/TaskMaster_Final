package com.example.taskmaster.data.local;

import android.provider.BaseColumns;

public final class DatabaseContract {

    // Private constructor to prevent instantiation
    private DatabaseContract() {}

    // Task table definition
    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COMPLETED = "completed";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_PRIORITY = "priority";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_COMPLETED + " INTEGER, " +
                        COLUMN_CATEGORY_ID + " INTEGER, " +
                        COLUMN_DUE_DATE + " TEXT, " +
                        COLUMN_PRIORITY + " INTEGER, " +
                        "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " +
                        CategoryEntry.TABLE_NAME + "(" + _ID + "))";
    }

    // Category table definition
    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COLOR = "color";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT UNIQUE, " +
                        COLUMN_COLOR + " TEXT)";
    }

    // Pomodoro table definition
    public static class PomodoroEntry implements BaseColumns {
        public static final String TABLE_NAME = "pomodoros";
        public static final String COLUMN_TASK_ID = "task_id";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_COMPLETED = "completed";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TASK_ID + " INTEGER, " +
                        COLUMN_START_TIME + " TEXT, " +
                        COLUMN_END_TIME + " TEXT, " +
                        COLUMN_DURATION + " INTEGER, " +
                        COLUMN_COMPLETED + " INTEGER, " +
                        "FOREIGN KEY (" + COLUMN_TASK_ID + ") REFERENCES " +
                        TaskEntry.TABLE_NAME + "(" + _ID + "))";
    }
}