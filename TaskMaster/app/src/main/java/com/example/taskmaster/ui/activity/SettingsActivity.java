package com.example.taskmaster.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.taskmaster.R;
import com.example.taskmaster.databinding.ActivitySettingsBinding;
import com.example.taskmaster.util.NotificationHelper;
import com.example.taskmaster.util.ThemeHelper;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_settings);
        }

        // Load settings fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Initialize preferences
            setupThemePreference();
            setupNotificationPreference();
            setupPomodoroPreferences();
        }

        private void setupThemePreference() {
            ListPreference themePreference = findPreference("theme_key");
            if (themePreference != null) {
                themePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
                themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    ThemeHelper.applyTheme(requireContext(), newValue.toString());
                    return true;
                });
            }
        }

        private void setupNotificationPreference() {
            SwitchPreferenceCompat notificationPreference = findPreference("notifications_key");
            if (notificationPreference != null) {
                notificationPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = (Boolean) newValue;
                    if (enabled) {
                        NotificationHelper.createNotificationChannel(requireContext());
                    }
                    return true;
                });
            }
        }

        private void setupPomodoroPreferences() {
            Preference pomodoroWorkDuration = findPreference("pomodoro_work_duration");
            Preference pomodoroBreakDuration = findPreference("pomodoro_break_duration");

            if (pomodoroWorkDuration != null) {
                pomodoroWorkDuration.setSummaryProvider((preference) -> {
                    String duration = preference.getSharedPreferences().getString("pomodoro_work_duration", "25");
                    return duration + " minutes";
                });
            }

            if (pomodoroBreakDuration != null) {
                pomodoroBreakDuration.setSummaryProvider((preference) -> {
                    String duration = preference.getSharedPreferences().getString("pomodoro_break_duration", "5");
                    return duration + " minutes";
                });
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Handle preference changes
            if (key.equals("data_sync_frequency")) {
                Preference preference = findPreference(key);
                if (preference != null) {
                    Toast.makeText(getContext(),
                            "Sync frequency updated: " + preference.getSummary(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}