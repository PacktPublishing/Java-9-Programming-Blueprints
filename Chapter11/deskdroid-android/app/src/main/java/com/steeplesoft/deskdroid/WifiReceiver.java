package com.steeplesoft.deskdroid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.steeplesoft.deskdroid.service.DeskDroidService;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext()
                .getSystemService(Service.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) {
            context.startService(new Intent(context, DeskDroidService.class));
        } else {
            context.stopService(new Intent(context, DeskDroidService.class));
        }
    }
}
