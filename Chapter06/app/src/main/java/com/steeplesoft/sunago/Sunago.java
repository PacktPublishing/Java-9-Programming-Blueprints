package com.steeplesoft.sunago;

import android.app.Application;
import android.content.Context;

public class Sunago extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Sunago.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Sunago.context;
    }}
