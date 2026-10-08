package com.petcare.app;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.app.PendingIntent;
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

        String id =
                intent.getStringExtra("id");

        String title =
                intent.getStringExtra("title");

        String action =
                intent.getAction();

        if (ACTION_DONE.equals(action)) {

            NotificationHelper.cancel(
                    context,
                    id
            );

            return;
        }

        if (ACTION_SNOOZE_10.equals(action)) {

            scheduleSnooze(
                    context,
                    id,
                    title,
                    10
            );

            NotificationHelper.cancel(
                    context,
                    id
            );

            return;
        }

        if (ACTION_SNOOZE_30.equals(action)) {

            scheduleSnooze(
                    context,
                    id,
                    title,
                    30
            );

            NotificationHelper.cancel(
                    context,
                    id
            );

            return;
        }

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
        );

        PendingIntent fullScreenIntent =
                PendingIntent.getActivity(
                        context,
                        Math.abs(
                                id.hashCode()
                        ),
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        android.app.NotificationManager manager =
                (android.app.NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            android.app.Notification.Builder builder;

            if (Build.VERSION.SDK_INT >= 26) {

                builder =
                        new android.app.Notification.Builder(
                                context,
                                "petcare_reminders"
                        );

            } else {

                builder =
                        new android.app.Notification.Builder(
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
                            android.app.Notification.CATEGORY_ALARM
                    )
                    .setPriority(
                            android.app.Notification.PRIORITY_MAX
                    )
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setFullScreenIntent(
                            fullScreenIntent,
                            true
                    );

            manager.notify(
                    Math.abs(
                            id.hashCode() % 100000
                    ),
                    builder.build()
            );
        }
    }

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

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        Math.abs(
                                id.hashCode()
                        ) + minutes,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager manager =
                (AlarmManager)
                        context.getSystemService(
                                Context.ALARM_SERVICE
                        );

        if (manager != null) {

            if (Build.VERSION.SDK_INT >= 23) {

                manager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        trigger,
                        pendingIntent
                );

            } else {

                manager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        trigger,
                        pendingIntent
                );
            }
        }
    }
}