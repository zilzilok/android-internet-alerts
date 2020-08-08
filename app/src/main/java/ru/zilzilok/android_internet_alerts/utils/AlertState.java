package ru.zilzilok.android_internet_alerts.utils;

import android.net.ConnectivityManager;

public class AlertState {
    private String connectionType;

    public AlertState(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getConnectionType() {
        return connectionType;
    }
}
