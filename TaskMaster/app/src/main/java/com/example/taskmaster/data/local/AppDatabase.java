package com.example.taskmaster.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.taskmaster.data.local.converter.DateConverter;
import com.example.taskmaster.data.local.dao.CategoryDao;
import com.example.taskmaster.data.local.dao.PomodoroDao;
import com.example.taskmaster.data.local.dao.TaskDao;
import com.example.taskmaster.data.local.entity.CategoryEntity;
import com.example.taskmaster.data.local.entity.PomodoroEntity;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.model.Task;

/**
 * Room database implementation for the app's local storage.
 * This is the main entry point for accessing the database.
 */
@Database(
        entities = {
                TaskEntity.class,
                CategoryEntity.class,
                PomodoroEntity.class
        },
        version = 2
)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "taskmaster.db";
    private static volatile AppDatabase instance;

    // Abstract methods that return DAOs
    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();
    public abstract PomodoroDao pomodoroDao();

    // Singleton pattern
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}