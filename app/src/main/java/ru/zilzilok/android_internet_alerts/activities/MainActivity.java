package ru.zilzilok.android_internet_alerts.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.zilzilok.android_internet_alerts.R;
import ru.zilzilok.android_internet_alerts.utils.AlertState;
import ru.zilzilok.android_internet_alerts.utils.AlertStateAdapter;
import ru.zilzilok.android_internet_alerts.utils.ProgressButton;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String CHANNEL_ID = "ZXC";
    private static final String CHANNEL_NAME = "ZXC";
    private static final String CHANNEL_DESCRIPTION = "ZXC";

    private int currId = 0;
    private ProgressButton addButton;
    private ProgressButton checkButton;
    private List<AlertState> alertStates = new ArrayList<>();
    private AlertStateAdapter alertStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check button click listener
        View viewCheckButton = findViewById(R.id.buttonCheck);
        checkButton = new ProgressButton(MainActivity.this,
                viewCheckButton, "Check Connection");
        viewCheckButton.setOnClickListener(this::buttonCheckClicked);

        // Add button click listener
        View viewAddButton = findViewById(R.id.buttonAdd);
        addButton = new ProgressButton(MainActivity.this,
                viewAddButton, "Add");
        viewAddButton.setOnClickListener(this::buttonAddClicked);

        // List for States
        ListView alertStateList = findViewById(R.id.listViewStates);
        alertStateAdapter = new AlertStateAdapter(this, R.layout.list_item, alertStates);
        alertStateList.setAdapter(alertStateAdapter);
    }

    public void buttonCheckClicked(View view) {
        checkButton.buttonActivated();
        view.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Toast.makeText(this, getNetworkClass(this), Toast.LENGTH_LONG).show();
            checkButton.buttonRestored();
            view.setEnabled(true);
        }, 800);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getNetworkClass(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                            return "2G";
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G";
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G";
                        default:
                            return "Error";
                    }
                default:
                    return "Error";
            }
        } else {
            return "No Network";
        }
    }

    public void buttonAddClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_alerts_menu);
        popupMenu.show();
    }

    private void startMonitor(AlertState state) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(state.getConnectionType() + " appeared.")
                        .setContentText("Wow, omg, Vadim pidor.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
        notificationManager.notify(currId++, builder.build());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        AlertState alertState = new AlertState(item.toString());
        alertStates.add(alertState);
        startMonitor(alertState);
        alertStateAdapter.notifyDataSetChanged();
        return true;
    }
}
