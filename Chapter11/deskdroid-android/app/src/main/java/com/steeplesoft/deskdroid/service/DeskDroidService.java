package com.steeplesoft.deskdroid.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;
import android.util.Log;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

public final class DeskDroidService extends Service {
    public static final String CODE_GENERATED = "code-generated";
    public static final String CODE_ACCEPTED = "code-accepted";
    public static final String SERVER_STATUS_CHANGE = "status-changed";

//    private static Server server;
    private static HttpServer server;
    protected BroadcastReceiver messageReceiver;
    protected String code;
    private static final Object lock = new Object();

    public DeskDroidService() {

    }

    public static boolean isRunning() {
        return server != null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        synchronized (lock) {
            if (server == null) {
                startServer();
                Intent statusIntent = new Intent(DeskDroidService.SERVER_STATUS_CHANGE);
                statusIntent.putExtra("running", true);
                LocalBroadcastManager.getInstance(this).sendBroadcast(statusIntent);
                messageReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String code = intent.getStringExtra("code");
                        DeskDroidService.this.code = code;
                        Log.d("receiver", "Got code: " + code);
                    }
                };
                LocalBroadcastManager.getInstance(this).registerReceiver(
                        messageReceiver, new IntentFilter(CODE_GENERATED));
            }
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (server != null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
//                server.stop();
                server.shutdownNow();
                server = null;
                Intent statusIntent = new Intent(DeskDroidService.SERVER_STATUS_CHANGE);
                statusIntent.putExtra("running", false);
                LocalBroadcastManager.getInstance(this).sendBroadcast(statusIntent);
            } catch (Exception e) {
            }
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startServer() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext()
                .getSystemService(Service.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) {
            // Deprecated. Does not support ipv6. *shrug* :)
            String ipAddress = Formatter.formatIpAddress(wifiMgr.getConnectionInfo()
                    .getIpAddress());

            URI baseUri = UriBuilder.fromUri("http://" + ipAddress)
                    .port(49152)
                    .build();
            ResourceConfig config = new ResourceConfig(SseFeature.class)
                    .register(JacksonFeature.class);
            config.registerInstances(new SecureFilter(this));
            config.registerInstances(new DeskDroidResource(this));
//            server = JettyHttpContainerFactory.createServer(baseUri, config);
            server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        }
    }

}
