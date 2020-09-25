package ru.zilzilok.ict.utils.resources.geolocation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Abstract class for geolocation permission.
 */
public abstract class GeoLocationPermission {
    private static final String TAG = "GeoLocationPermission";
    public static final int PERMISSION_REQUEST_CODE = 200;

    /**
     * @param context to check permission
     * @return true if application has permission, false otherwise
     */
    public static boolean checkPermission(Context context) {
        int currPerm1 = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        int currPerm2 = ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION);
        return currPerm1 == PackageManager.PERMISSION_GRANTED && currPerm2 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests geolocation permission.
     * @param activity to request permission
     */
    public static void requestPermission(Activity activity) {
        String funcName = "[requestPermission]";

        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        Log.i(TAG, String.format("%s Geolocation permission was requested.", funcName));
    }
}
