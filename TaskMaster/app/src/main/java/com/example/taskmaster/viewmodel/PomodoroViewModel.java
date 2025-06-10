package com.example.taskmaster.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.taskmaster.model.Pomodoro;
import com.example.taskmaster.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PomodoroViewModel extends ViewModel {

    private final MutableLiveData<List<Pomodoro>> completedSessions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> totalFocusMinutes = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> todayFocusMinutes = new MutableLiveData<>(0);

    // Current timer settings
    private final MutableLiveData<Integer> workDuration = new MutableLiveData<>(25);
    private final MutableLiveData<Integer> shortBreakDuration = new MutableLiveData<>(5);
    private final MutableLiveData<Integer> longBreakDuration = new MutableLiveData<>(15);
    private final MutableLiveData<Integer> sessionsBeforeLongBreak = new MutableLiveData<>(4);

    // Current session state
    private final MutableLiveData<String> currentSessionType = new MutableLiveData<>(Constants.SESSION_WORK);
    private final MutableLiveData<Integer> completedWorkSessions = new MutableLiveData<>(0);

    public PomodoroViewModel() {
        loadSavedSessions();
        calculateStatistics();
    }

    public LiveData<List<Pomodoro>> getCompletedSessions() {
        return completedSessions;
    }

    public LiveData<Integer> getTotalFocusMinutes() {
        return totalFocusMinutes;
    }

    public LiveData<Integer> getTodayFocusMinutes() {
        return todayFocusMinutes;
    }

    public LiveData<Integer> getWorkDuration() {
        return workDuration;
    }

    public LiveData<Integer> getShortBreakDuration() {
        return shortBreakDuration;
    }

    public LiveData<Integer> getLongBreakDuration() {
        return longBreakDuration;
    }

    public LiveData<Integer> getSessionsBeforeLongBreak() {
        return sessionsBeforeLongBreak;
    }

    public LiveData<String> getCurrentSessionType() {
        return currentSessionType;
    }

    public LiveData<Integer> getCompletedWorkSessions() {
        return completedWorkSessions;
    }

    public void updateSettings(int workDuration, int shortBreakDuration,
                               int longBreakDuration, int sessionsBeforeLongBreak) {
        this.workDuration.setValue(workDuration);
        this.shortBreakDuration.setValue(shortBreakDuration);
        this.longBreakDuration.setValue(longBreakDuration);
        this.sessionsBeforeLongBreak.setValue(sessionsBeforeLongBreak);
    }

    public void saveCompletedSession(Pomodoro pomodoro) {
        List<Pomodoro> currentSessions = completedSessions.getValue();
        if (currentSessions != null) {
            currentSessions.add(pomodoro);
            completedSessions.setValue(currentSessions);

            // If it was a work session, increment the completed work sessions counter
            if (Constants.SESSION_WORK.equals(pomodoro.getSessionType())) {
                int currentCount = completedWorkSessions.getValue() != null ?
                        completedWorkSessions.getValue() : 0;
                completedWorkSessions.setValue(currentCount + 1);

                // Update focus minutes statistics
                updateFocusStatistics(pomodoro.getDurationMinutes());
            }

            // Determine next session type
            determineNextSessionType();
        }
    }

    public String getNextSessionType() {
        int currentCompletedSessions = completedWorkSessions.getValue() != null ?
                completedWorkSessions.getValue() : 0;
        int sessionsLimit = sessionsBeforeLongBreak.getValue() != null ?
                sessionsBeforeLongBreak.getValue() : 4;

        if (Constants.SESSION_WORK.equals(currentSessionType.getValue())) {
            // After work, take a break
            return (currentCompletedSessions % sessionsLimit == 0 && currentCompletedSessions > 0) ?
                    Constants.SESSION_LONG_BREAK : Constants.SESSION_SHORT_BREAK;
        } else {
            // After a break, get back to work
            return Constants.SESSION_WORK;
        }
    }

    public void switchSessionType() {
        currentSessionType.setValue(getNextSessionType());
    }

    private void determineNextSessionType() {
        currentSessionType.setValue(getNextSessionType());
    }

    private void updateFocusStatistics(int minutes) {
        // Update total focus minutes
        int current = totalFocusMinutes.getValue() != null ? totalFocusMinutes.getValue() : 0;
        totalFocusMinutes.setValue(current + minutes);

        // Update today's focus minutes
        int todayCurrent = todayFocusMinutes.getValue() != null ? todayFocusMinutes.getValue() : 0;
        todayFocusMinutes.setValue(todayCurrent + minutes);
    }

    private void loadSavedSessions() {
        // In a real app, this would load data from a repository
        // For now, we'll use sample data
        List<Pomodoro> sessions = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 5; i++) {
            calendar.add(Calendar.HOUR, -i);
            Pomodoro pomodoro = new Pomodoro();
            pomodoro.setSessionType(i % 3 == 0 ? Constants.SESSION_SHORT_BREAK : Constants.SESSION_WORK);
            pomodoro.setDurationMinutes(i % 3 == 0 ? 5 : 25);
            pomodoro.setStartTime(calendar.getTime());
            sessions.add(pomodoro);
        }

        completedSessions.setValue(sessions);
    }

    private void calculateStatistics() {
        List<Pomodoro> sessions = completedSessions.getValue();
        if (sessions == null) return;

        int total = 0;
        int today = 0;
        int workSessionCount = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();

        for (Pomodoro session : sessions) {
            if (Constants.SESSION_WORK.equals(session.getSessionType())) {
                total += session.getDurationMinutes();
                workSessionCount++;

                // Check if session was completed today
                if (session.getStartTime() != null && session.getStartTime().after(startOfDay)) {
                    today += session.getDurationMinutes();
                }
            }
        }

        totalFocusMinutes.setValue(total);
        todayFocusMinutes.setValue(today);
        completedWorkSessions.setValue(workSessionCount);
    }
}