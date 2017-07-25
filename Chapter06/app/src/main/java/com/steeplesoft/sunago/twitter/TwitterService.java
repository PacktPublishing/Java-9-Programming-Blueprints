package com.steeplesoft.sunago.twitter;

import android.app.IntentService;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.steeplesoft.sunago.MainActivity;
import com.steeplesoft.sunago.R;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.api.SocialMediaItem;
import com.steeplesoft.sunago.data.SunagoContentProvider;

import java.util.ArrayList;
import java.util.List;

public class TwitterService extends IntentService {
    private BroadcastReceiver receiver;

    public TwitterService() {
        super("TwitterService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        receiver = new TwitterServiceReceiver();
        registerReceiver(receiver, new IntentFilter("sunago.service"));
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(receiver);
        return super.onUnbind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    private class TwitterUpdatesAsyncTask extends DataLoadAsyncTask {
        @Override
        protected List<ContentValues> doInBackground(Void... contexts) {
            return processItems(TwitterClient.instance().getItems());
        }
    }

    private class TwitterServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("REFRESH".equals(intent.getStringExtra("message"))) {
                if (SunagoUtil.getPreferences().getBoolean(getString(R.string.twitter_authd), false)) {
                    new TwitterUpdatesAsyncTask().execute();
                }
            }
        }
    }
}
