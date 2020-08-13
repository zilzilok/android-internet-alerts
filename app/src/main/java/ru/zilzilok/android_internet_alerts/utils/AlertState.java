package ru.zilzilok.android_internet_alerts.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.zilzilok.android_internet_alerts.R;

public class AlertState {
    private static final String CHANNEL_ID = "ZXC";
    private static final String CHANNEL_NAME = "ZXC";
    private static final String CHANNEL_DESCRIPTION = "ZXC";
    private static final int NOTIFY_ID = 1337;

    private String connectionType;

    public AlertState(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void notifyAboutState(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_poop)
                        .setContentTitle(getConnectionType() + context.getResources().getString(R.string.appeard))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }
}
