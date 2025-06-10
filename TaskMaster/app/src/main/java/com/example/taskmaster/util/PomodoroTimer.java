package com.example.taskmaster.util;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

/**
 * A utility class for managing Pomodoro timer functionality.
 * Provides countdown functionality with pause and resume capabilities.
 */
public class PomodoroTimer {

    private long millisInFuture;
    private final long countDownInterval;
    private CountDownTimer countDownTimer;
    private long timeRemaining;
    private boolean isPaused = true;

    // Callback interfaces for timer events
    private OnTickListener onTickListener;
    private OnFinishListener onFinishListener;

    /**
     * Creates a new Pomodoro timer.
     *
     * @param millisInFuture The total duration of the timer in milliseconds
     * @param countDownInterval The interval at which the timer ticks in milliseconds
     */
    public PomodoroTimer(long millisInFuture, long countDownInterval) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
        this.timeRemaining = millisInFuture;
    }

    /**
     * Starts or resumes the timer.
     */
    public void start() {
        if (isPaused) {
            createAndStartTimer();
            isPaused = false;
        }
    }

    /**
     * Pauses the timer.
     */
    public void pause() {
        if (!isPaused) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            isPaused = true;
        }
    }

    /**
     * Cancels the timer and resets the remaining time to the initial value.
     */
    public void cancel() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isPaused = true;
        timeRemaining = millisInFuture;
    }

    /**
     * Sets a new duration for the timer.
     *
     * @param millisInFuture The new duration in milliseconds
     */
    public void setDuration(long millisInFuture) {
        this.millisInFuture = millisInFuture;
        this.timeRemaining = millisInFuture;
    }

    /**
     * Gets the current time remaining on the timer.
     *
     * @return The time remaining in milliseconds
     */
    public long getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Creates and starts a new CountDownTimer with the current remaining time.
     */
    private void createAndStartTimer() {
        countDownTimer = new CountDownTimer(timeRemaining, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                if (onTickListener != null) {
                    // Use Handler to ensure callback runs on main thread
                    new Handler(Looper.getMainLooper()).post(() ->
                            onTickListener.onTick(millisUntilFinished));
                }
            }

            @Override
            public void onFinish() {
                timeRemaining = 0;
                isPaused = true;
                if (onFinishListener != null) {
                    // Use Handler to ensure callback runs on main thread
                    new Handler(Looper.getMainLooper()).post(() ->
                            onFinishListener.onFinish());
                }
            }
        };

        countDownTimer.start();
    }

    /**
     * Sets a listener to be called on each timer tick.
     *
     * @param listener The OnTickListener to notify
     */
    public void setOnTickListener(OnTickListener listener) {
        this.onTickListener = listener;
    }

    /**
     * Sets a listener to be called when the timer finishes.
     *
     * @param listener The OnFinishListener to notify
     */
    public void setOnFinishListener(OnFinishListener listener) {
        this.onFinishListener = listener;
    }

    /**
     * Interface definition for callbacks to be invoked when the timer ticks.
     */
    public interface OnTickListener {
        /**
         * Called on each timer tick.
         *
         * @param millisUntilFinished The amount of time until finished in milliseconds
         */
        void onTick(long millisUntilFinished);
    }

    /**
     * Interface definition for a callback to be invoked when the timer finishes.
     */
    public interface OnFinishListener {
        /**
         * Called when the timer finishes.
         */
        void onFinish();
    }
}