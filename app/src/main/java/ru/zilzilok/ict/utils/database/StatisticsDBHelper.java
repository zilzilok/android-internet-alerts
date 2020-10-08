package ru.zilzilok.ict.utils.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.activities.MainActivity;
import ru.zilzilok.ict.utils.connection.ConnectionState;
import ru.zilzilok.ict.utils.connection.ConnectionTypeConverter;
import ru.zilzilok.ict.utils.database.data.StatisticsContract;
import ru.zilzilok.ict.utils.resources.ResourceNotAvailableException;
import ru.zilzilok.ict.utils.resources.Resources;
import ru.zilzilok.ict.utils.resources.geolocation.GeoLocation;

/**
 * Class that helps to work with statistics database.
 */
public class StatisticsDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "GeolocationInfoDBHelper";

    public StatisticsDBHelper() {
        super(MainActivity.getContext(), StatisticsContract.DATABASE_NAME, null, StatisticsContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String funcName = "[onCreate]";
        Log.i(TAG, String.format("%s Database %s was created.", funcName, StatisticsContract.DATABASE_NAME));

        String SQL_CREATE_GEOLOCATION_TABLE = "CREATE TABLE " + StatisticsContract.GeolocationInfo.TABLE_NAME + " ("
                + StatisticsContract.GeolocationInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StatisticsContract.GeolocationInfo.COLUMN_NAME + " TEXT NOT NULL, "
                + StatisticsContract.GeolocationInfo.COLUMN_LAST_LATITUDE + " REAL NOT NULL DEFAULT 0.0,"
                + StatisticsContract.GeolocationInfo.COLUMN_LAST_LONGITUDE + " REAL NOT NULL DEFAULT 0.0);";

        String SQL_CREATE_CONNECTION_TABLE = "CREATE TABLE " + StatisticsContract.ConnectionInfo.TABLE_NAME + " ("
                + StatisticsContract.ConnectionInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StatisticsContract.ConnectionInfo.COLUMN_NAME + " TEXT NOT NULL, "
                + StatisticsContract.ConnectionInfo.COLUMN_APPEARED + " INTEGER NOT NULL DEFAULT 0, "
                + StatisticsContract.ConnectionInfo.COLUMN_SELECTED + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_GEOLOCATION_TABLE);
        db.execSQL(SQL_CREATE_CONNECTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * @param isSelected true if you need info about selected connection type, false otherwise
     * @return address if we know any coordinates, "no data" otherwise
     */
    public String getGeolocation(boolean isSelected) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                StatisticsContract.GeolocationInfo._ID,
                StatisticsContract.GeolocationInfo.COLUMN_NAME,
                StatisticsContract.GeolocationInfo.COLUMN_LAST_LATITUDE,
                StatisticsContract.GeolocationInfo.COLUMN_LAST_LONGITUDE};

        String name = getGeolocationTableName(isSelected);

        Cursor cursor = db.query(
                StatisticsContract.GeolocationInfo.TABLE_NAME,
                projection,
                StatisticsContract.GeolocationInfo.COLUMN_NAME + " = ?", new String[]{name},
                null,
                null,
                null,
                null);

        Double latitude;
        Double longitude;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            latitude = cursor.getDouble(2);
            longitude = cursor.getDouble(3);
            cursor.close();
            return GeoLocation.getGeolocationByCoordinates(MainActivity.getContext(), latitude, longitude);
        }
        return MainActivity.getContext().getResources().getString(R.string.geolocation_default_value);
    }

    /**
     * @param isSelected true if you need info about selected connection type, false otherwise
     * @return array of size 2 with last coordinates (latitude and longitude)
     */
    public Double[] getCoordinates(boolean isSelected) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                StatisticsContract.GeolocationInfo._ID,
                StatisticsContract.GeolocationInfo.COLUMN_NAME,
                StatisticsContract.GeolocationInfo.COLUMN_LAST_LATITUDE,
                StatisticsContract.GeolocationInfo.COLUMN_LAST_LONGITUDE};

        String name = getGeolocationTableName(isSelected);

        Cursor cursor = db.query(
                StatisticsContract.GeolocationInfo.TABLE_NAME,
                projection,
                StatisticsContract.GeolocationInfo.COLUMN_NAME + " = ?", new String[]{name},
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            double latitude = cursor.getDouble(2);
            double longitude = cursor.getDouble(3);
            cursor.close();
            return new Double[]{latitude, longitude};
        }
        return null;
    }

    /**
     * @param name       of connection type
     * @param isSelected true if you need info about selected connection type, false otherwise
     * @return quantity of selected/appeared connection
     */
    public int getConnectionInt(String name, boolean isSelected) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                StatisticsContract.ConnectionInfo._ID,
                StatisticsContract.ConnectionInfo.COLUMN_NAME,
                StatisticsContract.ConnectionInfo.COLUMN_APPEARED,
                StatisticsContract.ConnectionInfo.COLUMN_SELECTED};

        Cursor cursor = db.query(
                StatisticsContract.ConnectionInfo.TABLE_NAME,
                projection,
                StatisticsContract.ConnectionInfo.COLUMN_NAME + " = ?", new String[]{name},
                null,
                null,
                null,
                null);

        int output = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            output = cursor.getInt(isSelected ? 3 : 2);
            cursor.close();
        }
        return output;
    }

    /**
     * @param ascend if true then in ascending order, else in descending order
     * @return appeared connection types by specific order
     */
    public List<String> getAppearedConnectionsNameByOrder(boolean ascend) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                StatisticsContract.ConnectionInfo._ID,
                StatisticsContract.ConnectionInfo.COLUMN_NAME,
                StatisticsContract.ConnectionInfo.COLUMN_APPEARED,
                StatisticsContract.ConnectionInfo.COLUMN_SELECTED};

        Cursor cursor = db.query(
                StatisticsContract.ConnectionInfo.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                StatisticsContract.ConnectionInfo.COLUMN_SELECTED + (ascend ? " ASC" : " DESC"));

        List<String> res = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                res.add(ConnectionTypeConverter.getFull(cursor.getString(1)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return res;
    }

    /**
     * Updates database.
     *
     * @param connectionStates updated connection states
     * @param isSelected       true if you need info about selected connection type, false otherwise
     */
    public void updateDatabase(@NonNull List<ConnectionState> connectionStates, boolean isSelected) {
        updateConnectionTable(connectionStates, isSelected);
        updateGeolocationTable(isSelected);
    }

    private void updateConnectionTable(@NonNull List<ConnectionState> connectionStates, boolean isSelected) {
        String funcName = "[updateDatabase]";

        SQLiteDatabase db = this.getWritableDatabase();
        for (ConnectionState cs : connectionStates) {
            String name = ConnectionTypeConverter.getShort(cs.getConnectionType());
            ContentValues cv = new ContentValues();
            if (isSelected) {
                cv.put(StatisticsContract.ConnectionInfo.COLUMN_SELECTED, getConnectionInt(name, true) + 1);
            } else {
                cv.put(StatisticsContract.ConnectionInfo.COLUMN_APPEARED, getConnectionInt(name, false) + 1);
            }
            int res = db.update(
                    StatisticsContract.ConnectionInfo.TABLE_NAME,
                    cv,
                    StatisticsContract.ConnectionInfo.COLUMN_NAME + "= ?",
                    new String[]{name});
            if (res == 0) {
                cv.put(StatisticsContract.ConnectionInfo.COLUMN_NAME, name);
                db.insert(
                        StatisticsContract.ConnectionInfo.TABLE_NAME,
                        null,
                        cv);
            }
        }

        Log.i(TAG, String.format("%s Database %s was updated.", funcName, StatisticsContract.DATABASE_NAME));
    }

    private void updateGeolocationTable(boolean isSelected) {
        String funcName = "[updateDatabase]";
        try {
            Double latitude = Resources.INSTANCE.geoLocation.getLatitude();
            Double longitude = Resources.INSTANCE.geoLocation.getLongitude();

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            String name = getGeolocationTableName(isSelected);
            cv.put(StatisticsContract.GeolocationInfo.COLUMN_LAST_LATITUDE, latitude);
            cv.put(StatisticsContract.GeolocationInfo.COLUMN_LAST_LONGITUDE, longitude);
            int res = db.update(
                    StatisticsContract.GeolocationInfo.TABLE_NAME,
                    cv,
                    StatisticsContract.GeolocationInfo.COLUMN_NAME + "= ?",
                    new String[]{name});
            if (res == 0) {
                cv.put(StatisticsContract.GeolocationInfo.COLUMN_NAME, name);
                db.insert(
                        StatisticsContract.GeolocationInfo.TABLE_NAME,
                        null,
                        cv);
            }
            Log.i(TAG, String.format("%s Database %s was updated.", funcName, StatisticsContract.DATABASE_NAME));
        } catch (ResourceNotAvailableException ignore) {
            Log.i(TAG, String.format("%s Database %s wasn't updated.", funcName, StatisticsContract.DATABASE_NAME));
        }
    }

    private String getGeolocationTableName(boolean isSelected) {
        return isSelected ? "last_selected" : "last_appeared";
    }
}
