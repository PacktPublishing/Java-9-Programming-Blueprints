package com.steeplesoft.sunago.instagram;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import com.steeplesoft.sunago.MainActivity;
import com.steeplesoft.sunago.R;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.api.MessageHandler;
import com.steeplesoft.sunago.api.SocialMediaItem;
import com.steeplesoft.sunago.data.SunagoContentProvider;
import com.steeplesoft.sunago.twitter.DataLoadAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class InstagramService extends IntentService {
    private BroadcastReceiver receiver;

    public InstagramService() {
        super("InstagramService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        receiver = new InstagramServiceReceiver();
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

    private class InstagramUpdatesAsyncTask extends DataLoadAsyncTask {
        @Override
        protected List<ContentValues> doInBackground(Void... voids) {
            return processItems(InstagramClient.instance().getItems());
        }
    }

    private class InstagramServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("REFRESH".equals(intent.getStringExtra("message"))) {
                if (SunagoUtil.getPreferences().getBoolean(getString(R.string.instagram_authd), false)) {
                    new InstagramUpdatesAsyncTask().execute();
                }
            }
        }
    }
}
