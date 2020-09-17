package ru.zilzilok.ict.utils.resources.geolocation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public abstract class GeoLocationPermission {
    private static final String TAG = "GeoLocationPermission";
    public static final int PERMISSION_REQUEST_CODE = 200;

    public static boolean checkPermission(Context context) {
        int currPerm = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        return currPerm == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity) {
        String funcName = "[requestPermission]";

        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        Log.i(TAG, String.format("%s Geolocation permission was requested.", funcName));
    }
}
