package com.example.taskmaster.data.local;

import android.content.Context;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;
    private final TaskDatabase taskDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        // GUNAKAN getDatabase() bukan getInstance()
        taskDatabase = TaskDatabase.getDatabase(context);
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public TaskDatabase getTaskDatabase() {
        return taskDatabase;
    }
}