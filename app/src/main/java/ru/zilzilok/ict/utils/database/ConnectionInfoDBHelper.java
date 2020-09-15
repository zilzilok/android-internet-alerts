package ru.zilzilok.ict.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import ru.zilzilok.ict.utils.connection.ConnectionState;
import ru.zilzilok.ict.utils.connection.ConnectionTypeConverter;
import ru.zilzilok.ict.utils.database.data.StatisticsContract.ConnectionInfo;

public class ConnectionInfoDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "statistics.db";
    private static final int DATABASE_VERSION = 1;

    public ConnectionInfoDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + ConnectionInfo.TABLE_NAME + " ("
                + ConnectionInfo._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ConnectionInfo.COLUMN_NAME + " TEXT NOT NULL, "
                + ConnectionInfo.COLUMN_APPEARED + " INTEGER NOT NULL DEFAULT 0, "
                + ConnectionInfo.COLUMN_SELECTED + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int getInt(String name, boolean isSelected) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                ConnectionInfo._ID,
                ConnectionInfo.COLUMN_NAME,
                ConnectionInfo.COLUMN_APPEARED,
                ConnectionInfo.COLUMN_SELECTED};

        Cursor cursor = db.query(
                ConnectionInfo.TABLE_NAME,
                projection,
                ConnectionInfo.COLUMN_NAME + " = ?", new String[]{name},
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
        SQLiteDatabase db = this.getWritableDatabase();
        for (ConnectionState cs : connectionStates) {
            String name = ConnectionTypeConverter.get(cs.getConnectionType());
            ContentValues cv = new ContentValues();
            if (isSelected) {
                cv.put(ConnectionInfo.COLUMN_SELECTED, getInt(name, true) + 1);
            } else {
                cv.put(ConnectionInfo.COLUMN_APPEARED, getInt(name, false) + 1);
            }
            int res = db.update(
                    ConnectionInfo.TABLE_NAME,
                    cv,
                    ConnectionInfo.COLUMN_NAME + "= ?",
                    new String[]{name});
            if (res == 0) {
                cv.put(ConnectionInfo.COLUMN_NAME, name);
                db.insert(
                        ConnectionInfo.TABLE_NAME,
                        null,
                        cv);
            }
        }
    }
}