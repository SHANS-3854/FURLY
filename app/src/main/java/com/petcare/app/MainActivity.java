package com.petcare.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 1001;
    private static final int LOCATION_PERMISSION_REQUEST = 1002;

    private WebView webView;

    private GeolocationPermissions.Callback geoCallback;
    private String geoOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        requestNotificationPermission();
        requestLocationPermission();

        webView = new WebView(this);

        WebSettings settings =
                webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(
                new WebViewClient()
        );

        webView.setWebChromeClient(
                new WebChromeClient() {

                    @Override
                    public void onGeolocationPermissionsShowPrompt(
                            String origin,
                            GeolocationPermissions.Callback callback
                    ) {

                        geoOrigin = origin;
                        geoCallback = callback;

                        if (Build.VERSION.SDK_INT >= 23 &&
                                checkSelfPermission(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(
                                    new String[]{
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                    },
                                    LOCATION_PERMISSION_REQUEST
                            );

                        } else {

                            callback.invoke(
                                    origin,
                                    true,
                                    false
                            );
                        }
                    }
                }
        );

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

        if (Build.VERSION.SDK_INT >= 33) {

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

    private void requestLocationPermission() {

        if (Build.VERSION.SDK_INT >= 23) {

            boolean fine =
                    checkSelfPermission(
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;

            boolean coarse =
                    checkSelfPermission(
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;

            if (!fine && !coarse) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        LOCATION_PERMISSION_REQUEST
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                LOCATION_PERMISSION_REQUEST) {

            boolean granted = false;

            for (int result : grantResults) {

                if (result ==
                        PackageManager.PERMISSION_GRANTED) {

                    granted = true;
                    break;
                }
            }

            if (geoCallback != null &&
                    geoOrigin != null) {

                geoCallback.invoke(
                        geoOrigin,
                        granted,
                        false
                );

                geoCallback = null;
                geoOrigin = null;
            }
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId =
                    "petcare_reminders";

            NotificationChannel channel =
                    new NotificationChannel(
                            channelId,
                            "PetCare Reminders",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Pet care alarm reminders"
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

                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }

    public static class PetCareBridge {

        private final Context context;

        public PetCareBridge(
                Context context
        ) {
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
                        triggerTime
                );

                intent.putExtra(
                        "repeat",
                        repeat
                );

                int requestCode =
                        Math.abs(
                                id.hashCode()
                        );

                android.app.PendingIntent pendingIntent =
                        android.app.PendingIntent.getBroadcast(
                                context,
                                requestCode,
                                intent,
                                android.app.PendingIntent.FLAG_UPDATE_CURRENT
                                        | android.app.PendingIntent.FLAG_IMMUTABLE
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
                                triggerTime,
                                pendingIntent
                        );

                    } else {

                        Intent settingsIntent =
                                new Intent(
                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                        Uri.parse(
                                                "package:" +
                                                        context.getPackageName()
                                        )
                                );

                        settingsIntent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                        );

                        context.startActivity(
                                settingsIntent
                        );

                        alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                triggerTime,
                                pendingIntent
                        );
                    }

                } else if (Build.VERSION.SDK_INT >= 23) {

                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                    );

                } else {

                    alarmManager.setExact(
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