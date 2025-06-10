package com.example.taskmaster.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taskmaster.MainActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;
import com.example.taskmaster.ui.activity.DetailTaskActivity;

/**
 * Helper class to manage notifications in the TaskMaster app.
 */
public class NotificationHelper {

    private final Context context;
    private final NotificationManagerCompat notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }

    /**
     * Creates notification channels required for Android 8.0 and above.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Task reminder channel
            NotificationChannel taskChannel = new NotificationChannel(
                    Constants.CHANNEL_TASKS,
                    context.getString(R.string.channel_name_tasks),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            taskChannel.setDescription(context.getString(R.string.channel_desc_tasks));

            // Pomodoro alerts channel
            NotificationChannel pomodoroChannel = new NotificationChannel(
                    Constants.CHANNEL_POMODORO,
                    context.getString(R.string.channel_name_pomodoro),
                    NotificationManager.IMPORTANCE_HIGH
            );
            pomodoroChannel.setDescription(context.getString(R.string.channel_desc_pomodoro));

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(taskChannel);
            manager.createNotificationChannel(pomodoroChannel);
        }
    }

    /**
     * Sends a task reminder notification.
     *
     * @param task The task to remind about
     * @param notificationId A unique ID for the notification
     */
    public void showTaskReminderNotification(Task task, int notificationId) {
        Intent intent = new Intent(context, DetailTaskActivity.class);
        intent.putExtra(Constants.EXTRA_TASK_ID, task.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_TASKS)
                .setSmallIcon(R.drawable.ic_notification_task)
                .setContentTitle(context.getString(R.string.task_reminder))
                .setContentText(task.getTitle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Handle notification permission not granted
            e.printStackTrace();
        }
    }

    /**
     * Sends a Pomodoro session completion notification.
     *
     * @param title The notification title
     * @param message The notification message
     * @param notificationId A unique ID for the notification
     */
    public void showPomodoroNotification(String title, String message, int notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_POMODORO)
                .setSmallIcon(R.drawable.ic_notification_pomodoro)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            // Handle notification permission not granted
            e.printStackTrace();
        }
    }

    /**
     * Cancels a notification with the specified ID.
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    public static void createNotificationChannel(Context context) {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TaskMaster Notifications";
            String description = "Notifications for task reminders and updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(
                    "taskmaster_channel", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}