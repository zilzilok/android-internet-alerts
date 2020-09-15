package ru.zilzilok.ict.utils.database.data;

import android.provider.BaseColumns;

public class StatisticsContract {
    public static final class ConnectionInfo implements BaseColumns {
        public final static String TABLE_NAME = "connection_info";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_APPEARED = "appeared";
        public final static String COLUMN_SELECTED = "selected";
    }
}
