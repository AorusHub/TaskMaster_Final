package com.example.taskmaster.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taskmaster.R;

public class PomodoroFragment extends Fragment {

    // Timer states
    private enum TimerState {
        STOPPED, RUNNING, PAUSED
    }

    private enum SessionType {
        WORK, SHORT_BREAK, LONG_BREAK
    }

    // Views
    private TextView tvSessionType;
    private TextView tvSessionCount;
    private TextView tvTimeRemaining;
    private TextView tvMinutesLabel;
    private TextView tvTodayCount;
    private TextView tvTotalCount;
    private ProgressBar progressCircle;
    private Button btnStartPause;
    private Button btnReset;
    private Button btnSettings;
    private TextView chipWorkMode;
    private TextView chipShortBreak;
    private TextView chipLongBreak;
    private ImageView ivPomodoroIcon;

    // Timer variables
    private CountDownTimer countDownTimer;
    private TimerState timerState = TimerState.STOPPED;
    private SessionType currentSessionType = SessionType.WORK;
    private long timeLeftInMillis;
    private long initialTimeInMillis;
    private int currentSession = 1;
    private int totalSessions = 4;

    // Time constants (in milliseconds)
    private static final long WORK_TIME = 25 * 60 * 1000; // 25 minutes
    private static final long SHORT_BREAK_TIME = 5 * 60 * 1000; // 5 minutes
    private static final long LONG_BREAK_TIME = 15 * 60 * 1000; // 15 minutes

    // SharedPreferences
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "PomodoroPrefs";
    private static final String KEY_TODAY_COUNT = "today_count";
    private static final String KEY_TOTAL_COUNT = "total_count";
    private static final String KEY_LAST_DATE = "last_date";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        initViews(view);
        initPreferences();
        setupClickListeners();
        resetTimer();
        updateUI();

        return view;
    }

    private void initViews(View view) {
        tvSessionType = view.findViewById(R.id.tvSessionType);
        tvSessionCount = view.findViewById(R.id.tvSessionCount);
        tvTimeRemaining = view.findViewById(R.id.tvTimeRemaining);
        tvMinutesLabel = view.findViewById(R.id.tvMinutesLabel);
        tvTodayCount = view.findViewById(R.id.tvTodayCount);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        progressCircle = view.findViewById(R.id.progressCircle);
        btnStartPause = view.findViewById(R.id.btnStartPause);
        btnReset = view.findViewById(R.id.btnReset);
        btnSettings = view.findViewById(R.id.btnSettings);
        chipWorkMode = view.findViewById(R.id.chipWorkMode);
        chipShortBreak = view.findViewById(R.id.chipShortBreak);
        chipLongBreak = view.findViewById(R.id.chipLongBreak);
        ivPomodoroIcon = view.findViewById(R.id.ivPomodoroIcon);
    }

    private void initPreferences() {
        if (getContext() != null) {
            prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            // Check if it's a new day
            String today = java.text.DateFormat.getDateInstance().format(new java.util.Date());
            String lastDate = prefs.getString(KEY_LAST_DATE, "");

            if (!today.equals(lastDate)) {
                // Reset today count for new day
                prefs.edit()
                        .putInt(KEY_TODAY_COUNT, 0)
                        .putString(KEY_LAST_DATE, today)
                        .apply();
            }

            updateStatistics();
        }
    }

    private void setupClickListeners() {
        btnStartPause.setOnClickListener(v -> {
            if (timerState == TimerState.STOPPED || timerState == TimerState.PAUSED) {
                startTimer();
            } else {
                pauseTimer();
            }
        });

        btnReset.setOnClickListener(v -> resetTimer());

        btnSettings.setOnClickListener(v -> {
            // TODO: Open settings dialog
            showToast("Settings coming soon!");
        });

        chipWorkMode.setOnClickListener(v -> {
            if (timerState == TimerState.STOPPED) {
                setSessionType(SessionType.WORK);
            }
        });

        chipShortBreak.setOnClickListener(v -> {
            if (timerState == TimerState.STOPPED) {
                setSessionType(SessionType.SHORT_BREAK);
            }
        });

        chipLongBreak.setOnClickListener(v -> {
            if (timerState == TimerState.STOPPED) {
                setSessionType(SessionType.LONG_BREAK);
            }
        });
    }

    private void startTimer() {
        timerState = TimerState.RUNNING;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
                updateProgress();
            }

            @Override
            public void onFinish() {
                timerState = TimerState.STOPPED;
                onTimerComplete();
            }
        }.start();

        updateUI();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerState = TimerState.PAUSED;
        updateUI();
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timerState = TimerState.STOPPED;
        timeLeftInMillis = getTimeForCurrentSession();
        initialTimeInMillis = timeLeftInMillis;

        updateTimerDisplay();
        updateProgress();
        updateUI();
    }

    private void onTimerComplete() {
        // Play sound and vibrate
        playNotificationSound();
        vibrate();

        // Update statistics for completed work sessions
        if (currentSessionType == SessionType.WORK) {
            incrementSessionCount();
            currentSession++;

            // Auto switch to break
            if (currentSession > totalSessions) {
                setSessionType(SessionType.LONG_BREAK);
                currentSession = 1;
            } else {
                setSessionType(SessionType.SHORT_BREAK);
            }
        } else {
            // After break, switch back to work
            setSessionType(SessionType.WORK);
        }

        resetTimer();
        showToast(getSessionCompleteMessage());
    }

    private void setSessionType(SessionType sessionType) {
        currentSessionType = sessionType;
        resetTimer();
        updateChipSelection();
        updateSessionInfo();
    }

    private long getTimeForCurrentSession() {
        switch (currentSessionType) {
            case WORK:
                return WORK_TIME;
            case SHORT_BREAK:
                return SHORT_BREAK_TIME;
            case LONG_BREAK:
                return LONG_BREAK_TIME;
            default:
                return WORK_TIME;
        }
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        tvTimeRemaining.setText(timeFormatted);
    }

    private void updateProgress() {
        if (initialTimeInMillis > 0) {
            int progress = (int) (((initialTimeInMillis - timeLeftInMillis) * 100) / initialTimeInMillis);
            progressCircle.setProgress(progress);

            // Animate progress
            ObjectAnimator.ofInt(progressCircle, "progress", progressCircle.getProgress(), progress)
                    .setDuration(300)
                    .start();
        }
    }

    private void updateUI() {
        switch (timerState) {
            case STOPPED:
                btnStartPause.setText("START");
                btnStartPause.setEnabled(true);
                btnReset.setEnabled(true);
                enableChips(true);
                break;
            case RUNNING:
                btnStartPause.setText("PAUSE");
                btnStartPause.setEnabled(true);
                btnReset.setEnabled(true);
                enableChips(false);
                break;
            case PAUSED:
                btnStartPause.setText("RESUME");
                btnStartPause.setEnabled(true);
                btnReset.setEnabled(true);
                enableChips(false);
                break;
        }
    }

    private void updateChipSelection() {
        // Reset all chips
        chipWorkMode.setBackgroundResource(R.drawable.chip_unselected);
        chipWorkMode.setTextColor(getResources().getColor(android.R.color.black));
        chipShortBreak.setBackgroundResource(R.drawable.chip_unselected);
        chipShortBreak.setTextColor(getResources().getColor(android.R.color.black));
        chipLongBreak.setBackgroundResource(R.drawable.chip_unselected);
        chipLongBreak.setTextColor(getResources().getColor(android.R.color.black));

        // Highlight selected chip
        switch (currentSessionType) {
            case WORK:
                chipWorkMode.setBackgroundResource(R.drawable.chip_selected);
                chipWorkMode.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case SHORT_BREAK:
                chipShortBreak.setBackgroundResource(R.drawable.chip_selected);
                chipShortBreak.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case LONG_BREAK:
                chipLongBreak.setBackgroundResource(R.drawable.chip_selected);
                chipLongBreak.setTextColor(getResources().getColor(android.R.color.white));
                break;
        }
    }

    private void updateSessionInfo() {
        switch (currentSessionType) {
            case WORK:
                tvSessionType.setText("Focus Time");
                tvSessionCount.setText("Session " + currentSession + " of " + totalSessions);
                break;
            case SHORT_BREAK:
                tvSessionType.setText("Short Break");
                tvSessionCount.setText("Take a break!");
                break;
            case LONG_BREAK:
                tvSessionType.setText("Long Break");
                tvSessionCount.setText("Well deserved rest!");
                break;
        }
    }

    private void enableChips(boolean enabled) {
        chipWorkMode.setEnabled(enabled);
        chipShortBreak.setEnabled(enabled);
        chipLongBreak.setEnabled(enabled);

        float alpha = enabled ? 1.0f : 0.5f;
        chipWorkMode.setAlpha(alpha);
        chipShortBreak.setAlpha(alpha);
        chipLongBreak.setAlpha(alpha);
    }

    private void incrementSessionCount() {
        if (prefs != null) {
            int todayCount = prefs.getInt(KEY_TODAY_COUNT, 0);
            int totalCount = prefs.getInt(KEY_TOTAL_COUNT, 0);

            prefs.edit()
                    .putInt(KEY_TODAY_COUNT, todayCount + 1)
                    .putInt(KEY_TOTAL_COUNT, totalCount + 1)
                    .apply();

            updateStatistics();
        }
    }

    private void updateStatistics() {
        if (prefs != null) {
            int todayCount = prefs.getInt(KEY_TODAY_COUNT, 0);
            int totalCount = prefs.getInt(KEY_TOTAL_COUNT, 0);

            tvTodayCount.setText(String.valueOf(todayCount));
            tvTotalCount.setText(String.valueOf(totalCount));
        }
    }

    private String getSessionCompleteMessage() {
        switch (currentSessionType) {
            case WORK:
                return "Work session complete! Time for a break.";
            case SHORT_BREAK:
                return "Break over! Ready to focus?";
            case LONG_BREAK:
                return "Long break complete! Let's get back to work.";
            default:
                return "Session complete!";
        }
    }

    private void playNotificationSound() {
        try {
            if (getContext() != null) {
                MediaPlayer mp = MediaPlayer.create(getContext(), android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);
                if (mp != null) {
                    mp.start();
                    mp.setOnCompletionListener(MediaPlayer::release);
                }
            }
        } catch (Exception e) {
            // Handle error silently
        }
    }

    private void vibrate() {
        try {
            if (getContext() != null) {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(1000); // Vibrate for 1 second
                }
            }
        } catch (Exception e) {
            // Handle error silently
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save current state when app goes to background
        if (prefs != null) {
            prefs.edit()
                    .putLong("saved_time", timeLeftInMillis)
                    .putString("saved_state", timerState.name())
                    .putString("saved_session_type", currentSessionType.name())
                    .putInt("saved_session", currentSession)
                    .apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restore state when app comes back to foreground
        if (prefs != null) {
            timeLeftInMillis = prefs.getLong("saved_time", WORK_TIME);
            String savedState = prefs.getString("saved_state", TimerState.STOPPED.name());
            String savedSessionType = prefs.getString("saved_session_type", SessionType.WORK.name());
            currentSession = prefs.getInt("saved_session", 1);

            try {
                timerState = TimerState.valueOf(savedState);
                currentSessionType = SessionType.valueOf(savedSessionType);
            } catch (IllegalArgumentException e) {
                timerState = TimerState.STOPPED;
                currentSessionType = SessionType.WORK;
            }

            initialTimeInMillis = getTimeForCurrentSession();
            updateTimerDisplay();
            updateProgress();
            updateUI();
            updateChipSelection();
            updateSessionInfo();
        }
    }
}