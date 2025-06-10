package com.example.taskmaster.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.taskmaster.model.Task;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskmaster.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tasks table
        db.execSQL(DatabaseContract.TaskEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.CategoryEntry.CREATE_TABLE);
        db.execSQL(DatabaseContract.PomodoroEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.PomodoroEntry.TABLE_NAME);
        onCreate(db);
    }

    // Helper methods for Task operations
    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(DatabaseContract.TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(DatabaseContract.TaskEntry.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        long id = db.insert(DatabaseContract.TaskEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Other CRUD operations can be added as needed
}