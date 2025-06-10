package com.example.taskmaster.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.taskmaster.data.local.entity.AchievementEntity;
import com.example.taskmaster.data.local.entity.UserProgressEntity;
import com.example.taskmaster.data.repository.AchievementRepository;
import com.example.taskmaster.model.RecentActivity;

import java.util.List;

public class AchievementViewModel extends AndroidViewModel {

    private AchievementRepository repository;

    public AchievementViewModel(@NonNull Application application) {
        super(application);
        repository = new AchievementRepository(application);
    }

    // User Progress methods
    public LiveData<UserProgressEntity> getUserProgress(String userId) {
        return repository.getUserProgress(userId);
    }

    public void initializeUserProgress(String userId, String username) {
        repository.initializeUserProgress(userId, username);
    }

    // Achievement methods
    public LiveData<List<AchievementEntity>> getUserAchievements(String userId) {
        return repository.getUserAchievements(userId);
    }

    public LiveData<List<AchievementEntity>> getUnlockedAchievements(String userId) {
        return repository.getUnlockedAchievements(userId);
    }

    // Activity tracking methods
    public void onTaskCreated(String userId) {
        repository.onTaskCreated(userId);
    }

    public void onTaskCompleted(String userId) {
        repository.onTaskCompleted(userId);
    }

    public void onStreakUpdated(String userId, int streak) {
        repository.onStreakUpdated(userId, streak);
    }

    // Recent activities (mock data for now)
    public LiveData<List<RecentActivity>> getRecentActivities(String userId) {
        // This would be implemented with actual activity tracking
        MutableLiveData<List<RecentActivity>> activities = new MutableLiveData<>();
        // For now, return empty list
        activities.setValue(java.util.Collections.emptyList());
        return activities;
    }
}