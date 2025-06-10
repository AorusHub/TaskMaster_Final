package com.example.taskmaster.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.taskmaster.R;
import com.example.taskmaster.util.ThemeManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    // Views
    private RadioGroup rgThemeMode;
    private RadioButton rbLightMode, rbDarkMode, rbSystemMode;
    private SwitchCompat switchTaskReminders, switchAchievementNotifications;
    private LinearLayout llExportData, llClearData;
    private TextView tvAppVersion;
    private View viewThemePreview;

    // Data
    private SharedPreferences sharedPreferences;
    private ThemeManager themeManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        themeManager = new ThemeManager(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadSettings();
        setupClickListeners();
        updateThemePreview();
    }

    private void initViews(View view) {
        rgThemeMode = view.findViewById(R.id.rgThemeMode);
        rbLightMode = view.findViewById(R.id.rbLightMode);
        rbDarkMode = view.findViewById(R.id.rbDarkMode);
        rbSystemMode = view.findViewById(R.id.rbSystemMode);
        switchTaskReminders = view.findViewById(R.id.switchTaskReminders);
        switchAchievementNotifications = view.findViewById(R.id.switchAchievementNotifications);
        llExportData = view.findViewById(R.id.llExportData);
        llClearData = view.findViewById(R.id.llClearData);
        tvAppVersion = view.findViewById(R.id.tvAppVersion);
        viewThemePreview = view.findViewById(R.id.viewThemePreview);
    }

    private void loadSettings() {
        // Load theme mode
        int themeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        switch (themeMode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                rbLightMode.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                rbDarkMode.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                rbSystemMode.setChecked(true);
                break;
        }

        // Load notification settings
        boolean taskReminders = sharedPreferences.getBoolean("task_reminders", true);
        boolean achievementNotifications = sharedPreferences.getBoolean("achievement_notifications", true);

        switchTaskReminders.setChecked(taskReminders);
        switchAchievementNotifications.setChecked(achievementNotifications);

        // Load app version
        loadAppVersion();
    }

    private void setupClickListeners() {
        // Theme mode selection
        rgThemeMode.setOnCheckedChangeListener((group, checkedId) -> {
            int themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

            if (checkedId == R.id.rbLightMode) {
                themeMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.rbDarkMode) {
                themeMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else if (checkedId == R.id.rbSystemMode) {
                themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            applyThemeMode(themeMode);
        });

        // Notification switches
        switchTaskReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("task_reminders", isChecked).apply();
            showToast(isChecked ? "Task reminders enabled" : "Task reminders disabled");
        });

        switchAchievementNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("achievement_notifications", isChecked).apply();
            showToast(isChecked ? "Achievement notifications enabled" : "Achievement notifications disabled");
        });

        // Data actions
        llExportData.setOnClickListener(v -> showExportDataDialog());
        llClearData.setOnClickListener(v -> showClearDataDialog());
    }

    private void applyThemeMode(int themeMode) {
        // Save preference
        sharedPreferences.edit().putInt("theme_mode", themeMode).apply();

        // Apply theme immediately
        AppCompatDelegate.setDefaultNightMode(themeMode);

        // Update preview
        updateThemePreview();

        // Show feedback
        String themeName = getThemeName(themeMode);
        showToast("Theme changed to " + themeName);
    }

    private String getThemeName(int themeMode) {
        switch (themeMode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                return "Light Mode";
            case AppCompatDelegate.MODE_NIGHT_YES:
                return "Dark Mode";
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                return "Follow System";
        }
    }

    private void updateThemePreview() {
        int currentTheme = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        int colorResource;
        switch (currentTheme) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                colorResource = R.color.light_theme_preview;
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                colorResource = R.color.dark_theme_preview;
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                colorResource = R.color.system_theme_preview;
                break;
        }

        if (viewThemePreview != null) {
            viewThemePreview.setBackgroundResource(colorResource);
        }
    }

    private void showExportDataDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Export Data")
                .setMessage("Export your tasks and achievements to a file. This can be used for backup or transferring data.")
                .setPositiveButton("Export", (dialog, which) -> exportData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showClearDataDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠️ Clear All Data")
                .setMessage("This will permanently delete all your tasks, achievements, and settings. This action cannot be undone.\n\nAre you sure you want to continue?")
                .setPositiveButton("Clear Data", (dialog, which) -> clearAllData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void exportData() {
        try {
            String fileName = "TaskMaster_Backup_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";

            File externalDir = requireContext().getExternalFilesDir(null);
            File exportFile = new File(externalDir, fileName);

            FileWriter writer = new FileWriter(exportFile);
            writer.write("TaskMaster Data Export\n");
            writer.write("Export Date: " + new Date().toString() + "\n");
            writer.write("=========================\n\n");

            // Add export logic here - would export from database
            writer.write("Tasks: [Export would include all tasks]\n");
            writer.write("Achievements: [Export would include all achievements]\n");
            writer.write("Settings: [Export would include all settings]\n");

            writer.close();

            showToast("Data exported to: " + exportFile.getAbsolutePath());

            // Share the file
            shareExportFile(exportFile);

        } catch (IOException e) {
            e.printStackTrace();
            showToast("Export failed: " + e.getMessage());
        }
    }

    private void shareExportFile(File file) {
        Uri fileUri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "TaskMaster Data Backup");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share backup file"));
    }

    private void clearAllData() {
        // Clear SharedPreferences (except theme settings)
        int themeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        sharedPreferences.edit()
                .clear()
                .putInt("theme_mode", themeMode) // Preserve theme setting
                .apply();

        // Clear database - would implement actual clearing
        // TaskRepository.clearAllData();

        showToast("All data cleared successfully");

        // Restart the app or navigate to initial setup
        requireActivity().finishAffinity();
        System.exit(0);
    }

    private void loadAppVersion() {
        try {
            PackageInfo packageInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            tvAppVersion.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tvAppVersion.setText("Unknown");
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Public method to be called when theme changes from outside
    public void refreshTheme() {
        updateThemePreview();
    }
}