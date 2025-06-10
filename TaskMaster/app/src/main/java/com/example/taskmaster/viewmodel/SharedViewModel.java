package com.example.taskmaster.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskmaster.model.Achievement;
import com.example.taskmaster.model.UserStats;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {

    // User profile data
    private final MutableLiveData<String> username = new MutableLiveData<>("John Doe");
    private final MutableLiveData<String> email = new MutableLiveData<>("john.doe@example.com");
    private final MutableLiveData<String> profileImageUrl = new MutableLiveData<>();

    // User statistics
    private final MutableLiveData<UserStats> userStats = new MutableLiveData<>(new UserStats());

    // Achievements
    private final MutableLiveData<List<Achievement>> achievements = new MutableLiveData<>(new ArrayList<>());

    // App preferences
    private final MutableLiveData<String> appTheme = new MutableLiveData<>("system");
    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>(true);

    public SharedViewModel() {
        loadUserData();
        loadAchievements();
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getProfileImageUrl() {
        return profileImageUrl;
    }

    public LiveData<UserStats> getUserStats() {
        return userStats;
    }

    public LiveData<List<Achievement>> getAchievements() {
        return achievements;
    }

    public LiveData<String> getAppTheme() {
        return appTheme;
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void updateUsername(String username) {
        this.username.setValue(username);
        // In a real app, you would save this to a repository
    }

    public void updateEmail(String email) {
        this.email.setValue(email);
        // In a real app, you would save this to a repository
    }

    public void updateProfileImage(String imageUrl) {
        this.profileImageUrl.setValue(imageUrl);
        // In a real app, you would save this to a repository
    }

    public void updateAppTheme(String theme) {
        this.appTheme.setValue(theme);
        // In a real app, you would save this to SharedPreferences
    }

    public void updateNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled.setValue(enabled);
        // In a real app, you would save this to SharedPreferences
    }

    public void updateStats(UserStats stats) {
        this.userStats.setValue(stats);
        // In a real app, you would save this to a repository
    }

    private void loadUserData() {
        // In a real app, this would load data from a repository
        // For now, we'll use sample data
        UserStats stats = new UserStats();
        stats.setCompletedTasks(15);
        stats.setTotalTasks(25);
        stats.setPomodoroSessions(30);
        stats.setPomodoroMinutes(750);
        stats.setAchievementCount(5);

        userStats.setValue(stats);

        // Set profile image URL
        profileImageUrl.setValue("https://example.com/profile.jpg");
    }

    private void loadAchievements() {
        // In a real app, this would load data from a repository
        // For now, we'll use sample data
        List<Achievement> achievementList = new ArrayList<>();

        Achievement a1 = new Achievement();
        a1.setTitle("Task Master");
        a1.setDescription("Complete 10 tasks");
        a1.setUnlocked(true);
        a1.setProgress(100);
        achievementList.add(a1);

        Achievement a2 = new Achievement();
        a2.setTitle("Focus Champion");
        a2.setDescription("Complete 20 Pomodoro sessions");
        a2.setUnlocked(true);
        a2.setProgress(100);
        achievementList.add(a2);

        Achievement a3 = new Achievement();
        a3.setTitle("Productivity Guru");
        a3.setDescription("Complete 5 tasks in one day");
        a3.setUnlocked(true);
        a3.setProgress(100);
        achievementList.add(a3);

        Achievement a4 = new Achievement();
        a4.setTitle("Time Wizard");
        a4.setDescription("Accumulate 10 hours of focus time");
        a4.setUnlocked(false);
        a4.setProgress(75);
        achievementList.add(a4);

        Achievement a5 = new Achievement();
        a5.setTitle("Category Master");
        a5.setDescription("Complete tasks in all categories");
        a5.setUnlocked(false);
        a5.setProgress(60);
        achievementList.add(a5);

        achievements.setValue(achievementList);
    }

    public void checkForNewAchievements() {
        // In a real app, this would check if the user has unlocked new achievements
        // based on their current stats
        // For now, we'll just simulate this

        List<Achievement> currentAchievements = achievements.getValue();
        if (currentAchievements == null) return;

        for (Achievement achievement : currentAchievements) {
            if (!achievement.isUnlocked() && achievement.getProgress() == 100) {
                achievement.setUnlocked(true);
                // Notify user of new achievement
            }
        }

        achievements.setValue(currentAchievements);
    }
}