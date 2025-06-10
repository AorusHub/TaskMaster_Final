package com.example.taskmaster.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.model.UserStats;
import com.example.taskmaster.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsViewModel extends ViewModel {

    private final MutableLiveData<UserStats> userStats = new MutableLiveData<>(new UserStats());
    private final MutableLiveData<Map<String, Integer>> taskCompletionByDay = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Map<String, Integer>> taskCompletionByCategory = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Date> statisticsStartDate = new MutableLiveData<>(DateTimeUtils.getStartOfWeek());

    public StatisticsViewModel() {
        loadStatistics();
    }

    public LiveData<UserStats> getUserStats() {
        return userStats;
    }

    public LiveData<Map<String, Integer>> getTaskCompletionByDay() {
        return taskCompletionByDay;
    }

    public LiveData<Map<String, Integer>> getTaskCompletionByCategory() {
        return taskCompletionByCategory;
    }

    public void loadStatisticsSince(Date startDate) {
        statisticsStartDate.setValue(startDate);
        loadStatistics();
    }

    public void loadStatisticsForWeek() {
        statisticsStartDate.setValue(DateTimeUtils.getStartOfWeek());
        loadStatistics();
    }

    public void loadStatisticsForMonth() {
        statisticsStartDate.setValue(DateTimeUtils.getStartOfMonth());
        loadStatistics();
    }

    public void loadStatisticsForYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        statisticsStartDate.setValue(calendar.getTime());
        loadStatistics();
    }

    private void loadStatistics() {
        // In a real app, this would load data from a repository
        // For now, we'll use sample data

        // Create sample tasks
        List<Task> completedTasks = generateSampleTasks(true);
        List<Task> allTasks = new ArrayList<>(completedTasks);
        allTasks.addAll(generateSampleTasks(false));

        // Calculate statistics
        calculateUserStats(completedTasks, allTasks);
        calculateTaskCompletionByDay(completedTasks);
        calculateTaskCompletionByCategory(completedTasks);
    }

    private List<Task> generateSampleTasks(boolean completed) {
        List<Task> tasks = new ArrayList<>();
        int count = completed ? 15 : 10;

        for (int i = 0; i < count; i++) {
            // Use the constructor with all required parameters
            // Parameters: id, title, description, category, priority, dueDate, completed
            Task task = new Task(
                    i,                      // id
                    "Task " + i,            // title
                    "Description " + i,     // description
                    "Work",                 // category (sample value)
                    "Medium",               // priority (sample value)
                    "07/15/2024",           // dueDate (sample value)
                    completed               // completed status
            );
            tasks.add(task);
        }

        return tasks;
    }

    private void calculateUserStats(List<Task> completedTasks, List<Task> allTasks) {
        UserStats stats = new UserStats();
        stats.setCompletedTasks(completedTasks.size());
        stats.setTotalTasks(allTasks.size());
        stats.setPomodoroSessions(30);
        stats.setPomodoroMinutes(750);
        stats.setAchievementCount(5);

        userStats.setValue(stats);
    }

    private void calculateTaskCompletionByDay(List<Task> completedTasks) {
        Map<String, Integer> completionByDay = new HashMap<>();
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        // Initialize the map with all days
        for (String day : daysOfWeek) {
            completionByDay.put(day, 0);
        }

        // Add some random data
        completionByDay.put("Monday", 3);
        completionByDay.put("Tuesday", 5);
        completionByDay.put("Wednesday", 2);
        completionByDay.put("Thursday", 4);
        completionByDay.put("Friday", 6);

        taskCompletionByDay.setValue(completionByDay);
    }

    private void calculateTaskCompletionByCategory(List<Task> completedTasks) {
        Map<String, Integer> completionByCategory = new HashMap<>();

        // Add sample categories
        completionByCategory.put("Work", 8);
        completionByCategory.put("Personal", 4);
        completionByCategory.put("Study", 6);
        completionByCategory.put("Health", 3);

        taskCompletionByCategory.setValue(completionByCategory);
    }
    private Task convertToTask(TaskEntity entity) {
        Task task = new Task(); // FIXED: Use default constructor
        task.setId(entity.getId());
        task.setTitle(entity.getTitle());
        task.setDescription(entity.getDescription());
        task.setPriority(entity.getPriorityString());
        task.setCompleted(entity.isCompleted());
        task.setCategory(entity.getCategory());
        task.setTags(entity.getTags());

        if (entity.getDueDate() != null) {
            task.setDueDate(entity.getFormattedDueDate());
        } else {
            task.setDueDate("No due date");
        }

        return task;
    }
}