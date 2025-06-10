package com.example.taskmaster.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.taskmaster.data.local.dao.AchievementDao;
import com.example.taskmaster.data.local.dao.TaskDao;
import com.example.taskmaster.data.local.entity.AchievementEntity;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.data.local.converter.DateConverter;
import com.example.taskmaster.data.local.entity.UserProgressEntity;

/**
 * Main database class using Room persistence library.
 * This class defines all entities and DAOs used in the application.
 */
@Database(
        entities = {TaskEntity.class, AchievementEntity.class, UserProgressEntity.class},
        version = 4, // Increment version
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class TaskDatabase extends RoomDatabase {

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add new columns to tasks table
            database.execSQL("ALTER TABLE tasks ADD COLUMN category TEXT");
            database.execSQL("ALTER TABLE tasks ADD COLUMN tags TEXT");
            database.execSQL("ALTER TABLE tasks ADD COLUMN priority INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE tasks ADD COLUMN due_date INTEGER");
            database.execSQL("ALTER TABLE tasks ADD COLUMN is_recurring INTEGER NOT NULL DEFAULT 0");
        }
    };
    private static volatile TaskDatabase INSTANCE;

    public abstract TaskDao taskDao();
    public abstract AchievementDao achievementDao(); // ADD THIS

    // Migration from version 3 to 4
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create achievements table
            database.execSQL("CREATE TABLE IF NOT EXISTS `achievements` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`user_id` TEXT, " +
                    "`achievement_type` TEXT, " +
                    "`title` TEXT, " +
                    "`description` TEXT, " +
                    "`icon` TEXT, " +
                    "`is_unlocked` INTEGER NOT NULL, " +
                    "`unlock_date` INTEGER, " +
                    "`requirement_value` INTEGER NOT NULL, " +
                    "`current_progress` INTEGER NOT NULL, " +
                    "`experience_points` INTEGER NOT NULL)");

            // Create user_progress table
            database.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (" +
                    "`user_id` TEXT PRIMARY KEY NOT NULL, " +
                    "`username` TEXT, " +
                    "`level` INTEGER NOT NULL, " +
                    "`experience_points` INTEGER NOT NULL, " +
                    "`total_tasks_created` INTEGER NOT NULL, " +
                    "`total_tasks_completed` INTEGER NOT NULL, " +
                    "`current_streak` INTEGER NOT NULL, " +
                    "`longest_streak` INTEGER NOT NULL, " +
                    "`last_activity_date` INTEGER, " +
                    "`created_at` INTEGER)");
        }
    };

    public static TaskDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TaskDatabase.class, "task_database")
                            .addMigrations(MIGRATION_2_3, MIGRATION_3_4) // Add new migration
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ... rest of existing methods
}