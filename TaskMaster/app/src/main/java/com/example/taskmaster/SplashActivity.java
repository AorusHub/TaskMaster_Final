package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmaster.util.ThemeManager;

/**
 * Splash screen activity that displays the app logo and name
 * before transitioning to the main activity.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize UI elements
        ImageView logoImage = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.splash_app_name);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Apply animations
        logoImage.startAnimation(fadeIn);
        appName.startAnimation(slideUp);

        // Navigate to MainActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the splash activity
        }, SPLASH_DELAY);
    }
}