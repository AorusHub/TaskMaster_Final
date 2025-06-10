package com.example.taskmaster.data.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.taskmaster.data.local.dao.TaskDao;
import com.example.taskmaster.data.local.TaskDatabase;
import com.example.taskmaster.data.local.entity.TaskEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private static final String TAG = "TaskRepository";
    private TaskDao taskDao;
    private LiveData<List<TaskEntity>> allTasks;
    private ExecutorService executor;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getDatabase(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        executor = Executors.newFixedThreadPool(4);
    }

    // Existing methods
    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TaskEntity>> getActiveTasks() {
        return taskDao.getActiveTasks();
    }

    public LiveData<List<TaskEntity>> getCompletedTasks() {
        return taskDao.getCompletedTasks();
    }

    public void insertTaskLocal(TaskEntity task) {
        executor.execute(() -> {
            try {
                taskDao.insertTask(task);
                Log.d(TAG, "Task inserted successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error inserting task", e);
            }
        });
    }

    public void updateTaskLocal(TaskEntity task) {
        executor.execute(() -> {
            try {
                taskDao.updateTask(task);
                Log.d(TAG, "Task updated successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error updating task", e);
            }
        });
    }

    public void deleteTaskLocal(int taskId) {
        executor.execute(() -> {
            try {
                taskDao.deleteTask(taskId);
                Log.d(TAG, "Task deleted successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error deleting task", e);
            }
        });
    }

    // NEW METHODS for enhanced features
    public LiveData<List<String>> getAllCategories() {
        return taskDao.getAllCategories();
    }

    public LiveData<List<TaskEntity>> getTasksByCategory(String category) {
        return taskDao.getTasksByCategory(category);
    }

    public LiveData<List<TaskEntity>> getTasksByTag(String tag) {
        return taskDao.getTasksByTag(tag);
    }

    public LiveData<List<TaskEntity>> getTasksByPriority(int priority) {
        return taskDao.getTasksByPriority(priority);
    }

    public LiveData<Integer> getTodayCompletedCount() {
        return taskDao.getTodayCompletedCount();
    }

    public LiveData<Integer> getTodayCreatedCount() {
        return taskDao.getTodayCreatedCount();
    }

    public LiveData<TaskEntity> getTaskById(int taskId) {
        return taskDao.getTaskByIdLive(taskId);
    }
}