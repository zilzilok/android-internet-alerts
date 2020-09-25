package ru.zilzilok.ict.utils.database.data;

import android.provider.BaseColumns;

/**
 * Class that represent statistics database contract.
 */
public class StatisticsContract {
    public static final String DATABASE_NAME = "statistics.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Columns in ConnectionInfo table.
     */
    public static final class ConnectionInfo implements BaseColumns {
        public static final String TABLE_NAME = "connection_info";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_APPEARED = "appeared";
        public static final String COLUMN_SELECTED = "selected";
    }

    /**
     * Columns in GeolocationInfo table.
     */
    public static final class GeolocationInfo implements BaseColumns {
        public static final String TABLE_NAME = "geolocation_info";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAST_LATITUDE = "latitude";
        public static final String COLUMN_LAST_LONGITUDE = "longitude";
    }
}
