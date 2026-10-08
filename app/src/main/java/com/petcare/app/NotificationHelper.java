package com.petcare.app;

import android.app.NotificationManager;
import android.content.Context;

public class NotificationHelper {

    public static void cancel(
            Context context,
            String id
    ) {

        if (id == null) {
            return;
        }

        int notificationId =
                Math.abs(
                        id.hashCode() % 100000
                );

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (manager != null) {

            manager.cancel(
                    notificationId
            );
        }
    }
}
