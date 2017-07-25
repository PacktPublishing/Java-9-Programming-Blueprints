package com.steeplesoft.deskdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.widget.TextView;

import com.steeplesoft.deskdroid.service.DeskDroidService;

import java.util.Random;

public class AuthorizeClientActivity extends AppCompatActivity {
    private BroadcastReceiver messageReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_client);

        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiMgr.getConnectionInfo().getIpAddress());

        String code = Integer.toString(100000 + new Random().nextInt(900000));

        ((TextView) findViewById(R.id.ip)).setText(ipAddress);
        ((TextView) findViewById(R.id.code)).setText(code);

        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                clientAuthenticated();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(
                messageReceiver, new IntentFilter(DeskDroidService.CODE_ACCEPTED));

        Intent intent = new Intent(DeskDroidService.CODE_GENERATED);
        intent.putExtra("code", code);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void clientAuthenticated() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        setResult(2, new Intent());
        finish();
    }
}
