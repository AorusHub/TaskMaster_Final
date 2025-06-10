package com.example.taskmaster.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.data.repository.CalendarRepository;
import com.example.taskmaster.model.CalendarEvent;

import java.util.HashMap;
import java.util.Map;

public class TaskCalendarManager {
    private static final String TAG = "TaskCalendarManager";
    private static final String PREFS_NAME = "task_calendar_prefs";
    private static final String KEY_PREFIX = "task_event_";

    private final Context context;
    private final CalendarRepository calendarRepository;
    private final Map<Integer, String> taskEventMap;

    public TaskCalendarManager(Context context) {
        this.context = context;
        this.calendarRepository = new CalendarRepository();
        this.taskEventMap = new HashMap<>();
        loadMappings();
    }

    /**
     * Load saved mappings between tasks and events
     */
    private void loadMappings() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(KEY_PREFIX)) {
                try {
                    int taskId = Integer.parseInt(entry.getKey().substring(KEY_PREFIX.length()));
                    String eventId = (String) entry.getValue();
                    taskEventMap.put(taskId, eventId);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing task ID", e);
                }
            }
        }
    }

    /**
     * Save mapping between task and event
     */
    private void saveMapping(int taskId, String eventId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_PREFIX + taskId, eventId);
        editor.apply();
        taskEventMap.put(taskId, eventId);
    }

    /**
     * Remove mapping for a task
     */
    private void removeMapping(int taskId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_PREFIX + taskId);
        editor.apply();
        taskEventMap.remove(taskId);
    }

    /**
     * Sync a task with the calendar
     */
    public void syncTaskWithCalendar(TaskEntity task, SyncCallback callback) {
        if (task.getDueDate() == null) {
            callback.onSyncComplete(false, "Task has no due date");
            return;
        }

        String eventId = taskEventMap.get(task.getId());

        if (eventId == null) {
            // No event exists for this task, create one
            calendarRepository.addTaskToCalendar(task, new CalendarRepository.CalendarOperationCallback() {
                @Override
                public void onSuccess(CalendarEvent event) {
                    saveMapping(task.getId(), event.getId());
                    callback.onSyncComplete(true, "Task added to calendar");
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onSyncComplete(false, errorMessage);
                }
            });
        } else {
            // Event exists, update it
            calendarRepository.updateTaskCalendarEvent(eventId, task, new CalendarRepository.CalendarOperationCallback() {
                @Override
                public void onSuccess(CalendarEvent event) {
                    callback.onSyncComplete(true, "Calendar event updated");
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onSyncComplete(false, errorMessage);
                }
            });
        }
    }

    /**
     * Remove task from calendar
     */
    public void removeTaskFromCalendar(TaskEntity task, SyncCallback callback) {
        String eventId = taskEventMap.get(task.getId());

        if (eventId != null) {
            calendarRepository.deleteCalendarEvent(eventId, new CalendarRepository.CalendarOperationCallback() {
                @Override
                public void onSuccess(CalendarEvent event) {
                    removeMapping(task.getId());
                    callback.onSyncComplete(true, "Task removed from calendar");
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onSyncComplete(false, errorMessage);
                }
            });
        } else {
            callback.onSyncComplete(true, "No calendar event to delete");
        }
    }

    /**
     * Check if a task has a calendar event
     */
    public boolean hasCalendarEvent(int taskId) {
        return taskEventMap.containsKey(taskId);
    }

    public interface SyncCallback {
        void onSyncComplete(boolean success, String message);
    }
}