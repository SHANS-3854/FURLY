package com.petcare.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();
        requestNotificationPermission();

        webView = new WebView(this);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient());

        webView.addJavascriptInterface(
                new PetCareBridge(this),
                "AndroidPetCare"
        );

        webView.loadUrl(
                "file:///android_asset/index.html"
        );

        setContentView(webView);
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_REQUEST
                );
            }
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "petcare_reminders";

            NotificationChannel channel =
                    new NotificationChannel(
                            channelId,
                            "PetCare Reminders",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Pet care reminder notifications"
            );

            channel.enableVibration(true);

            channel.setVibrationPattern(
                    new long[]{
                            0,
                            500,
                            300,
                            500
                    }
            );

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static class PetCareBridge {

        private final Context context;

        public PetCareBridge(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void scheduleReminder(
                String id,
                String title,
                String timestamp,
                String repeat
        ) {

            try {

                long triggerTime =
                        Long.parseLong(timestamp);

                Intent intent =
                        new Intent(
                                context,
                                ReminderReceiver.class
                        );

                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra(
                        "timestamp",
                        triggerTime
                );
                intent.putExtra("repeat", repeat);

                int requestCode =
                        Math.abs(id.hashCode());

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

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (webView != null &&
                webView.canGoBack()) {

            webView.goBack();

        } else {

            super.onBackPressed();
        }
    }
}
