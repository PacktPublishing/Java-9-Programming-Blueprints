package com.steeplesoft.sunago;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.steeplesoft.sunago.data.SunagoContentProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String PLUGIN_ACTION = "com.steeplesoft.sunago.intent.plugin";
    public static final String LOG_TAG = "SUNAGO";
    private final List<ComponentName> plugins = new ArrayList<>();
    private final List<PluginServiceConnection> pluginServiceConnections = new ArrayList<>();
    private SunagoCursorAdapter adapter;
    private static final String[] ITEM_PROJECTION = new String[]{
            SunagoContentProvider._ID,
            SunagoContentProvider.PROVIDER,
            SunagoContentProvider.BODY,
            SunagoContentProvider.TITLE,
            SunagoContentProvider.URL,
            SunagoContentProvider.IMAGE,
            SunagoContentProvider.TIMESTAMP
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable()) {
            showErrorDialog("A valid internet connection can't be established");
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            findPlugins();

            adapter = new SunagoCursorAdapter(this, null, 0);
            final ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Cursor c = (Cursor) adapterView.getItemAtPosition(position);
                    String url = c.getString(c.getColumnIndex(SunagoContentProvider.URL));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });

                getLoaderManager().initLoader(0, null, this);
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error!");
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.alert_dark_frame);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });

        alertDialog.show();
    }

    public boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : cm.getAllNetworks()) {
            NetworkInfo networkInfo = cm.getNetworkInfo(network);
            if (networkInfo.isConnected() == true) {
                connected = true;
                break;
            }
        }
        return connected;
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindPluginServices();
    }

    @Override
    protected void onStop() {
        releasePluginServices();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    showPreferencesActivity();
                    return true;
                case R.id.action_refresh:
                    sendRefreshMessage();
                    break;
            }

            return super.onOptionsItemSelected(item);
        }

    private void showPreferencesActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    private void sendRefreshMessage() {
        sendMessage("REFRESH");
    }

    private void sendMessage(String message) {
        Intent intent = new Intent("sunago.service");
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    private void bindPluginServices() {
        for (ComponentName plugin : plugins) {
            Intent intent = new Intent();
            intent.setComponent(plugin);
            PluginServiceConnection conn = new PluginServiceConnection();
            pluginServiceConnections.add(conn);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    private void releasePluginServices() {
        for (PluginServiceConnection conn : pluginServiceConnections) {
            unbindService(conn);
        }
        pluginServiceConnections.clear();
    }

    private void findPlugins() {
        Intent baseIntent = new Intent(PLUGIN_ACTION);
        baseIntent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
        List<ResolveInfo> list = getPackageManager().queryIntentServices(baseIntent,
                PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo rinfo : list) {
            ServiceInfo sinfo = rinfo.serviceInfo;
            if (sinfo != null) {
                plugins.add(new ComponentName(sinfo.packageName, sinfo.name));
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cl = new CursorLoader(this, SunagoContentProvider.CONTENT_URI, ITEM_PROJECTION,
                null, null, SunagoContentProvider.TIMESTAMP + " DESC");
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
