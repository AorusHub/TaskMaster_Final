package com.example.taskmaster.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.taskmaster.data.local.entity.TaskEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    LiveData<List<TaskEntity>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY priority DESC, due_date ASC")
    LiveData<List<TaskEntity>> getActiveTasks();

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY updated_at DESC")
    LiveData<List<TaskEntity>> getCompletedTasks();

    // NEW CATEGORY QUERIES
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY created_at DESC")
    LiveData<List<TaskEntity>> getTasksByCategory(String category);

    @Query("SELECT DISTINCT category FROM tasks WHERE category IS NOT NULL AND category != ''")
    LiveData<List<String>> getAllCategories();

    @Query("SELECT * FROM tasks WHERE tags LIKE '%' || :tag || '%' ORDER BY created_at DESC")
    LiveData<List<TaskEntity>> getTasksByTag(String tag);

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY created_at DESC")
    LiveData<List<TaskEntity>> getTasksByPriority(int priority);

    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startDate AND :endDate ORDER BY due_date ASC")
    LiveData<List<TaskEntity>> getTasksByDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM tasks WHERE is_recurring = 1")
    LiveData<List<TaskEntity>> getRecurringTasks();

    // Statistics queries
    @Query("SELECT COUNT(*) FROM tasks WHERE is_completed = 1 AND DATE(updated_at) = DATE('now')")
    LiveData<Integer> getTodayCompletedCount();

    @Query("SELECT COUNT(*) FROM tasks WHERE DATE(created_at) = DATE('now')")
    LiveData<Integer> getTodayCreatedCount();

    @Insert
    void insertTask(TaskEntity task);

    @Update
    void updateTask(TaskEntity task);

    @Query("DELETE FROM tasks WHERE id = :taskId")
    void deleteTask(int taskId);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    TaskEntity getTaskById(int taskId);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<TaskEntity> getTaskByIdLive(int taskId);

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY priority DESC, due_date ASC LIMIT 10")
    List<TaskEntity> getActiveTasksSync();
}

// Helper class for category statistics
