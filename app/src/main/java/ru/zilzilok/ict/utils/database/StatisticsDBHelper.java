package ru.zilzilok.ict.utils.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import ru.zilzilok.ict.activities.MainActivity;
import ru.zilzilok.ict.utils.connection.ConnectionState;
import ru.zilzilok.ict.utils.connection.ConnectionTypeConverter;
import ru.zilzilok.ict.utils.database.data.StatisticsContract;
import ru.zilzilok.ict.utils.resources.Resources;

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
                + StatisticsContract.GeolocationInfo.COLUMN_LAST_VALUE + " TEXT NOT NULL DEFAULT \"null\");";

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

    public String getGeolocation(boolean isSelected) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                StatisticsContract.GeolocationInfo._ID,
                StatisticsContract.GeolocationInfo.COLUMN_NAME,
                StatisticsContract.GeolocationInfo.COLUMN_LAST_VALUE};

        String name = getGeolocationName(isSelected);

        Cursor cursor = db.query(
                StatisticsContract.GeolocationInfo.TABLE_NAME,
                projection,
                StatisticsContract.GeolocationInfo.COLUMN_NAME + " = ?", new String[]{name},
                null,
                null,
                null,
                null);

        String output = "null";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            output = cursor.getString(2);
            cursor.close();
        }
        return output;
    }

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

    public void updateDatabase(@NonNull List<ConnectionState> connectionStates, boolean isSelected) {
        updateConnectionTable(connectionStates, isSelected);
        updateGeolocationTable(isSelected);
    }

    private void updateConnectionTable(@NonNull List<ConnectionState> connectionStates, boolean isSelected) {
        String funcName = "[updateDatabase]";

        SQLiteDatabase db = this.getWritableDatabase();
        for (ConnectionState cs : connectionStates) {
            String name = ConnectionTypeConverter.get(cs.getConnectionType());
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
        String geolocation = Resources.INSTANCE.geoLocation.toString();

        if(!geolocation.equals("null")) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            String name = getGeolocationName(isSelected);
            cv.put(StatisticsContract.GeolocationInfo.COLUMN_LAST_VALUE, geolocation);
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
        }
        else {
            Log.i(TAG, String.format("%s Database %s wasn't updated.", funcName, StatisticsContract.DATABASE_NAME));
        }
    }

    private String getGeolocationName(boolean isSelected) {
        return isSelected ? "last_selected" : "last_appeared";
    }
}
