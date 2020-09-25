package ru.zilzilok.ict.utils.resources;

import ru.zilzilok.ict.utils.resources.geolocation.GeoLocation;

public enum Resources {
    INSTANCE;

    public GeoLocation geoLocation;

    private Resources() {
        geoLocation = new GeoLocation();
    }
}
