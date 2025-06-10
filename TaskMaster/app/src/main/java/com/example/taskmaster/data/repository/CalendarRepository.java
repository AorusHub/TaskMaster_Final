package com.example.taskmaster.data.repository;

import android.util.Log;

import com.example.taskmaster.api.ApiService;
import com.example.taskmaster.api.RetrofitClient;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.model.CalendarEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarRepository {
    private static final String TAG = "CalendarRepository";
    private final ApiService apiService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

    public CalendarRepository() {
        this.apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    /**
     * Get calendar events for a date range
     */
    public void getEvents(String startDate, String endDate, EventsCallback callback) {
        Call<List<CalendarEvent>> call = apiService.getCalendarEvents(startDate, endDate);

        call.enqueue(new Callback<List<CalendarEvent>>() {
            @Override
            public void onResponse(Call<List<CalendarEvent>> call, Response<List<CalendarEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onEventsLoaded(response.body());
                } else {
                    callback.onError("Failed to fetch events: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<CalendarEvent>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error when fetching events", t);
            }
        });
    }

    /**
     * Add a task to calendar
     */
    public void addTaskToCalendar(TaskEntity task, CalendarOperationCallback callback) {
        // Parse due date
        Date dueDate = task.getDueDate();
        if (dueDate == null) {
            callback.onError("Task has no due date");
            return;
        }

        // Set event start time to due date
        String startTime = dateTimeFormat.format(dueDate);

        // Set event end time to 1 hour after start
        Date endDate = new Date(dueDate.getTime() + 3600000);
        String endTime = dateTimeFormat.format(endDate);

        // Create calendar event
        CalendarEvent event = new CalendarEvent(
                task.getTitle(),
                task.getDescription(),
                startTime,
                endTime,
                task.getId()
        );

        // API call to create event
        Call<CalendarEvent> call = apiService.createCalendarEvent(event);
        call.enqueue(new Callback<CalendarEvent>() {
            @Override
            public void onResponse(Call<CalendarEvent> call, Response<CalendarEvent> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create event: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CalendarEvent> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error when creating event", t);
            }
        });

    }

    /**
     * Update a calendar event for a task
     */
    public void updateTaskCalendarEvent(String eventId, TaskEntity task, CalendarOperationCallback callback) {
        // Parse due date
        Date dueDate = task.getDueDate();
        if (dueDate == null) {
            callback.onError("Invalid due date format");
            return;
        }

        // Set event times
        String startTime = dateTimeFormat.format(dueDate);
        Date endDate = new Date(dueDate.getTime() + 3600000);
        String endTime = dateTimeFormat.format(endDate);

        // Create updated calendar event
        CalendarEvent event = new CalendarEvent(
                task.getTitle(),
                task.getDescription(),
                startTime,
                endTime,
                task.getId()
        );

        // API call to update event
        Call<CalendarEvent> call = apiService.updateCalendarEvent(eventId, event);
        call.enqueue(new Callback<CalendarEvent>() {
            @Override
            public void onResponse(Call<CalendarEvent> call, Response<CalendarEvent> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update event: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CalendarEvent> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error when updating event", t);
            }
        });

    }

    /**
     * Delete a calendar event
     */
    public void deleteCalendarEvent(String eventId, CalendarOperationCallback callback) {
        Call<Void> call = apiService.deleteCalendarEvent(eventId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to delete event: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Network error when deleting event", t);
            }
        });
    }

    // Callback interfaces
    public interface EventsCallback {
        void onEventsLoaded(List<CalendarEvent> events);
        void onError(String errorMessage);
    }

    public interface CalendarOperationCallback {
        void onSuccess(CalendarEvent event);
        void onError(String errorMessage);
    }
}