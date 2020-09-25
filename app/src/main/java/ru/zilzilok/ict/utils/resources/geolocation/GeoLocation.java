package ru.zilzilok.ict.utils.resources.geolocation;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import ru.zilzilok.ict.utils.resources.ResourceNotAvailableException;

/**
 * Class for geolocation receiving.
 */
public class GeoLocation implements GeoLocationChangedListener {
    private static final String TAG = "GeoLocation";

    Geocoder geocoder;
    Location location;

    Double latitude;
    Double longitude;
    String country;
    String countryCode;
    String city;
    String street;
    String feature;
    String continent;

    @Override
    public void onLocationChange(Geocoder geocoder, Location location) {
        this.geocoder = geocoder;
        this.location = location;

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();


        try {
            Address address = this.geocoder.getFromLocation(this.location.getLatitude(), this.location.getLongitude(), 1).get(0);

            country = address.getCountryName();
            countryCode = address.getCountryCode();
            city = address.getLocality();
            street = address.getThoroughfare();
            feature = address.getFeatureName();

            try {
                Log.i(TAG, "Overall: " + address.toString());
                Log.i(TAG, "Latitude: " + getLatitude());
                Log.i(TAG, "Longitude: " + getLongitude());
                Log.i(TAG, "Country: " + getCountry());
                Log.i(TAG, "City: " + getCity());
                Log.i(TAG, "Street: " + getStreet());
                Log.i(TAG, "Hemisphere: " + getHemisphere());
            } catch (ResourceNotAvailableException ignored) {

            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }


    @Override
    public void onError(String error) {
        Log.e(TAG, error);
    }

    public String getCity() throws ResourceNotAvailableException {
        if (city == null)
            throw new ResourceNotAvailableException("Can't get location...");
        return city;
    }

    public String getCountry() throws ResourceNotAvailableException {
        if (country == null)
            throw new ResourceNotAvailableException("Can't get location...");
        return country;
    }

    public Double getLongitude() throws ResourceNotAvailableException {

        if (longitude == null)
            throw new ResourceNotAvailableException("Can't get location...");
        return longitude;
    }

    public Double getLatitude() throws ResourceNotAvailableException {

        if (latitude == null)
            throw new ResourceNotAvailableException("Can't get location...");
        return latitude;
    }

    public String getStreet() throws ResourceNotAvailableException {
        if (street == null)
            throw new ResourceNotAvailableException("Can't get location...");
        return street;
    }

    public String getFeature() throws ResourceNotAvailableException {
        if (feature == null)
            throw new ResourceNotAvailableException("Can't get location...");
        return feature;
    }

    public String getHemisphere() throws ResourceNotAvailableException {
        return String.format("(%f,%f)", getLongitude(), getLatitude());
    }

    @NonNull
    @Override
    public String toString() {
        try {
            return String.format("%s, %s, %s %s", getCountry(), getCity(), getStreet(), getFeature());
        } catch (ResourceNotAvailableException e) {
            return "null";
        }
    }
}
