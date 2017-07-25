package com.steeplesoft.sunago.twitter;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.steeplesoft.sunago.MainActivity;
import com.steeplesoft.sunago.Sunago;
import com.steeplesoft.sunago.api.SocialMediaItem;
import com.steeplesoft.sunago.data.SunagoContentProvider;

import java.util.ArrayList;
import java.util.List;

public abstract class DataLoadAsyncTask extends AsyncTask<Void, Void, List<ContentValues>> {

    protected List<ContentValues> processItems(List<? extends SocialMediaItem> items) {
        List<ContentValues> values = new ArrayList<>();
        for (SocialMediaItem item : items) {
            ContentValues cv = new ContentValues();
            cv.put(SunagoContentProvider.BODY, item.getBody());
            cv.put(SunagoContentProvider.URL, item.getUrl());
            cv.put(SunagoContentProvider.IMAGE, item.getImage());
            cv.put(SunagoContentProvider.PROVIDER, item.getProvider());
            cv.put(SunagoContentProvider.TITLE, item.getTitle());
            cv.put(SunagoContentProvider.TIMESTAMP, item.getTimestamp().getTime());
            values.add(cv);
        }
        return values;
    }

    @Override
    protected void onPostExecute(List<ContentValues> values) {
        Log.i(MainActivity.LOG_TAG, "Inserting " + values.size() + " tweets.");
        Sunago.getAppContext().getContentResolver().bulkInsert(SunagoContentProvider.CONTENT_URI,
                values.toArray(new ContentValues[0]));
    }
}
