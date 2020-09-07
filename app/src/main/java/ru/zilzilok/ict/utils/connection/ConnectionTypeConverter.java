package ru.zilzilok.ict.utils.connection;

import androidx.annotation.NonNull;

public class ConnectionTypeConverter {
    public static String get(@NonNull String conType) {
        switch (conType){
            case "WIFI":
                return "wifi";
            case "Нет Интернета":
            case "No Internet":
                return "no_internet";
            case "2G/E":
                return "2g";
            case "3G/H/H+":
                return "3g";
            case  "4G/LTE":
                return "4g";
            default:
                return "not_found";
        }
    }
}
