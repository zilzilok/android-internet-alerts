package ru.zilzilok.ict.utils.connection;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.activities.MainActivity;

public class ConnectionState {
    private static final String CHANNEL_ID = "ZXC";
    private static final String CHANNEL_NAME = "ZXC";
    private static final String CHANNEL_DESCRIPTION = "ZXC";
    private static final int NOTIFY_ID = 1337;

    private String connectionType;

    public ConnectionState(String connectionType) {
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
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_connection)
                        .setContentTitle(getConnectionType() + context.getResources().getString(R.string.appeard))
                        .setContentIntent(contentIntent)
                        .setStyle(new NotificationCompat.InboxStyle())
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ConnectionState)) {
            return false;
        }

        ConnectionState connectionState = (ConnectionState) obj;
        return connectionState.getConnectionType().equals(this.getConnectionType());
    }
}