package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.data.local.entity.AchievementEntity;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private List<AchievementEntity> achievements = new ArrayList<>();
    private OnAchievementClickListener listener;

    // Interface for click events
    public interface OnAchievementClickListener {
        void onAchievementClick(AchievementEntity achievement);
    }

    public void setOnAchievementClickListener(OnAchievementClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        AchievementEntity achievement = achievements.get(position);
        holder.bind(achievement);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAchievementClick(achievement);
            }
        });
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public void updateAchievements(List<AchievementEntity> newAchievements) {
        if (newAchievements == null) {
            // Handle null case
            this.achievements.clear();
            notifyDataSetChanged();
            return;
        }

        // Use DiffUtil for better performance and animations
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AchievementDiffCallback(this.achievements, newAchievements));

        // Update the list
        this.achievements.clear();
        this.achievements.addAll(newAchievements);

        // Apply the diff result
        diffResult.dispatchUpdatesTo(this);
    }

    // Get achievement at position (useful for click handling)
    public AchievementEntity getAchievementAt(int position) {
        if (position >= 0 && position < achievements.size()) {
            return achievements.get(position);
        }
        return null;
    }

    // Get all achievements
    public List<AchievementEntity> getAllAchievements() {
        return new ArrayList<>(achievements);
    }

    // Get unlocked achievements count
    public int getUnlockedCount() {
        int count = 0;
        for (AchievementEntity achievement : achievements) {
            if (achievement.isUnlocked()) {
                count++;
            }
        }
        return count;
    }

    // Get total experience from unlocked achievements
    public int getTotalExperience() {
        int total = 0;
        for (AchievementEntity achievement : achievements) {
            if (achievement.isUnlocked()) {
                total += achievement.getExperiencePoints();
            }
        }
        return total;
    }

    // Clear all achievements
    public void clearAchievements() {
        int size = achievements.size();
        achievements.clear();
        notifyItemRangeRemoved(0, size);
    }

    // Add single achievement (for real-time updates)
    public void addAchievement(AchievementEntity achievement) {
        if (achievement != null) {
            achievements.add(achievement);
            notifyItemInserted(achievements.size() - 1);
        }
    }

    // Update single achievement (for progress updates)
    public void updateAchievement(AchievementEntity updatedAchievement) {
        if (updatedAchievement == null) return;

        for (int i = 0; i < achievements.size(); i++) {
            AchievementEntity existing = achievements.get(i);
            if (existing.getId() == updatedAchievement.getId()) {
                achievements.set(i, updatedAchievement);
                notifyItemChanged(i);
                break;
            }
        }
    }

    // Filter achievements by type
    public void filterByType(String achievementType) {
        List<AchievementEntity> filtered = new ArrayList<>();
        for (AchievementEntity achievement : achievements) {
            if (achievementType == null || achievementType.equals(achievement.getAchievementType())) {
                filtered.add(achievement);
            }
        }
        updateAchievements(filtered);
    }

    // Filter unlocked achievements only
    public void showOnlyUnlocked(boolean unlockedOnly) {
        if (!unlockedOnly) {
            // Show all achievements - would need original list
            return;
        }

        List<AchievementEntity> unlocked = new ArrayList<>();
        for (AchievementEntity achievement : achievements) {
            if (achievement.isUnlocked()) {
                unlocked.add(achievement);
            }
        }
        updateAchievements(unlocked);
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {

        private TextView tvIcon, tvTitle, tvDescription, tvProgress, tvExperience;
        private ProgressBar progressBar;
        private View lockOverlay;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvAchievementIcon);
            tvTitle = itemView.findViewById(R.id.tvAchievementTitle);
            tvDescription = itemView.findViewById(R.id.tvAchievementDescription);
            tvProgress = itemView.findViewById(R.id.tvAchievementProgress);
            tvExperience = itemView.findViewById(R.id.tvAchievementExperience);
            progressBar = itemView.findViewById(R.id.progressAchievement);
            lockOverlay = itemView.findViewById(R.id.lockOverlay);
        }

        public void bind(AchievementEntity achievement) {
            // Set icon with fallback
            String icon = achievement.getIcon();
            tvIcon.setText(icon != null ? icon : "🏆");

            // Set text content
            tvTitle.setText(achievement.getTitle() != null ? achievement.getTitle() : "Achievement");
            tvDescription.setText(achievement.getDescription() != null ? achievement.getDescription() : "");
            tvExperience.setText("+" + achievement.getExperiencePoints() + " XP");

            if (achievement.isUnlocked()) {
                // Achievement is unlocked
                lockOverlay.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                tvProgress.setText("✅ Completed");
                tvProgress.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark, null));

                // Add unlock date if available
                if (achievement.getUnlockDate() != null) {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                    tvProgress.setText("✅ Unlocked " + sdf.format(achievement.getUnlockDate()));
                }

                // Set visual state for unlocked
                itemView.setAlpha(1.0f);
                tvIcon.setAlpha(1.0f);

            } else {
                // Achievement is locked
                lockOverlay.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                int progressPercentage = achievement.getProgressPercentage();
                progressBar.setProgress(progressPercentage);

                String progressText = achievement.getCurrentProgress() + "/" + achievement.getRequirementValue();
                tvProgress.setText(progressText + " (" + progressPercentage + "%)");
                tvProgress.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray, null));

                // Set visual state for locked
                itemView.setAlpha(0.7f);
                tvIcon.setAlpha(0.5f);
                itemView.setBackgroundResource(0);
            }
        }

        private boolean isRecentlyUnlocked(AchievementEntity achievement) {
            if (achievement.getUnlockDate() == null) return false;

            long now = System.currentTimeMillis();
            long unlockTime = achievement.getUnlockDate().getTime();
            long dayInMillis = 24 * 60 * 60 * 1000; // 24 hours

            return (now - unlockTime) < dayInMillis; // Unlocked within last 24 hours
        }
    }

    // DiffUtil Callback for efficient updates
    private static class AchievementDiffCallback extends DiffUtil.Callback {
        private final List<AchievementEntity> oldList;
        private final List<AchievementEntity> newList;

        AchievementDiffCallback(List<AchievementEntity> oldList, List<AchievementEntity> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            AchievementEntity oldItem = oldList.get(oldItemPosition);
            AchievementEntity newItem = newList.get(newItemPosition);

            return oldItem.getId() == newItem.getId() &&
                    oldItem.isUnlocked() == newItem.isUnlocked() &&
                    oldItem.getCurrentProgress() == newItem.getCurrentProgress() &&
                    oldItem.getRequirementValue() == newItem.getRequirementValue() &&
                    java.util.Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    java.util.Objects.equals(oldItem.getDescription(), newItem.getDescription());
        }
    }
}