package com.example.taskmaster.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.data.repository.CalendarRepository;
import com.example.taskmaster.model.CalendarEvent;
import com.example.taskmaster.util.TaskCalendarManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarViewModel extends AndroidViewModel {
    private final CalendarRepository calendarRepository;
    private final MutableLiveData<List<CalendarEvent>> events = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private TaskCalendarManager calendarManager;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        calendarRepository = new CalendarRepository();
        calendarManager = new TaskCalendarManager(application.getApplicationContext());
    }

    // Get LiveData objects
    public LiveData<List<CalendarEvent>> getEvents() {
        return events;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    // Load events for current month
    public void loadCurrentMonthEvents() {
        loading.setValue(true);

        // Calculate start and end dates for current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // First day of month
        Date startDate = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Last day of month
        Date endDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);

        calendarRepository.getEvents(startDateStr, endDateStr, new CalendarRepository.EventsCallback() {
            @Override
            public void onEventsLoaded(List<CalendarEvent> calendarEvents) {
                events.postValue(calendarEvents);
                loading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                loading.postValue(false);
            }
        });
    }

    // Sync task with calendar
    public void syncTaskWithCalendar(TaskEntity task) {
        loading.setValue(true);

        calendarManager.syncTaskWithCalendar(task, new TaskCalendarManager.SyncCallback() {
            @Override
            public void onSyncComplete(boolean success, String message) {
                if (success) {
                    successMessage.postValue(message);
                } else {
                    errorMessage.postValue(message);
                }
                loading.postValue(false);
            }
        });
    }

    // Remove task from calendar
    public void removeTaskFromCalendar(TaskEntity task) {
        loading.setValue(true);

        calendarManager.removeTaskFromCalendar(task, new TaskCalendarManager.SyncCallback() {
            @Override
            public void onSyncComplete(boolean success, String message) {
                if (success) {
                    successMessage.postValue(message);
                } else {
                    errorMessage.postValue(message);
                }
                loading.postValue(false);
            }
        });
    }

    // Check if task has calendar event
    public boolean taskHasCalendarEvent(TaskEntity task) {
        return calendarManager.hasCalendarEvent(task.getId());
    }
}