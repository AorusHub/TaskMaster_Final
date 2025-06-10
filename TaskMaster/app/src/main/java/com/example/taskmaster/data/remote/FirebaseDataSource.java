package com.example.taskmaster.data.remote;

import com.example.taskmaster.model.FirebaseTask;
import com.example.taskmaster.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDataSource {
    private final FirebaseFirestore firestore;

    private static final String TASKS_COLLECTION = "tasks";
    private static final String USERS_COLLECTION = "users";
    private static final String CATEGORIES_COLLECTION = "categories";
    private static final String POMODOROS_COLLECTION = "pomodoros";
    private static final String ACTIVITIES_COLLECTION = "activities";

    public FirebaseDataSource() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    // ========== TASK OPERATIONS ==========

    public void getAllTasks(String userId, TasksCallback callback) {
        firestore.collection(TASKS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<FirebaseTask> tasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FirebaseTask firebaseTask = document.toObject(FirebaseTask.class);
                            // Firestore doesn't include document ID in object by default
                            firebaseTask.setFirebaseId(document.getId());
                            tasks.add(firebaseTask);
                        }
                        callback.onTasksLoaded(tasks);
                    } else {
                        callback.onError(task.getException() != null ?
                                task.getException().getMessage() : "Error loading tasks");
                    }
                });
    }

    public void addTask(FirebaseTask task, OperationCallback callback) {
        // Track activity
        trackActivity(task.getUserId(), "create_task", task.getTitle());

        firestore.collection(TASKS_COLLECTION)
                .add(task)
                .addOnSuccessListener(documentReference -> {
                    task.setFirebaseId(documentReference.getId());
                    callback.onSuccess(task);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateTask(FirebaseTask task, OperationCallback callback) {
        // Track activity
        trackActivity(task.getUserId(), "update_task", task.getTitle());

        if (task.getFirebaseId() != null) {
            firestore.collection(TASKS_COLLECTION)
                    .document(task.getFirebaseId())
                    .set(task)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(task))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            callback.onError("Task doesn't have Firebase ID");
        }
    }

    public void deleteTask(FirebaseTask task, OperationCallback callback) {
        // Track activity
        trackActivity(task.getUserId(), "delete_task", task.getTitle());

        if (task.getFirebaseId() != null) {
            firestore.collection(TASKS_COLLECTION)
                    .document(task.getFirebaseId())
                    .delete()
                    .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            callback.onError("Task doesn't have Firebase ID");
        }
    }

    public void getTaskById(String taskId, TaskCallback callback) {
        firestore.collection(TASKS_COLLECTION)
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    FirebaseTask task = documentSnapshot.toObject(FirebaseTask.class);
                    if (task != null) {
                        task.setFirebaseId(documentSnapshot.getId());
                    }
                    callback.onTaskLoaded(task);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========== USER OPERATIONS ==========

    public void createUser(User user, OperationCallback callback) {
        firestore.collection(USERS_COLLECTION)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getUser(String userId, UserCallback callback) {
        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    callback.onUserLoaded(user);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateUser(User user, OperationCallback callback) {
        if (user.getId() != null) {
            firestore.collection(USERS_COLLECTION)
                    .document(user.getId())
                    .set(user)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        } else {
            callback.onError("User ID is invalid");
        }
    }

    // ========== ACTIVITY TRACKING ==========

    public void trackActivity(String userId, String activityType, String description) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("userId", userId);
        activity.put("type", activityType);
        activity.put("description", description);
        activity.put("timestamp", new Date());

        firestore.collection(ACTIVITIES_COLLECTION).add(activity);
    }

    public void getUserActivities(String userId, int limit, ActivitiesCallback callback) {
        firestore.collection(ACTIVITIES_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp")
                .limit(limit)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> activities = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        activities.add(doc.getData());
                    }
                    callback.onActivitiesLoaded(activities);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========== CALLBACK INTERFACES ==========

    public interface TasksCallback {
        void onTasksLoaded(List<FirebaseTask> tasks);
        void onError(String errorMessage);
    }

    public interface TaskCallback {
        void onTaskLoaded(FirebaseTask task);
        void onError(String errorMessage);
    }

    public interface UserCallback {
        void onUserLoaded(User user);
        void onError(String errorMessage);
    }

    public interface ActivitiesCallback {
        void onActivitiesLoaded(List<Map<String, Object>> activities);
        void onError(String errorMessage);
    }

    public interface OperationCallback {
        void onSuccess(Object result);
        void onError(String errorMessage);
    }
}