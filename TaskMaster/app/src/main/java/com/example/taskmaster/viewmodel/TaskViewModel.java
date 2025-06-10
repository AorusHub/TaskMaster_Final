package com.example.taskmaster.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.data.repository.TaskRepository;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<TaskEntity>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
    }

    // Existing methods
    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TaskEntity>> getActiveTasks() {
        return repository.getActiveTasks();
    }

    public LiveData<List<TaskEntity>> getCompletedTasks() {
        return repository.getCompletedTasks();
    }

    public void insertTask(TaskEntity task) {
        repository.insertTaskLocal(task);
    }

    public void updateTask(TaskEntity task) {
        repository.updateTaskLocal(task);
    }

    public void deleteTask(int taskId) {
        repository.deleteTaskLocal(taskId);
    }

    // FIXED: Add missing method
    public LiveData<TaskEntity> getTaskById(int taskId) {
        return repository.getTaskById(taskId);
    }

    // Enhanced features methods
    public LiveData<List<String>> getAllCategories() {
        return repository.getAllCategories();
    }

    public LiveData<List<TaskEntity>> getTasksByCategory(String category) {
        return repository.getTasksByCategory(category);
    }

    public LiveData<List<TaskEntity>> getTasksByTag(String tag) {
        return repository.getTasksByTag(tag);
    }

    public LiveData<List<TaskEntity>> getTasksByPriority(int priority) {
        return repository.getTasksByPriority(priority);
    }

    public LiveData<Integer> getTodayCompletedCount() {
        return repository.getTodayCompletedCount();
    }

    public LiveData<Integer> getTodayCreatedCount() {
        return repository.getTodayCreatedCount();
    }
}