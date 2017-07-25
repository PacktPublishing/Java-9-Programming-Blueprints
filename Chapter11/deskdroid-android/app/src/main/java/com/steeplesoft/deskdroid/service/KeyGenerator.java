package com.steeplesoft.deskdroid.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.steeplesoft.deskdroid.R;

import java.security.Key;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jason on 4/22/2017.
 */

public class KeyGenerator {
    private static Key key;
    private static final Object lock = new Object();

    public static Key getKey(Context context) {
        synchronized (lock) {
            if (key == null) {
                SharedPreferences sharedPref = context.getSharedPreferences(
                        context.getString(R.string.preference_deskdroid), Context.MODE_PRIVATE);
                String signingKey = sharedPref.getString(context.getString(R.string.preference_signing_key), null);
                if (signingKey == null) {
                    signingKey = UUID.randomUUID().toString();
                    final SharedPreferences.Editor edit = sharedPref.edit();
                    edit.putString(context.getString(R.string.preference_signing_key), signingKey);
                    edit.commit();
                }
                key = new SecretKeySpec(signingKey.getBytes(), 0, signingKey.getBytes().length, "DES");
            }
        }

        return key;
    }
}