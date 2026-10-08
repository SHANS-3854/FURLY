package com.petcare.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class AlarmService extends Service {

    private static final String CHANNEL_ID =
            "petcare_alarm_service";

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private String reminderId;
    private String reminderTitle;

    @Override
    public void onCreate() {

        super.onCreate();

        createChannel();

        startAlarmSound();
        startVibration();
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        if (intent != null) {

            reminderId =
                    intent.getStringExtra("id");

            reminderTitle =
                    intent.getStringExtra("title");
        }

        if (reminderId == null) {

            reminderId =
                    String.valueOf(
                            System.currentTimeMillis()
                    );
        }

        if (reminderTitle == null ||
                reminderTitle.trim().isEmpty()) {

            reminderTitle =
                    "Pet Care Reminder";
        }

        showNotification();

        /*
         * Open alarm screen
         */
        Intent alarmIntent =
                new Intent(
                        this,
                        AlarmActivity.class
                );

        alarmIntent.putExtra(
                "id",
                reminderId
        );

        alarmIntent.putExtra(
                "title",
                reminderTitle
        );

        alarmIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

        try {

            startActivity(alarmIntent);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return START_STICKY;
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "PetCare Alarm",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Active PetCare alarm"
            );

            channel.setSound(
                    null,
                    null
            );

            channel.enableVibration(false);

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }

    private void showNotification() {

        Intent alarmIntent =
                new Intent(
                        this,
                        AlarmActivity.class
                );

        alarmIntent.putExtra(
                "id",
                reminderId
        );

        alarmIntent.putExtra(
                "title",
                reminderTitle
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        Math.abs(
                                reminderId.hashCode()
                        ),
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {

            builder =
                    new Notification.Builder(
                            this,
                            CHANNEL_ID
                    );

        } else {

            builder =
                    new Notification.Builder(
                            this
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
                        reminderTitle
                )
                .setCategory(
                        Notification.CATEGORY_ALARM
                )
                .setPriority(
                        Notification.PRIORITY_MAX
                )
                .setOngoing(true)
                .setContentIntent(
                        pendingIntent
                );

        startForeground(
                2001,
                builder.build()
        );
    }

    private void startAlarmSound() {

        try {

            android.net.Uri sound =
                    android.media.RingtoneManager
                            .getDefaultUri(
                                    android.media.RingtoneManager.TYPE_ALARM
                            );

            mediaPlayer =
                    new MediaPlayer();

            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(
                                    AudioAttributes.USAGE_ALARM
                            )
                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                            )
                            .build()
            );

            mediaPlayer.setDataSource(
                    this,
                    sound
            );

            mediaPlayer.setLooping(true);

            mediaPlayer.prepare();

            mediaPlayer.start();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void startVibration() {

        vibrator =
                (Vibrator)
                        getSystemService(
                                Context.VIBRATOR_SERVICE
                        );

        if (vibrator == null) {
            return;
        }

        long[] pattern = {
                0,
                1000,
                500,
                1000,
                500
        };

        if (Build.VERSION.SDK_INT >= 26) {

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

    public void stopAlarm() {

        if (mediaPlayer != null) {

            try {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

            } catch (Exception ignored) {
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (vibrator != null) {

            vibrator.cancel();
        }

        stopForeground(true);

        stopSelf();
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {

            try {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

            } catch (Exception ignored) {
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (vibrator != null) {
            vibrator.cancel();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
