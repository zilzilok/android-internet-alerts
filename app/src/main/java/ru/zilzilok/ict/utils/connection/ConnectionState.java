package ru.zilzilok.ict.utils.connection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.activities.MainActivity;

/**
 *  Class that presents connection type state.
 */
public class ConnectionState {
    private static final String TAG = "ConnectionState";
    private static final String CHANNEL_ID = "def_id";
    private static final String CHANNEL_NAME = "ICT";
    private static final String CHANNEL_DESCRIPTION = "Notification about connection appearance.";
    private static final int NOTIFY_ID = 1337;

    private String connectionType;  // Connection type

    public ConnectionState(String connectionType) {
        this.connectionType = connectionType;
    }

    /**
     * @return connection type
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     *  Notify about connection type appearance.
     * @param context for resource accessing
     */
    public void notifyAboutState(Context context) {
        String funcName = "[notifyAboutState]";

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
                        .setContentTitle(context.getResources().getString(R.string.app_name_fullname))
                        .setContentText(getConnectionType() + context.getResources().getString(R.string.appeard))
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, builder.build());

        Log.i(TAG, String.format("%s Notified about %s.", funcName, getConnectionType()));
    }

    /**
     * @param context for resource accessing
     * @return current connection type
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getCurrentConnectionType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager != null
                ? connectivityManager.getActiveNetworkInfo()
                : null;

        if (activeNetwork != null) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    // connected to wifi
                    return "WIFI";
                case ConnectivityManager.TYPE_MOBILE:
                    // connected to mobile data
                    TelephonyManager mTelephonyManager = (TelephonyManager)
                            context.getSystemService(Context.TELEPHONY_SERVICE);
                    int networkType = Objects.requireNonNull(mTelephonyManager).getNetworkType();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "2G/E";
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G/H/H+";
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G/LTE";
                        default:
                            return "ERROR";
                    }
                default:
                    return "ERROR";
            }
        } else {
            return context.getResources().getString(R.string.no_internet);
        }
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
