package com.petcare.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SnoozeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        String id =
                intent.getStringExtra("id");

        String title =
                intent.getStringExtra("title");

        if (id == null) {
            id = String.valueOf(System.currentTimeMillis());
        }

        if (title == null || title.trim().isEmpty()) {
            title = "Pet Care Reminder";
        }

        Intent reminderIntent =
                new Intent(
                        context,
                        ReminderReceiver.class
                );

        reminderIntent.setAction(
                ReminderReceiver.ACTION_REMINDER
        );

        reminderIntent.putExtra(
                "id",
                id
        );

        reminderIntent.putExtra(
                "title",
                title
        );

        context.sendBroadcast(
                reminderIntent
        );
    }
}
