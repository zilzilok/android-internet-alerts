package ru.zilzilok.ict.utils.resources.geolocation;

import android.location.Geocoder;
import android.location.Location;

public interface GeoLocationChangedListener {
    void onLocationChange(Geocoder geocoder, Location location);
    void onError(String error);
}
