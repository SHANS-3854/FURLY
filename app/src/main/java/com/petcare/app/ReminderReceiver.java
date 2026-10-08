package com.petcare.app;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String ACTION_REMINDER =
            "com.petcare.app.ACTION_REMINDER";

    public static final String ACTION_DONE =
            "com.petcare.app.ACTION_DONE";

    public static final String ACTION_SNOOZE_10 =
            "com.petcare.app.ACTION_SNOOZE_10";

    public static final String ACTION_SNOOZE_30 =
            "com.petcare.app.ACTION_SNOOZE_30";

    private static final String CHANNEL_ID =
            "petcare_reminders";

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        String action = intent.getAction();

        if (ACTION_DONE.equals(action)) {

            cancelNotification(context, intent);
            return;
        }

        if (ACTION_SNOOZE_10.equals(action)) {

            scheduleSnooze(
                    context,
                    intent,
                    10
            );

            cancelNotification(context, intent);
            return;
        }

        if (ACTION_SNOOZE_30.equals(action)) {

            scheduleSnooze(
                    context,
                    intent,
                    30
            );

            cancelNotification(context, intent);
            return;
        }

        showNotification(context, intent);
    }

    private void showNotification(
            Context context,
            Intent intent
    ) {

        String id =
                intent.getStringExtra("id");

        String title =
                intent.getStringExtra("title");

        if (title == null || title.trim().isEmpty()) {
            title = "Pet Care Reminder";
        }

        int notificationId =
                getNotificationId(id);

        Intent doneIntent =
                new Intent(
                        context,
                        ReminderReceiver.class
                );

        doneIntent.setAction(ACTION_DONE);

        doneIntent.putExtra("id", id);
        doneIntent.putExtra("title", title);

        PendingIntent donePendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        notificationId + 1,
                        doneIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Intent snooze10Intent =
                new Intent(
                        context,
                        ReminderReceiver.class
                );

        snooze10Intent.setAction(ACTION_SNOOZE_10);

        snooze10Intent.putExtra("id", id);
        snooze10Intent.putExtra("title", title);

        PendingIntent snooze10PendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        notificationId + 2,
                        snooze10Intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Intent snooze30Intent =
                new Intent(
                        context,
                        ReminderReceiver.class
                );

        snooze30Intent.setAction(ACTION_SNOOZE_30);

        snooze30Intent.putExtra("id", id);
        snooze30Intent.putExtra("title", title);

        PendingIntent snooze30PendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        notificationId + 3,
                        snooze30Intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Uri alarmSound =
                RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_ALARM
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle("PetCare")
                        .setContentText(title)
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setCategory(
                                NotificationCompat.CATEGORY_ALARM
                        )
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setSound(alarmSound)
                        .setVibrate(
                                new long[]{
                                        0,
                                        500,
                                        300,
                                        500
                                }
                        )
                        .addAction(
                                0,
                                "DONE",
                                donePendingIntent
                        )
                        .addAction(
                                0,
                                "SNOOZE 10m",
                                snooze10PendingIntent
                        )
                        .addAction(
                                0,
                                "SNOOZE 30m",
                                snooze30PendingIntent
                        );

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.notify(
                    notificationId,
                    builder.build()
            );
        }
    }

    private void cancelNotification(
            Context context,
            Intent intent
    ) {

        String id =
                intent.getStringExtra("id");

        int notificationId =
                getNotificationId(id);

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.cancel(notificationId);
        }
    }

    private void scheduleSnooze(
            Context context,
            Intent originalIntent,
            int minutes
    ) {

        String id =
                originalIntent.getStringExtra("id");

        String title =
                originalIntent.getStringExtra("title");

        long triggerTime =
                System.currentTimeMillis()
                        + (minutes * 60L * 1000L);

        Intent reminderIntent =
                new Intent(
                        context,
                        ReminderReceiver.class
                );

        reminderIntent.setAction(
                        ACTION_REMINDER
                );

        reminderIntent.putExtra(
                "id",
                id
        );

        reminderIntent.putExtra(
                "title",
                title
        );

        reminderIntent.putExtra(
                "timestamp",
                triggerTime
        );

        int requestCode =
                getNotificationId(id)
                        + minutes;

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        reminderIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager alarmManager =
                (AlarmManager)
                        context.getSystemService(
                                Context.ALARM_SERVICE
                        );

        if (alarmManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (alarmManager.canScheduleExactAlarms()) {

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );

            } else {

                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            }

        } else {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }

    private int getNotificationId(String id) {

        if (id == null) {
            return 1000;
        }

        return Math.abs(id.hashCode() % 100000);
    }
}
