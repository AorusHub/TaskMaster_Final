package com.example.taskmaster.ui.dialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.example.taskmaster.R;
import com.example.taskmaster.databinding.DialogPodomoroSettingBinding;

public class PomodoroSettingsDialog extends DialogFragment {

    private DialogPodomoroSettingBinding binding;
    private SharedPreferences sharedPreferences;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogPodomoroSettingBinding.inflate(getLayoutInflater());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        loadCurrentSettings();

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.pomodoro_settings)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    saveSettings();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dismiss();
                })
                .create();
    }

    private void loadCurrentSettings() {
        // Work duration
        int workDuration = sharedPreferences.getInt("pomodoro_work_duration", 25);
        binding.seekBarWorkDuration.setProgress(workDuration - 5); // Adjust for minimum value
        binding.tvWorkDurationValue.setText(workDuration + " min");

        // Short break duration
        int shortBreakDuration = sharedPreferences.getInt("pomodoro_short_break_duration", 5);
        binding.seekBarShortBreakDuration.setProgress(shortBreakDuration - 1); // Adjust for minimum value
        binding.tvShortBreakDurationValue.setText(shortBreakDuration + " min");

        // Long break duration
        int longBreakDuration = sharedPreferences.getInt("pomodoro_long_break_duration", 15);
        binding.seekBarLongBreakDuration.setProgress(longBreakDuration - 5); // Adjust for minimum value
        binding.tvLongBreakDurationValue.setText(longBreakDuration + " min");

        // Sessions before long break
        int sessionsCount = sharedPreferences.getInt("pomodoro_sessions_before_long_break", 4);
        binding.seekBarSessionsCount.setProgress(sessionsCount - 2); // Adjust for minimum value
        binding.tvSessionsCountValue.setText(String.valueOf(sessionsCount));

        setupSeekBarListeners();
    }

    private void setupSeekBarListeners() {
        // Work duration listener
        binding.seekBarWorkDuration.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 5; // 5 min minimum
                binding.tvWorkDurationValue.setText(value + " min");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });

        // Short break duration listener
        binding.seekBarShortBreakDuration.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 1; // 1 min minimum
                binding.tvShortBreakDurationValue.setText(value + " min");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });

        // Long break duration listener
        binding.seekBarLongBreakDuration.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 5; // 5 min minimum
                binding.tvLongBreakDurationValue.setText(value + " min");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });

        // Sessions count listener
        binding.seekBarSessionsCount.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 2; // Minimum 2 sessions
                binding.tvSessionsCountValue.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save work duration
        int workDuration = binding.seekBarWorkDuration.getProgress() + 5;
        editor.putInt("pomodoro_work_duration", workDuration);

        // Save short break duration
        int shortBreakDuration = binding.seekBarShortBreakDuration.getProgress() + 1;
        editor.putInt("pomodoro_short_break_duration", shortBreakDuration);

        // Save long break duration
        int longBreakDuration = binding.seekBarLongBreakDuration.getProgress() + 5;
        editor.putInt("pomodoro_long_break_duration", longBreakDuration);

        // Save sessions count
        int sessionsCount = binding.seekBarSessionsCount.getProgress() + 2;
        editor.putInt("pomodoro_sessions_before_long_break", sessionsCount);

        editor.apply();

        Toast.makeText(requireContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}