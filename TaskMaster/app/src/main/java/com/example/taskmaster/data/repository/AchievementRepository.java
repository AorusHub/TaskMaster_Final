package com.example.taskmaster.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.taskmaster.data.local.TaskDatabase;
import com.example.taskmaster.data.local.dao.AchievementDao;
import com.example.taskmaster.data.local.entity.AchievementEntity;
import com.example.taskmaster.data.local.entity.UserProgressEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AchievementRepository {

    private AchievementDao achievementDao;
    private ExecutorService executor;

    public AchievementRepository(Application application) {
        TaskDatabase database = TaskDatabase.getDatabase(application);
        achievementDao = database.achievementDao();
        executor = Executors.newFixedThreadPool(2);
    }

    // User Progress methods
    public LiveData<UserProgressEntity> getUserProgress(String userId) {
        return achievementDao.getUserProgress(userId);
    }

    public void initializeUserProgress(String userId, String username) {
        executor.execute(() -> {
            UserProgressEntity existingProgress = achievementDao.getUserProgressSync(userId);
            if (existingProgress == null) {
                UserProgressEntity newProgress = new UserProgressEntity(userId, username);
                achievementDao.insertUserProgress(newProgress);

                // Initialize default achievements
                initializeDefaultAchievements(userId);
            }
        });
    }

    private void initializeDefaultAchievements(String userId) {
        List<AchievementEntity> defaultAchievements = new ArrayList<>();

        // Task completion achievements
        defaultAchievements.add(new AchievementEntity(userId, "TASK_COMPLETION",
                "First Steps", "Complete your first task", "🎯", 1, 10));
        defaultAchievements.add(new AchievementEntity(userId, "TASK_COMPLETION",
                "Getting Started", "Complete 5 tasks", "⭐", 5, 25));
        defaultAchievements.add(new AchievementEntity(userId, "TASK_COMPLETION",
                "Task Master", "Complete 10 tasks", "🏆", 10, 50));
        defaultAchievements.add(new AchievementEntity(userId, "TASK_COMPLETION",
                "Productivity Pro", "Complete 25 tasks", "💎", 25, 100));
        defaultAchievements.add(new AchievementEntity(userId, "TASK_COMPLETION",
                "Achievement Hunter", "Complete 50 tasks", "👑", 50, 200));

        // Streak achievements
        defaultAchievements.add(new AchievementEntity(userId, "STREAK",
                "Consistent", "Maintain a 3-day streak", "🔥", 3, 30));
        defaultAchievements.add(new AchievementEntity(userId, "STREAK",
                "Dedicated", "Maintain a 7-day streak", "💪", 7, 70));
        defaultAchievements.add(new AchievementEntity(userId, "STREAK",
                "Unstoppable", "Maintain a 30-day streak", "🚀", 30, 300));

        // Task creation achievements
        defaultAchievements.add(new AchievementEntity(userId, "TASK_CREATION",
                "Planner", "Create 10 tasks", "📝", 10, 20));
        defaultAchievements.add(new AchievementEntity(userId, "TASK_CREATION",
                "Organizer", "Create 25 tasks", "📊", 25, 50));

        // Level achievements
        defaultAchievements.add(new AchievementEntity(userId, "LEVEL",
                "Rising Star", "Reach Level 5", "🌟", 5, 0));
        defaultAchievements.add(new AchievementEntity(userId, "LEVEL",
                "Expert", "Reach Level 10", "🎖️", 10, 0));

        achievementDao.insertAchievements(defaultAchievements);
    }

    // Achievement methods
    public LiveData<List<AchievementEntity>> getUserAchievements(String userId) {
        return achievementDao.getUserAchievements(userId);
    }

    public LiveData<List<AchievementEntity>> getUnlockedAchievements(String userId) {
        return achievementDao.getUnlockedAchievements(userId);
    }

    // Activity tracking methods
    public void onTaskCreated(String userId) {
        executor.execute(() -> {
            achievementDao.incrementTasksCreated(userId);
            achievementDao.addExperience(userId, 5); // 5 XP for creating task

            UserProgressEntity progress = achievementDao.getUserProgressSync(userId);
            if (progress != null) {
                updateAchievementProgress(userId, "TASK_CREATION", progress.getTotalTasksCreated());
                checkLevelUp(userId, progress);
            }
        });
    }

    public void onTaskCompleted(String userId) {
        executor.execute(() -> {
            achievementDao.incrementTasksCompleted(userId);
            achievementDao.addExperience(userId, 10); // 10 XP for completing task

            UserProgressEntity progress = achievementDao.getUserProgressSync(userId);
            if (progress != null) {
                updateAchievementProgress(userId, "TASK_COMPLETION", progress.getTotalTasksCompleted());
                checkLevelUp(userId, progress);
            }
        });
    }

    public void onStreakUpdated(String userId, int streak) {
        executor.execute(() -> {
            achievementDao.updateCurrentStreak(userId, streak);

            UserProgressEntity progress = achievementDao.getUserProgressSync(userId);
            if (progress != null && streak > progress.getLongestStreak()) {
                achievementDao.updateLongestStreak(userId, streak);
            }

            updateAchievementProgress(userId, "STREAK", streak);

            // Bonus XP for streaks
            if (streak > 0) {
                achievementDao.addExperience(userId, streak * 2);
            }
        });
    }

    private void updateAchievementProgress(String userId, String achievementType, int progress) {
        achievementDao.updateAchievementProgress(userId, achievementType, progress);

        // Check for newly unlocked achievements
        // This would require querying achievements and checking if any can be unlocked
        checkForUnlockedAchievements(userId, achievementType, progress);
    }

    private void checkForUnlockedAchievements(String userId, String achievementType, int progress) {
        // Implementation would check if any achievements of this type can now be unlocked
        // and unlock them, awarding XP
    }

    private void checkLevelUp(String userId, UserProgressEntity progress) {
        if (progress.canLevelUp()) {
            int newLevel = progress.getLevel() + 1;
            progress.setLevel(newLevel);
            achievementDao.updateUserProgress(progress);

            // Update level achievements
            updateAchievementProgress(userId, "LEVEL", newLevel);
        }
    }
}