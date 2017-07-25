/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.sunago.app;

import com.steeplesoft.sunago.api.SunagoPreferences;
import java.util.prefs.Preferences;

/**
 *
 * @author jason
 */
public class SunagoPreferencesImpl implements SunagoPreferences {
    private final Preferences prefs = Preferences.userRoot()
            .node(SunagoPreferencesImpl.class.getPackage().getName());

    @Override
    public String getPreference(String key) {
        return prefs.get(key, null);
    }

    @Override
    public String getPreference(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }

    @Override
    public Integer getPreference(String key, Integer defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    @Override
    public void putPreference(String key, String value) {
        prefs.put(key, value);
    }

    @Override
    public void putPreference(String key, Integer value) {
        prefs.putInt(key, value);
    }
}
