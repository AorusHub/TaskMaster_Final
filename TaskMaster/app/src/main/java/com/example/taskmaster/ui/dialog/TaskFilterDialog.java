package com.example.taskmaster.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.taskmaster.R;
import com.example.taskmaster.databinding.DialogTaskFilterBinding;

public class TaskFilterDialog extends DialogFragment {

    private DialogTaskFilterBinding binding;
    private TaskFilterListener listener;

    // Filter options
    private String selectedPriority = "all";
    private String selectedCategory = "all";
    private String selectedStatus = "all";
    private String selectedSortBy = "due_date";

    public interface TaskFilterListener {
        void onFiltersApplied(String priority, String category, String status, String sortBy);
    }

    public void setTaskFilterListener(TaskFilterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogTaskFilterBinding.inflate(LayoutInflater.from(getContext()));

        setupPriorityFilter();
        setupCategoryFilter();
        setupStatusFilter();
        setupSortingOptions();

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.filter_tasks)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.apply, (dialog, which) -> {
                    applyFilters();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dismiss();
                })
                .setNeutralButton(R.string.reset, (dialog, which) -> {
                    resetFilters();
                })
                .create();
    }

    private void setupPriorityFilter() {
        binding.radioPriorityAll.setOnClickListener(v -> selectedPriority = "all");
        binding.radioPriorityHigh.setOnClickListener(v -> selectedPriority = "high");
        binding.radioPriorityMedium.setOnClickListener(v -> selectedPriority = "medium");
        binding.radioPriorityLow.setOnClickListener(v -> selectedPriority = "low");

        binding.radioPriorityAll.setChecked(true);
    }

    private void setupCategoryFilter() {
        String[] categories = {"All Categories", "Work", "Personal", "Study", "Health", "Shopping"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setSelection(0);
    }

    private void setupStatusFilter() {
        binding.radioStatusAll.setOnClickListener(v -> selectedStatus = "all");
        binding.radioStatusCompleted.setOnClickListener(v -> selectedStatus = "completed");
        binding.radioStatusPending.setOnClickListener(v -> selectedStatus = "pending");

        binding.radioStatusAll.setChecked(true);
    }

    private void setupSortingOptions() {
        binding.radioSortDueDate.setOnClickListener(v -> selectedSortBy = "due_date");
        binding.radioSortCreatedDate.setOnClickListener(v -> selectedSortBy = "created_date");
        binding.radioSortPriority.setOnClickListener(v -> selectedSortBy = "priority");

        binding.radioSortDueDate.setChecked(true);
    }

    private void applyFilters() {
        if (listener != null) {
            String category = "all";
            if (binding.spinnerCategory.getSelectedItemPosition() > 0) {
                category = binding.spinnerCategory.getSelectedItem().toString();
            }

            listener.onFiltersApplied(selectedPriority, category, selectedStatus, selectedSortBy);
        }
    }

    private void resetFilters() {
        selectedPriority = "all";
        selectedCategory = "all";
        selectedStatus = "all";
        selectedSortBy = "due_date";

        if (listener != null) {
            listener.onFiltersApplied(selectedPriority, selectedCategory, selectedStatus, selectedSortBy);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}