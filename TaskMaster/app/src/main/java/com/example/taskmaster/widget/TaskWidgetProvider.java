package com.example.taskmaster.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.example.taskmaster.R;
import com.example.taskmaster.data.local.TaskDatabase;
import com.example.taskmaster.data.local.dao.TaskDao;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update each widget
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Create an intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get the layout for the app widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_task_list);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

        // Load tasks asynchronously
        loadTasksAsync(context, views, appWidgetManager, appWidgetId);
    }

    private void loadTasksAsync(Context context, RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                TaskDatabase database = TaskDatabase.getDatabase(context);
                TaskDao taskDao = database.taskDao();

                // Get active tasks synchronously
                List<TaskEntity> taskEntities = taskDao.getActiveTasksSync();

                List<Task> tasks = new ArrayList<>();
                for (TaskEntity entity : taskEntities) {
                    Task task = convertEntityToTask(entity);
                    tasks.add(task);
                }

                // Update widget on main thread
                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                mainHandler.post(() -> {
                    updateWidgetWithTasks(context, views, tasks, appWidgetManager, appWidgetId);
                });

            } catch (Exception e) {
                // Fallback to default content
                android.os.Handler mainHandler = new android.os.Handler(context.getMainLooper());
                mainHandler.post(() -> {
                    updateWidgetWithError(context, views, appWidgetManager, appWidgetId);
                });
            }
        });
    }

    private Task convertEntityToTask(TaskEntity entity) {
        Task task = new Task();
        task.setId(entity.getId());
        task.setTitle(entity.getTitle());
        task.setDescription(entity.getDescription());
        task.setPriority(entity.getPriorityString());
        task.setCompleted(entity.isCompleted());
        task.setCategory(entity.getCategory());

        if (entity.getDueDate() != null) {
            task.setDueDate(entity.getFormattedDueDate());
        } else {
            task.setDueDate("No due date");
        }

        return task;
    }

    private void updateWidgetWithTasks(Context context, RemoteViews views, List<Task> tasks, AppWidgetManager appWidgetManager, int appWidgetId) {
        if (tasks.isEmpty()) {
            views.setTextViewText(R.id.widget_task_count, "No tasks");
            views.setTextViewText(R.id.widget_task_title, "All caught up! 🎉");
            views.setViewVisibility(R.id.widget_task_description, android.view.View.GONE);
            views.setViewVisibility(R.id.widget_task_priority, android.view.View.GONE);
            views.setViewVisibility(R.id.widget_task_category, android.view.View.GONE);
        } else {
            Task firstTask = tasks.get(0);

            // Update task count
            String countText = tasks.size() == 1 ? "1 task" : tasks.size() + " tasks";
            views.setTextViewText(R.id.widget_task_count, countText);

            // Update first task details
            views.setTextViewText(R.id.widget_task_title, firstTask.getTitle());

            // Show description if available
            if (firstTask.getDescription() != null && !firstTask.getDescription().trim().isEmpty()) {
                views.setTextViewText(R.id.widget_task_description, firstTask.getDescription());
                views.setViewVisibility(R.id.widget_task_description, android.view.View.VISIBLE);
            } else {
                views.setViewVisibility(R.id.widget_task_description, android.view.View.GONE);
            }

            // Show priority
            views.setTextViewText(R.id.widget_task_priority, firstTask.getPriority());
            views.setViewVisibility(R.id.widget_task_priority, android.view.View.VISIBLE);

            // Set priority color
            int priorityColor = getPriorityColor(firstTask.getPriority());
            views.setInt(R.id.widget_task_priority, "setBackgroundColor", priorityColor);

            // Show category
            if (firstTask.getCategory() != null && !firstTask.getCategory().trim().isEmpty()) {
                views.setTextViewText(R.id.widget_task_category, firstTask.getCategory());
                views.setViewVisibility(R.id.widget_task_category, android.view.View.VISIBLE);
            } else {
                views.setViewVisibility(R.id.widget_task_category, android.view.View.GONE);
            }
        }

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateWidgetWithError(Context context, RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
        views.setTextViewText(R.id.widget_task_count, "Error");
        views.setTextViewText(R.id.widget_task_title, "Failed to load tasks");
        views.setViewVisibility(R.id.widget_task_description, android.view.View.GONE);
        views.setViewVisibility(R.id.widget_task_priority, android.view.View.GONE);
        views.setViewVisibility(R.id.widget_task_category, android.view.View.GONE);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private int getPriorityColor(String priority) {
        switch (priority.toLowerCase()) {
            case "high": return Color.parseColor("#F44336");
            case "medium": return Color.parseColor("#FF9800");
            case "low": return Color.parseColor("#4CAF50");
            default: return Color.parseColor("#757575");
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}