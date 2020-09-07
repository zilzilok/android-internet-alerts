package ru.zilzilok.ict.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.zilzilok.ict.utils.connection.ConnectionState;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "connections.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table checkedStates (id integer primary key autoincrement,name text,email text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateDatabase(@NonNull List<ConnectionState> connectionStates) {

    }
}
