package com.steeplesoft.sunago.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SunagoOpenHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_MAIN = "CREATE TABLE items (" +
            " _id INTEGER PRIMARY KEY, " +
            " provider TEXT, " +
            " title TEXT, " +
            " body TEXT, " +
            " url TEXT, " +
            " image TEXT, " +
            " timestamp TEXT )";

    public SunagoOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
