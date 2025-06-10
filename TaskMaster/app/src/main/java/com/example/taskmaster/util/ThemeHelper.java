package com.example.taskmaster.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.taskmaster.R;

/**
 * Helper class to manage app themes.
 */
public class ThemeHelper {

    public static final String THEME_SYSTEM = "system";
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_BLUE = "blue";
    public static final String THEME_GREEN = "green";
    public static final String THEME_PURPLE = "purple";

    /**
     * Applies the saved theme from preferences.
     */
    public static void applyTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = sharedPreferences.getString(Constants.PREF_THEME, THEME_SYSTEM);
        applyTheme(context, themeName);
    }

    /**
     * Applies the specified theme by name.
     */
    public static void applyTheme(Context context, String themeName) {
        // First apply light/dark mode
        switch (themeName) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        // Then apply color scheme if needed
        @StyleRes int themeId = getThemeResId(themeName);
        if (themeId != 0 && context instanceof Activity) {
            context.setTheme(themeId);
        }
    }

    /**
     * Gets the resource ID for the specified theme.
     */
    private static @StyleRes int getThemeResId(String themeName) {
        switch (themeName) {
            case THEME_BLUE:
                return R.style.Theme_TaskMaster_Blue;
            case THEME_GREEN:
                return R.style.Theme_TaskMaster_Green;
            case THEME_PURPLE:
                return R.style.Theme_TaskMaster_Purple;
            default:
                return 0; // Default theme (will be handled by night mode)
        }
    }

    /**
     * Saves the theme preference.
     */
    public static void saveTheme(Context context, String themeName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREF_THEME, themeName);
        editor.apply();
    }

    /**
     * Returns the current theme name from preferences.
     */
    public static String getCurrentTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Constants.PREF_THEME, THEME_SYSTEM);
    }

    /**
     * Checks if the current theme is dark.
     */
    public static boolean isDarkTheme(Context context) {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    }
}