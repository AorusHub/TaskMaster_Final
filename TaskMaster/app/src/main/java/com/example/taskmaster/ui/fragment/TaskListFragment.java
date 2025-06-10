package com.example.taskmaster.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.adapter.TaskAdapter;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.viewmodel.TaskViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TaskViewModel taskViewModel;
    private TabLayout tabLayout;
    private TextView tvDate;
    private TextView tvEmptyState;
    private List<Task> allTasks = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    // Category filter components
    private Spinner spinnerCategoryFilter;
    private ChipGroup chipGroupTags;
    private String selectedCategory = "All";
    private List<String> selectedTags = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupTabLayout();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        tabLayout = view.findViewById(R.id.tab_layout);
        tvDate = view.findViewById(R.id.tv_date);
        tvEmptyState = view.findViewById(R.id.text_empty_tasks);

        // Category and tag filter views (optional - only if they exist in layout)
        spinnerCategoryFilter = view.findViewById(R.id.spinner_category_filter);
        chipGroupTags = view.findViewById(R.id.chip_group_tags);

        // Set current date
        if (tvDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
            tvDate.setText(dateFormat.format(new Date()));
        }

        // Setup filters if they exist
        if (spinnerCategoryFilter != null) {
            setupCategoryFilter();
        }
        if (chipGroupTags != null) {
            setupTagFilter();
        }
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnTaskClickListener(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                showTaskDetailsDialog(task);
            }

            @Override
            public void onTaskLongClick(Task task) {
                showDeleteConfirmation(task.getId());
            }
        });
    }

    private void setupViewModel() {
        if (getActivity() != null) {
            taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

            taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), taskEntities -> {
                if (taskEntities != null) {
                    allTasks = convertToTaskList(taskEntities);
                    filterTasks();
                    updateEmptyState(allTasks.isEmpty());
                } else {
                    if (getView() != null) {
                        Snackbar.make(getView(), "Failed to load tasks", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setupTabLayout() {
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    filterTasks();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
    }

    private void setupCategoryFilter() {
        // Only setup if TaskViewModel has the method
        try {
            taskViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
                if (categories != null) {
                    List<String> categoryList = new ArrayList<>();
                    categoryList.add("All");
                    categoryList.addAll(categories);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            categoryList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategoryFilter.setAdapter(adapter);

                    spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategory = categoryList.get(position);
                            filterTasks();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            });
        } catch (Exception e) {
            // Hide spinner if method doesn't exist
            if (spinnerCategoryFilter != null) {
                spinnerCategoryFilter.setVisibility(View.GONE);
            }
        }
    }

    private void setupTagFilter() {
        updateTagChips();
    }

    private void updateTagChips() {
        if (chipGroupTags == null) return;

        chipGroupTags.removeAllViews();

        Set<String> allTags = new HashSet<>();
        for (Task task : allTasks) {
            if (task.getTags() != null && !task.getTags().isEmpty()) {
                String[] taskTags = task.getTags().split(",");
                for (String tag : taskTags) {
                    allTags.add(tag.trim());
                }
            }
        }

        for (String tag : allTags) {
            Chip chip = new Chip(getContext());
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChecked(selectedTags.contains(tag));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
                filterTasks();
            });
            chipGroupTags.addView(chip);
        }
    }

    private void filterTasks() {
        if (taskAdapter == null) return;

        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : allTasks) {
            boolean passesFilter = true;

            // Category filter
            if (!selectedCategory.equals("All")) {
                if (!selectedCategory.equals(task.getCategory())) {
                    passesFilter = false;
                }
            }

            // Tag filter
            if (!selectedTags.isEmpty()) {
                boolean hasMatchingTag = false;
                if (task.getTags() != null) {
                    String[] taskTags = task.getTags().split(",");
                    for (String taskTag : taskTags) {
                        if (selectedTags.contains(taskTag.trim())) {
                            hasMatchingTag = true;
                            break;
                        }
                    }
                }
                if (!hasMatchingTag) {
                    passesFilter = false;
                }
            }

            // Tab filter
            if (tabLayout != null) {
                int selectedTabPosition = tabLayout.getSelectedTabPosition();
                switch (selectedTabPosition) {
                    case 0: // All tasks
                        break;
                    case 1: // Active tasks
                        if (task.isCompleted()) passesFilter = false;
                        break;
                    case 2: // Completed tasks
                        if (!task.isCompleted()) passesFilter = false;
                        break;
                }
            }

            if (passesFilter) {
                filteredTasks.add(task);
            }
        }

        taskAdapter.updateTasks(filteredTasks);
        updateEmptyState(filteredTasks.isEmpty());
    }

    private List<Task> convertToTaskList(List<TaskEntity> taskEntities) {
        List<Task> tasks = new ArrayList<>();
        for (TaskEntity entity : taskEntities) {
            Task task = new Task();
            task.setId(entity.getId());
            task.setTitle(entity.getTitle());
            task.setDescription(entity.getDescription());
            task.setPriority(convertPriorityToString(entity.getPriority()));
            task.setCompleted(entity.isCompleted());

            if (entity.getDueDate() != null) {
                task.setDueDate(convertDateToString(entity.getDueDate()));
            } else {
                task.setDueDate("No due date");
            }

            // Set new fields with null checks
            task.setCategory(entity.getCategory() != null ? entity.getCategory() : "General");
            task.setTags(entity.getTags());
            task.setRecurring(entity.isRecurring());
            task.setRecurrencePattern(entity.getRecurrencePattern());
            task.setEstimatedTimeMinutes(entity.getEstimatedTimeMinutes());
            task.setActualTimeMinutes(entity.getActualTimeMinutes());
            task.setColorTag(entity.getColorTag());

            tasks.add(task);
        }
        return tasks;
    }

    private String convertPriorityToString(int priority) {
        switch (priority) {
            case 1: return "Low";
            case 2: return "Medium";
            case 3: return "High";
            default: return "Medium";
        }
    }

    private String convertDateToString(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
        return "No due date";
    }

    private void updateEmptyState(boolean isEmpty) {
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void showTaskDetailsDialog(Task task) {
        if (getContext() == null || task == null) return;

        Dialog taskDialog = new Dialog(getContext());
        taskDialog.setContentView(R.layout.dialog_task_details);

        Window window = taskDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = taskDialog.findViewById(R.id.tv_task_title);
        TextView tvDescription = taskDialog.findViewById(R.id.tv_task_description);
        TextView tvDueDate = taskDialog.findViewById(R.id.tv_task_due_date);
        TextView tvPriority = taskDialog.findViewById(R.id.tv_task_priority);
        TextView tvCategory = taskDialog.findViewById(R.id.tv_task_category);
        Button btnComplete = taskDialog.findViewById(R.id.btn_mark_complete);
        Button btnDelete = taskDialog.findViewById(R.id.btn_delete_task);
        Button btnClose = taskDialog.findViewById(R.id.btn_close);

        if (tvTitle != null) tvTitle.setText(task.getTitle());
        if (tvDescription != null) tvDescription.setText(task.getDescription());
        if (tvDueDate != null) tvDueDate.setText("Due: " + task.getDueDate());
        if (tvPriority != null) tvPriority.setText("Priority: " + task.getPriority());
        if (tvCategory != null) tvCategory.setText("Category: " + task.getCategory());

        if (btnComplete != null) {
            btnComplete.setOnClickListener(v -> {
                markTaskAsCompleted(task.getId());
                taskDialog.dismiss();
            });
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                showDeleteConfirmation(task.getId());
                taskDialog.dismiss();
            });
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> taskDialog.dismiss());
        }

        taskDialog.show();
    }

    private void showDeleteConfirmation(long taskId) {
        if (getContext() == null) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (taskViewModel != null) {
                        taskViewModel.deleteTask((int) taskId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void markTaskAsCompleted(long taskId) {
        executor.execute(() -> {
            // Find task and mark as completed
            for (Task task : allTasks) {
                if (task.getId() == taskId) {
                    task.setCompleted(true);

                    // Convert back to TaskEntity and update
                    TaskEntity entity = convertToTaskEntity(task);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (taskViewModel != null) {
                                taskViewModel.updateTask(entity);
                            }
                        });
                    }
                    break;
                }
            }
        });
    }

    private TaskEntity convertToTaskEntity(Task task) {
        TaskEntity entity = new TaskEntity();
        entity.setId((int) task.getId());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setPriority(convertPriorityToInt(task.getPriority()));
        entity.setCompleted(task.isCompleted());
        entity.setCategory(task.getCategory());
        entity.setTags(task.getTags());
        entity.setRecurring(task.isRecurring());
        entity.setRecurrencePattern(task.getRecurrencePattern());
        entity.setEstimatedTimeMinutes(task.getEstimatedTimeMinutes());
        entity.setActualTimeMinutes(task.getActualTimeMinutes());
        entity.setColorTag(task.getColorTag());
        entity.setUpdatedAt(new Date());
        return entity;
    }

    private int convertPriorityToInt(String priority) {
        switch (priority.toLowerCase()) {
            case "low": return 1;
            case "medium": return 2;
            case "high": return 3;
            default: return 2;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}