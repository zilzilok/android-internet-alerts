package ru.zilzilok.android_internet_alerts.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.intellij.lang.annotations.Language;

import java.util.List;

import ru.zilzilok.android_internet_alerts.utils.AlertState;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table checkedStates (id integer primary key autoincrement,name text,email text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void updateDatabase(@NonNull List<AlertState> alertStates) {

    }
}
