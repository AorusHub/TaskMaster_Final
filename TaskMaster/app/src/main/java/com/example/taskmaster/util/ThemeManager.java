package com.example.taskmaster.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeManager {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public static final String PREF_THEME_MODE = "theme_mode";

    public ThemeManager(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void applyTheme() {
        int themeMode = getThemeMode();
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    public int getThemeMode() {
        return sharedPreferences.getInt(PREF_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setThemeMode(int themeMode) {
        sharedPreferences.edit().putInt(PREF_THEME_MODE, themeMode).apply();
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    public boolean isDarkMode() {
        int themeMode = getThemeMode();

        switch (themeMode) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                return true;
            case AppCompatDelegate.MODE_NIGHT_NO:
                return false;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                return isSystemDarkMode();
        }
    }

    private boolean isSystemDarkMode() {
        int uiMode = context.getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public String getThemeName() {
        int themeMode = getThemeMode();

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
}