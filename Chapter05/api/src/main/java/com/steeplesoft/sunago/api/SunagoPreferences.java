package com.steeplesoft.sunago.api;

/**
 *
 * @author jason
 */
public interface SunagoPreferences {
    String getPreference(String key);
    String getPreference(String key, String defaultValue);
    Integer getPreference(String key, Integer defaultValue);
    void putPreference(String key, String value);
    void putPreference(String key, Integer value);
}
