package com.example.taskmaster.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskmaster.data.local.entity.PomodoroEntity;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Pomodoro entity operations.
 * Provides methods to interact with pomodoro sessions in the database.
 */
@Dao
public interface PomodoroDao {

    @Query("SELECT * FROM pomodoros")
    List<PomodoroEntity> getAllPomodoros();

    @Query("SELECT * FROM pomodoros")
    LiveData<List<PomodoroEntity>> getAllPomodorosLive();

    @Query("SELECT * FROM pomodoros WHERE id = :id")
    PomodoroEntity getPomodoroById(int id);

    @Query("SELECT * FROM pomodoros WHERE task_id = :taskId")
    List<PomodoroEntity> getPomodorosByTaskId(int taskId);

    @Query("SELECT * FROM pomodoros WHERE task_id = :taskId")
    LiveData<List<PomodoroEntity>> getPomodorosByTaskIdLive(int taskId);

    @Query("SELECT * FROM pomodoros WHERE start_time BETWEEN :startDate AND :endDate")
    List<PomodoroEntity> getPomodorosByDateRange(Date startDate, Date endDate);

    @Query("SELECT SUM(duration) FROM pomodoros WHERE task_id = :taskId")
    long getTotalPomodoroTimeByTaskId(int taskId);

    @Query("SELECT COUNT(*) FROM pomodoros WHERE task_id = :taskId")
    int getCompletedPomodoroCountByTaskId(int taskId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPomodoro(PomodoroEntity pomodoro);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertPomodoros(List<PomodoroEntity> pomodoros);

    @Update
    int updatePomodoro(PomodoroEntity pomodoro);

    @Delete
    int deletePomodoro(PomodoroEntity pomodoro);

    @Query("DELETE FROM pomodoros WHERE task_id = :taskId")
    int deletePomodorosByTaskId(int taskId);

    @Query("DELETE FROM pomodoros")
    void deleteAllPomodoros();
}