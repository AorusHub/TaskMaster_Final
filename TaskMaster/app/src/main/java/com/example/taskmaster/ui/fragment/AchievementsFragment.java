package com.example.taskmaster.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.data.local.entity.UserProgressEntity;
import com.example.taskmaster.adapter.AchievementAdapter;
import com.example.taskmaster.adapter.RecentActivityAdapter;
import com.example.taskmaster.viewmodel.AchievementViewModel;

public class AchievementsFragment extends Fragment {

    private TextView tvUserName, tvTotalTasks, tvCompletedTasks, tvSuccessRate;
    private TextView tvCurrentLevel, tvCurrentExp, tvNeededExp, tvLevelDescription;
    private ProgressBar progressLevel;
    private RecyclerView rvAchievements, rvRecentActivities;

    private AchievementViewModel achievementViewModel;
    private AchievementAdapter achievementAdapter;
    private RecentActivityAdapter recentActivityAdapter;

    private String currentUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get current user ID from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        currentUserId = prefs.getString("current_user_id", "default_user");

        achievementViewModel = new ViewModelProvider(this).get(AchievementViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerViews();
        observeData();

        // Initialize user progress if not exists
        achievementViewModel.initializeUserProgress(currentUserId, getUserName());
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvTotalTasks = view.findViewById(R.id.tvTotalTasks);
        tvCompletedTasks = view.findViewById(R.id.tvCompletedTasks);
        tvSuccessRate = view.findViewById(R.id.tvSuccessRate);
        tvCurrentLevel = view.findViewById(R.id.tvCurrentLevel);
        tvCurrentExp = view.findViewById(R.id.tvCurrentExp);
        tvNeededExp = view.findViewById(R.id.tvNeededExp);
        tvLevelDescription = view.findViewById(R.id.tvLevelDescription);
        progressLevel = view.findViewById(R.id.progressLevel);
        rvAchievements = view.findViewById(R.id.rvAchievements);
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
    }

    private void setupRecyclerViews() {
        // Achievement RecyclerView
        achievementAdapter = new AchievementAdapter();
        rvAchievements.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAchievements.setAdapter(achievementAdapter);

        // Recent Activities RecyclerView
        recentActivityAdapter = new RecentActivityAdapter();
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentActivities.setAdapter(recentActivityAdapter);
    }

    private void observeData() {
        // Observe user progress
        achievementViewModel.getUserProgress(currentUserId).observe(getViewLifecycleOwner(),
                userProgress -> {
                    if (userProgress != null) {
                        updateUserProgressUI(userProgress);
                    }
                });

        // Observe achievements
        achievementViewModel.getUserAchievements(currentUserId).observe(getViewLifecycleOwner(),
                achievements -> {
                    if (achievements != null) {
                        achievementAdapter.updateAchievements(achievements);
                    }
                });

        // Observe recent activities
        achievementViewModel.getRecentActivities(currentUserId).observe(getViewLifecycleOwner(),
                activities -> {
                    if (activities != null) {
                        recentActivityAdapter.updateActivities(activities);
                    }
                });
    }

    private void updateUserProgressUI(UserProgressEntity userProgress) {
        // Update user name
        tvUserName.setText("Welcome back, " + userProgress.getUsername() + "!");

        // Update statistics
        tvTotalTasks.setText(String.valueOf(userProgress.getTotalTasksCreated()));
        tvCompletedTasks.setText(String.valueOf(userProgress.getTotalTasksCompleted()));
        tvSuccessRate.setText(String.format("%.1f%%", userProgress.getSuccessRate()));

        // Update level information
        tvCurrentLevel.setText("Level " + userProgress.getLevel());
        tvCurrentExp.setText(String.valueOf(userProgress.getCurrentLevelExp()));
        tvNeededExp.setText(String.valueOf(userProgress.getExpForNextLevel()));

        // Update progress bar
        int progressPercentage = (userProgress.getCurrentLevelExp() * 100) / userProgress.getExpForNextLevel();
        progressLevel.setProgress(progressPercentage);

        // Update level description
        updateLevelDescription(userProgress);
    }

    private void updateLevelDescription(UserProgressEntity userProgress) {
        String description;
        int expNeeded = userProgress.getExpNeededForNextLevel();

        if (expNeeded <= 0) {
            description = "🎉 You can level up! Complete more tasks to advance!";
        } else {
            description = String.format("Complete %d more tasks to reach Level %d!",
                    expNeeded / 10, userProgress.getLevel() + 1);
        }

        tvLevelDescription.setText(description);
    }

    private String getUserName() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return prefs.getString("username", "User");
    }

    public void onTaskCreated() {
        achievementViewModel.onTaskCreated(currentUserId);
    }

    public void onTaskCompleted() {
        achievementViewModel.onTaskCompleted(currentUserId);
    }

    public void onStreakUpdated(int streak) {
        achievementViewModel.onStreakUpdated(currentUserId, streak);
    }
}