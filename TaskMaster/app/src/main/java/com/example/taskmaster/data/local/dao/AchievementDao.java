package com.example.taskmaster.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskmaster.data.local.entity.AchievementEntity;
import com.example.taskmaster.data.local.entity.UserProgressEntity;

import java.util.List;

@Dao
public interface AchievementDao {

    // Achievement queries
    @Query("SELECT * FROM achievements WHERE user_id = :userId ORDER BY is_unlocked DESC, achievement_type ASC")
    LiveData<List<AchievementEntity>> getUserAchievements(String userId);

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND is_unlocked = 1 ORDER BY unlock_date DESC")
    LiveData<List<AchievementEntity>> getUnlockedAchievements(String userId);

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND achievement_type = :type")
    LiveData<List<AchievementEntity>> getAchievementsByType(String userId, String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchievement(AchievementEntity achievement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchievements(List<AchievementEntity> achievements);

    @Update
    void updateAchievement(AchievementEntity achievement);

    @Query("UPDATE achievements SET current_progress = :progress WHERE user_id = :userId AND achievement_type = :type")
    void updateAchievementProgress(String userId, String type, int progress);

    @Query("UPDATE achievements SET is_unlocked = 1, unlock_date = :unlockDate WHERE id = :achievementId")
    void unlockAchievement(int achievementId, long unlockDate);

    // User Progress queries
    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    LiveData<UserProgressEntity> getUserProgress(String userId);

    @Query("SELECT * FROM user_progress WHERE user_id = :userId")
    UserProgressEntity getUserProgressSync(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserProgress(UserProgressEntity userProgress);

    @Update
    void updateUserProgress(UserProgressEntity userProgress);

    @Query("UPDATE user_progress SET experience_points = experience_points + :exp WHERE user_id = :userId")
    void addExperience(String userId, int exp);

    @Query("UPDATE user_progress SET total_tasks_created = total_tasks_created + 1 WHERE user_id = :userId")
    void incrementTasksCreated(String userId);

    @Query("UPDATE user_progress SET total_tasks_completed = total_tasks_completed + 1 WHERE user_id = :userId")
    void incrementTasksCompleted(String userId);

    @Query("UPDATE user_progress SET current_streak = :streak WHERE user_id = :userId")
    void updateCurrentStreak(String userId, int streak);

    @Query("UPDATE user_progress SET longest_streak = :streak WHERE user_id = :userId")
    void updateLongestStreak(String userId, int streak);
}