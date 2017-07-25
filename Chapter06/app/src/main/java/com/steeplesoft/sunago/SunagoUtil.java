package com.steeplesoft.sunago;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jason on 12/26/2016.
 */

public class SunagoUtil {
    private static SharedPreferences prefs;

    public static SharedPreferences getPreferences() {
        if (prefs == null) {
            prefs = Sunago.getAppContext().getSharedPreferences(Sunago.getAppContext().getPackageName(), Context.MODE_PRIVATE);
        }
        return prefs;
    }
}
