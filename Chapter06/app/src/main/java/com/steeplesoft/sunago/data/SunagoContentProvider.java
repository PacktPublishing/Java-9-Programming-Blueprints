package com.steeplesoft.sunago.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.steeplesoft.sunago.data.SunagoOpenHelper;

public class SunagoContentProvider extends ContentProvider {
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String DBNAME = "sunago";
    private SunagoOpenHelper openHelper;

    private static final String PROVIDER_NAME = "com.steeplesoft.sunago.SunagoProvider";
    private static final String CONTENT_URL = "content://" + PROVIDER_NAME + "/items";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URL);

    public static final String _ID = "_id";
    public static final String PROVIDER = "provider";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String URL = "url";
    public static final String IMAGE = "image";
    public static final String TIMESTAMP = "timestamp";

    private static final int ITEM = 1;
    private static final int ITEM_ID = 2;
    static {
        URI_MATCHER.addURI(PROVIDER_NAME, "items", ITEM);
        URI_MATCHER.addURI(PROVIDER_NAME, "items/#", ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        openHelper = new SunagoOpenHelper(getContext(), DBNAME, null, 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case 2:
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query("items", projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        long rowID = db.insert("items", "", values);

        if (rowID > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
