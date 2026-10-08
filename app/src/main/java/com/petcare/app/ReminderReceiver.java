package com.petcare.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String ACTION_REMINDER =
            "com.petcare.app.ACTION_REMINDER";

    public static final String ACTION_DONE =
            "com.petcare.app.ACTION_DONE";

    public static final String ACTION_SNOOZE_10 =
            "com.petcare.app.ACTION_SNOOZE_10";

    public static final String ACTION_SNOOZE_30 =
            "com.petcare.app.ACTION_SNOOZE_30";

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        if (intent == null) {
            return;
        }

        String id =
                intent.getStringExtra("id");

        String title =
                intent.getStringExtra("title");

        String action =
                intent.getAction();

        if (id == null ||
                id.trim().isEmpty()) {

            id =
                    String.valueOf(
                            System.currentTimeMillis()
                    );
        }

        if (title == null ||
                title.trim().isEmpty()) {

            title =
                    "Pet Care Reminder";
        }

        /*
         * DONE
         */
        if (ACTION_DONE.equals(action)) {

            cancelNotification(
                    context,
                    id
            );

            return;
        }

        /*
         * SNOOZE 10 MINUTES
         */
        if (ACTION_SNOOZE_10.equals(action)) {

            scheduleSnooze(
                    context,
                    id,
                    title,
                    10
            );

            cancelNotification(
                    context,
                    id
            );

            return;
        }

        /*
         * SNOOZE 30 MINUTES
         */
        if (ACTION_SNOOZE_30.equals(action)) {

            scheduleSnooze(
                    context,
                    id,
                    title,
                    30
            );

            cancelNotification(
                    context,
                    id
            );

            return;
        }

        /*
         * SHOW ALARM
         */

        Intent alarmIntent =
                new Intent(
                        context,
                        AlarmActivity.class
                );

        alarmIntent.putExtra(
                "id",
                id
        );

        alarmIntent.putExtra(
                "title",
                title
        );

        alarmIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        int requestCode =
                Math.abs(
                        id.hashCode()
                );

        PendingIntent fullScreenIntent =
                PendingIntent.getActivity(
                        context,
                        requestCode,
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            Notification.Builder builder;

            if (Build.VERSION.SDK_INT >= 26) {

                builder =
                        new Notification.Builder(
                                context,
                                "petcare_reminders"
                        );

            } else {

                builder =
                        new Notification.Builder(
                                context
                        );
            }

            builder
                    .setSmallIcon(
                            android.R.drawable.ic_lock_idle_alarm
                    )
                    .setContentTitle(
                            "PetCare Alarm"
                    )
                    .setContentText(
                            title
                    )
                    .setCategory(
                            Notification.CATEGORY_ALARM
                    )
                    .setPriority(
                            Notification.PRIORITY_MAX
                    )
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setFullScreenIntent(
                            fullScreenIntent,
                            true
                    );

            manager.notify(
                    getNotificationId(id),
                    builder.build()
            );
        }
    }

    /*
     * Cancel notification
     */
    private void cancelNotification(
            Context context,
            String id
    ) {

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.cancel(
                    getNotificationId(id)
            );
        }
    }

    /*
     * Notification ID
     */
    private int getNotificationId(
            String id
    ) {

        return Math.abs(
                id.hashCode() % 100000
        );
    }

    /*
     * Schedule snooze
     */
    private void scheduleSnooze(
            Context context,
            String id,
            String title,
            int minutes
    ) {

        long trigger =
                System.currentTimeMillis()
                        + minutes * 60L * 1000L;

        Intent intent =
                new Intent(
                        context,
                        ReminderReceiver.class
                );

        intent.setAction(
                ACTION_REMINDER
        );

        intent.putExtra(
                "id",
                id
        );

        intent.putExtra(
                "title",
                title
        );

        intent.putExtra(
                "timestamp",
                trigger
        );

        int requestCode =
                Math.abs(
                        id.hashCode()
                ) + minutes;

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        requestCode,
                        intent,
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

        if (Build.VERSION.SDK_INT >= 31) {

            if (alarmManager.canScheduleExactAlarms()) {

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        trigger,
                        pendingIntent
                );

            } else {

                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        trigger,
                        pendingIntent
                );
            }

        } else if (Build.VERSION.SDK_INT >= 23) {

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    trigger,
                    pendingIntent
            );

        } else {

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    trigger,
                    pendingIntent
            );
        }
    }
}