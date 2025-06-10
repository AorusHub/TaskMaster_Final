package com.example.taskmaster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmaster.R;
import com.example.taskmaster.data.local.entity.TaskEntity;
import com.example.taskmaster.util.ThemeManager;
import com.example.taskmaster.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    // Views
    private EditText etTaskTitle;
    private EditText etTaskDescription;
    private TextView categoryWork, categoryPersonal, categoryStudy, categoryHealth, categoryOther;
    private Spinner spinnerPriority;
    private EditText etEstimatedTime;
    private Button btnSelectDate, btnSelectTime;
    private TextView tvSelectedDateTime;
    private EditText etTags;
    private Button btnCancel, btnSave;

    // Data
    private TaskViewModel taskViewModel;
    private Calendar selectedDateTime;
    private String selectedCategory = "Work";
    private List<TextView> categoryViews;

    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupViewModel();
        setupSpinners();
        setupCategorySelection();
        setupClickListeners();

        selectedDateTime = Calendar.getInstance();
        updateDateTimeDisplay();
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        categoryWork = findViewById(R.id.categoryWork);
        categoryPersonal = findViewById(R.id.categoryPersonal);
        categoryStudy = findViewById(R.id.categoryStudy);
        categoryHealth = findViewById(R.id.categoryHealth);
        categoryOther = findViewById(R.id.categoryOther);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        etEstimatedTime = findViewById(R.id.etEstimatedTime);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        etTags = findViewById(R.id.etTags);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        categoryViews = new ArrayList<>();
        categoryViews.add(categoryWork);
        categoryViews.add(categoryPersonal);
        categoryViews.add(categoryStudy);
        categoryViews.add(categoryHealth);
        categoryViews.add(categoryOther);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupSpinners() {
        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
        spinnerPriority.setSelection(1); // Default to Medium
    }

    private void setupCategorySelection() {
        updateCategorySelection("Work");

        categoryWork.setOnClickListener(v -> updateCategorySelection("Work"));
        categoryPersonal.setOnClickListener(v -> updateCategorySelection("Personal"));
        categoryStudy.setOnClickListener(v -> updateCategorySelection("Study"));
        categoryHealth.setOnClickListener(v -> updateCategorySelection("Health"));
        categoryOther.setOnClickListener(v -> updateCategorySelection("Other"));
    }

    private void updateCategorySelection(String category) {
        selectedCategory = category;

        for (TextView categoryView : categoryViews) {
            categoryView.setBackgroundResource(R.drawable.category_chip_unselected);
            categoryView.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        }

        TextView selectedView = null;
        switch (category) {
            case "Work": selectedView = categoryWork; break;
            case "Personal": selectedView = categoryPersonal; break;
            case "Study": selectedView = categoryStudy; break;
            case "Health": selectedView = categoryHealth; break;
            case "Other": selectedView = categoryOther; break;
        }

        if (selectedView != null) {
            selectedView.setBackgroundResource(R.drawable.category_chip_selected);
            selectedView.setTextColor(getResources().getColor(android.R.color.white, null));
        }
    }

    private void setupClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE, dd MMM yyyy 'at' HH:mm", Locale.getDefault());
        String formattedDateTime = dateTimeFormat.format(selectedDateTime.getTime());
        tvSelectedDateTime.setText("Due: " + formattedDateTime);
    }

    private void saveTask() {
        if (!validateInput()) return;

        TaskEntity task = new TaskEntity();
        task.setTitle(etTaskTitle.getText().toString().trim());
        task.setDescription(etTaskDescription.getText().toString().trim());
        task.setDueDate(selectedDateTime.getTime());
        task.setPriority(getPriorityValue());
        task.setCompleted(false);
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());
        task.setCategory(selectedCategory);
        task.setTags(getTags());
        task.setRecurring(false);
        task.setRecurrencePattern("");
        task.setEstimatedTimeMinutes(getEstimatedTime());
        task.setActualTimeMinutes(0);
        task.setColorTag("");

        taskViewModel.insertTask(task);
        Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInput() {
        String title = etTaskTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etTaskTitle.setError("Task title is required");
            etTaskTitle.requestFocus();
            return false;
        }
        return true;
    }

    private int getPriorityValue() {
        String priority = spinnerPriority.getSelectedItem().toString();
        switch (priority) {
            case "Low": return 1;
            case "Medium": return 2;
            case "High": return 3;
            default: return 2;
        }
    }

    private String getTags() {
        String tagsText = etTags.getText().toString().trim();
        if (TextUtils.isEmpty(tagsText)) return "";

        tagsText = tagsText.replace(" ", ",");
        return tagsText;
    }

    private int getEstimatedTime() {
        String timeText = etEstimatedTime.getText().toString().trim();
        if (TextUtils.isEmpty(timeText)) return 0;

        try {
            return Math.max(0, Integer.parseInt(timeText));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}