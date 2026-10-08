package com.petcare.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmActivity extends Activity {

    private Ringtone ringtone;
    private Vibrator vibrator;

    private String reminderId;
    private String reminderTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reminderId =
                getIntent().getStringExtra("id");

        reminderTitle =
                getIntent().getStringExtra("title");

        if (reminderTitle == null) {
            reminderTitle = "Pet Care Reminder";
        }

        startAlarm();

        showAlarmUI();
    }

    private void startAlarm() {

        Uri sound =
                RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_ALARM
                );

        ringtone =
                RingtoneManager.getRingtone(
                        this,
                        sound
                );

        if (ringtone != null) {

            ringtone.play();
        }

        vibrator =
                (Vibrator)
                        getSystemService(
                                Context.VIBRATOR_SERVICE
                        );

        if (vibrator != null) {

            long[] pattern = {
                    0,
                    1000,
                    500,
                    1000,
                    500
            };

            if (android.os.Build.VERSION.SDK_INT >= 26) {

                vibrator.vibrate(
                        VibrationEffect.createWaveform(
                                pattern,
                                0
                        )
                );

            } else {

                vibrator.vibrate(
                        pattern,
                        0
                );
            }
        }
    }

    private void showAlarmUI() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setGravity(
                Gravity.CENTER
        );

        root.setPadding(
                30,
                30,
                30,
                30
        );

        TextView title =
                new TextView(this);

        title.setText(
                "PETCARE"
        );

        title.setTextSize(14);

        title.setGravity(
                Gravity.CENTER
        );

        TextView reminder =
                new TextView(this);

        reminder.setText(
                reminderTitle
        );

        reminder.setTextSize(28);

        reminder.setGravity(
                Gravity.CENTER
        );

        reminder.setPadding(
                0,
                25,
                0,
                35
        );

        Button done =
                new Button(this);

        done.setText(
                "DONE"
        );

        Button snooze10 =
                new Button(this);

        snooze10.setText(
                "SNOOZE 10 MIN"
        );

        Button snooze30 =
                new Button(this);

        snooze30.setText(
                "SNOOZE 30 MIN"
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        root.addView(
                reminder,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        root.addView(
                done,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        root.addView(
                snooze10,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        root.addView(
                snooze30,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        done.setOnClickListener(
                v -> finishAlarm()
        );

        snooze10.setOnClickListener(
                v -> snooze(10)
        );

        snooze30.setOnClickListener(
                v -> snooze(30)
        );

        setContentView(root);
    }

    private void finishAlarm() {

        stopAlarm();

        NotificationHelper.cancel(
                this,
                reminderId
        );

        finish();
    }

    private void snooze(int minutes) {

        stopAlarm();

        Intent intent =
                new Intent(
                        this,
                        ReminderReceiver.class
                );

        intent.setAction(
                ReminderReceiver.ACTION_REMINDER
        );

        intent.putExtra(
                "id",
                reminderId
        );

        intent.putExtra(
                "title",
                reminderTitle
        );

        long trigger =
                System.currentTimeMillis()
                        + minutes * 60L * 1000L;

        intent.putExtra(
                "timestamp",
                trigger
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        Math.abs(
                                reminderId.hashCode()
                        ) + minutes,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        AlarmManager alarmManager =
                (AlarmManager)
                        getSystemService(
                                ALARM_SERVICE
                        );

        if (alarmManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= 23) {

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

        finish();
    }

    private void stopAlarm() {

        if (ringtone != null &&
                ringtone.isPlaying()) {

            ringtone.stop();
        }

        if (vibrator != null) {

            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {

        stopAlarm();

        super.onDestroy();
    }
}
