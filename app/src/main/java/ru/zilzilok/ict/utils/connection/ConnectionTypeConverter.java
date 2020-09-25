package ru.zilzilok.ict.utils.connection;

import androidx.annotation.NonNull;

/**
 * Abstract class for converting connection types identically.
 */
public abstract class ConnectionTypeConverter {
    public static final class TYPES {
        public static final String WIFI = "wifi";
        public static final String NO_INTERNET = "no_internet";
        public static final String _2G = "2g";
        public static final String _3G = "3g";
        public static final String _4G = "4g";
    }

    public static String get(@NonNull String conType) {
        switch (conType) {
            case "WIFI":
            case TYPES.WIFI:
                return TYPES.WIFI;
            case "Нет Интернета":
            case "No Internet":
            case TYPES.NO_INTERNET:
                return TYPES.NO_INTERNET;
            case "2G/E":
            case TYPES._2G:
                return TYPES._2G;
            case "3G/H/H+":
            case TYPES._3G:
                return TYPES._3G;
            case "4G/LTE":
            case TYPES._4G:
                return TYPES._4G;
            default:
                return "not_found";
        }
    }
}
