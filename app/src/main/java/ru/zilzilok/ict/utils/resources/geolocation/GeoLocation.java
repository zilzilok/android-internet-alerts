package ru.zilzilok.ict.utils.resources.geolocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

public class GeoLocation implements LocationListener {
    private static final String TAG = "GeoLocation";
    private double latitude;
    private double longitude;
    private Address address;

    public GeoLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (GeoLocationPermission.checkPermission(context) && locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                this.latitude = location.getLatitude();
                this.longitude = location.getLongitude();
            }
            try {
                List<Address> addressList = new Geocoder(context).getFromLocation(latitude, longitude, 1);
                if (addressList.size() > 0)
                    address = addressList.get(0);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String getCountryName() {
        return address != null ? address.getCountryName() : null;
    }

    public String getCountryCode() {
        return address != null ? address.getCountryCode() : null;
    }

    public String getCity() {
        return address != null ? address.getLocality() : null;
    }

    public String getStreet() {
        return address != null ? address.getThoroughfare() : null;
    }

    @NonNull
    @Override
    public String toString() {
        String countryName = getCountryName();
        String city = getCity();
        String street = getStreet();
        return address != null ? String.format("%s, %s, %s", countryName, city, street) : "";
    }
}
