package com.petcare.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver
        extends BroadcastReceiver {

    public static final String ACTION_REMINDER =
            "com.petcare.app.ACTION_REMINDER";

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

        Intent serviceIntent =
                new Intent(
                        context,
                        AlarmService.class
                );

        serviceIntent.putExtra(
                "id",
                id
        );

        serviceIntent.putExtra(
                "title",
                title
        );

        if (Build.VERSION.SDK_INT >= 26) {

            context.startForegroundService(
                    serviceIntent
            );

        } else {

            context.startService(
                    serviceIntent
            );
        }
    }
}