package com.example.taskmaster.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskLongClick(Task task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks != null ? newTasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvDueDate;
        private TextView tvPriority;
        private TextView tvCategory;
        private TextView tvTags;
        private View priorityIndicator;
        private CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvDescription = itemView.findViewById(R.id.tv_task_description);
            tvDueDate = itemView.findViewById(R.id.tv_task_due_date);
            tvPriority = itemView.findViewById(R.id.tv_task_priority);
            tvCategory = itemView.findViewById(R.id.tv_task_category);
            tvTags = itemView.findViewById(R.id.tv_task_tags);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
            cardView = itemView.findViewById(R.id.card_task);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onTaskClick(tasks.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onTaskLongClick(tasks.get(position));
                        return true;
                    }
                }
                return false;
            });
        }

        public void bind(Task task) {
            tvTitle.setText(task.getTitle());
            tvDescription.setText(task.getDescription());
            tvDueDate.setText(task.getDueDate());
            tvPriority.setText(task.getPriority());

            // NEW: Display category
            tvCategory.setText(task.getCategory());

            // NEW: Display tags
            if (task.getTags() != null && !task.getTags().isEmpty()) {
                tvTags.setText("#" + task.getTags().replace(",", " #"));
                tvTags.setVisibility(View.VISIBLE);
            } else {
                tvTags.setVisibility(View.GONE);
            }

            // Handle completed tasks
            if (task.isCompleted()) {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(Color.GRAY);
                cardView.setAlpha(0.6f);
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitle.setTextColor(Color.BLACK);
                cardView.setAlpha(1.0f);
            }

            // Set priority indicator color
            setPriorityIndicator(task.getPriority());

            // Set category color if available
            if (task.getColorTag() != null && !task.getColorTag().isEmpty()) {
                try {
                    cardView.setCardBackgroundColor(Color.parseColor(task.getColorTag()));
                } catch (Exception e) {
                    cardView.setCardBackgroundColor(Color.WHITE);
                }
            } else {
                cardView.setCardBackgroundColor(Color.WHITE);
            }
        }

        private void setPriorityIndicator(String priority) {
            if (priorityIndicator == null) return;

            switch (priority.toLowerCase()) {
                case "high":
                    priorityIndicator.setBackgroundColor(Color.RED);
                    break;
                case "medium":
                    priorityIndicator.setBackgroundColor(Color.YELLOW);
                    break;
                case "low":
                    priorityIndicator.setBackgroundColor(Color.GREEN);
                    break;
                default:
                    priorityIndicator.setBackgroundColor(Color.GRAY);
                    break;
            }
        }
    }
}