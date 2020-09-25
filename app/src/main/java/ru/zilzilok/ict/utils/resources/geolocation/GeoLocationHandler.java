package ru.zilzilok.ict.utils.resources.geolocation;

import android.app.Activity;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Objects;

/**
 * Class that represent geolocation handler.
 */
public class GeoLocationHandler {
    private Activity activity;
    private GeoLocationChangedListener geoLocationChangedListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean updateStartedInternally = false;

    private Geocoder geocoder;

    public GeoLocationHandler(Activity activity, GeoLocationChangedListener geoLocationChangedListener) {
        this.activity = activity;
        this.geoLocationChangedListener = geoLocationChangedListener;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        createLocationRequest();
        getDeviceLocation();

        geocoder = new Geocoder(activity);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);
                    lastKnownLocation = location;
                    if (geoLocationChangedListener != null) {
                        geoLocationChangedListener.onLocationChange(geocoder, location);
                        if (updateStartedInternally) {
                            stopLocationUpdate();
                        }
                    }
                }
            }
        };

        startLocationUpdates();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation == null) {
                        updateStartedInternally = true;
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    } else {
                        geoLocationChangedListener.onLocationChange(geocoder, lastKnownLocation);
                    }
                } else {
                    geoLocationChangedListener.onError("Can't get Location");
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
            geoLocationChangedListener.onError(e.getMessage());

        }
    }

    private void startLocationUpdates() {
        if (GeoLocationPermission.checkPermission(activity)) {
            updateStartedInternally = false;
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);         //set the interval in which you want to get locations
        locationRequest.setFastestInterval(5000);   //if a location is available sooner you can get it (i.e. another app is using the location services)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}