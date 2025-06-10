package com.example.taskmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.model.RecentActivity;

import java.util.ArrayList;
import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.RecentActivityViewHolder> {

    private List<RecentActivity> activities = new ArrayList<>();

    @NonNull
    @Override
    public RecentActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_item_activity, parent, false);
        return new RecentActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentActivityViewHolder holder, int position) {
        RecentActivity activity = activities.get(position);
        holder.bind(activity);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void updateActivities(List<RecentActivity> newActivities) {
        this.activities = newActivities;
        notifyDataSetChanged();
    }

    static class RecentActivityViewHolder extends RecyclerView.ViewHolder {

        private TextView tvIcon, tvTitle, tvDescription, tvTime, tvExperience;

        public RecentActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvActivityIcon);
            tvTitle = itemView.findViewById(R.id.tvActivityTitle);
            tvDescription = itemView.findViewById(R.id.tvActivityDescription);
            tvTime = itemView.findViewById(R.id.tvActivityTime);
            tvExperience = itemView.findViewById(R.id.tvActivityExperience);
        }

        public void bind(RecentActivity activity) {
            tvIcon.setText(activity.getIcon());
            tvTitle.setText(activity.getTitle());
            tvDescription.setText(activity.getDescription());
            tvTime.setText(activity.getFormattedTime());

            if (activity.getExperienceGained() > 0) {
                tvExperience.setText("+" + activity.getExperienceGained() + " XP");
                tvExperience.setVisibility(View.VISIBLE);
            } else {
                tvExperience.setVisibility(View.GONE);
            }
        }
    }
}