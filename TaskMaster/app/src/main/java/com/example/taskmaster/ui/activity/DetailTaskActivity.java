package com.example.taskmaster.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmaster.R;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.databinding.ActivityDetailTaskBinding;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.viewmodel.CalendarViewModel;
import com.example.taskmaster.viewmodel.TaskViewModel;

public class DetailTaskActivity extends AppCompatActivity {

    private ActivityDetailTaskBinding binding;
    private TaskViewModel taskViewModel;
    private CalendarViewModel calendarViewModel;
    private int taskId;
    private Task currentTask;
    private TaskEntity taskEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Task Details");

        // Get task ID from intent
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Invalid task", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize view models
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        // Observe calendar operation results
        calendarViewModel.getSuccessMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        calendarViewModel.getErrorMessage().observe(this, message -> {
            Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
        });

        // Load task details
        loadTaskDetails();

        // Setup UI interactions
        setupButtonListeners();
    }

    private void loadTaskDetails() {
        // Load task from ViewModel using taskId
        taskViewModel.getTaskById(taskId).observe(this, task -> {
            if (task != null) {
                taskEntity = task;

                // Update UI
                binding.tvTaskTitle.setText(task.getTitle());
                binding.tvTaskDescription.setText(task.getDescription());
                binding.cbTaskCompleted.setChecked(task.isCompleted());

                // Update add/sync calendar button text based on whether task has a calendar event
                updateCalendarButtonText();
            }
        });
    }

    private void updateCalendarButtonText() {
        if (taskEntity != null) {
            boolean hasCalendarEvent = calendarViewModel.taskHasCalendarEvent(taskEntity);
            binding.btnAddToCalendar.setText(hasCalendarEvent ? "Update Calendar" : "Add to Calendar");
        }
    }

    private void setupButtonListeners() {
        binding.btnEditTask.setOnClickListener(v -> {
            // Launch edit task screen
            // Add your implementation here
            Toast.makeText(this, "Edit task clicked", Toast.LENGTH_SHORT).show();
        });

        binding.btnDeleteTask.setOnClickListener(v -> {
            // Delete task
            // Add your implementation here
            Toast.makeText(this, "Delete task clicked", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.cbTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update task completion status
            if (taskEntity != null) {
                taskEntity.setCompleted(isChecked);
                // Save changes
                taskViewModel.updateTask(taskEntity);
            }
        });

        // Add calendar button click listener
        binding.btnAddToCalendar.setOnClickListener(v -> {
            if (taskEntity != null) {
                if (calendarViewModel.taskHasCalendarEvent(taskEntity)) {
                    // Task already has a calendar event, show options dialog
                    showCalendarOptionsDialog(taskEntity);
                } else {
                    // Add task to calendar
                    calendarViewModel.syncTaskWithCalendar(taskEntity);
                }
            } else {
                Toast.makeText(this, "Task not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCalendarOptionsDialog(TaskEntity task) {
        new AlertDialog.Builder(this)
                .setTitle("Calendar Options")
                .setItems(new String[]{"Update Event", "Remove from Calendar"}, (dialog, which) -> {
                    if (which == 0) {
                        // Update event
                        calendarViewModel.syncTaskWithCalendar(task);
                    } else {
                        // Remove event
                        calendarViewModel.removeTaskFromCalendar(task);
                        updateCalendarButtonText();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}