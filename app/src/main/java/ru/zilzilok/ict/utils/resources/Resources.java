package ru.zilzilok.ict.utils.resources;

import ru.zilzilok.ict.utils.resources.geolocation.GeoLocation;

/**
 * Singleton class for geolocation resource.
 */
public enum Resources {
    INSTANCE;

    public GeoLocation geoLocation;

    private Resources() {
        geoLocation = new GeoLocation();
    }
}
