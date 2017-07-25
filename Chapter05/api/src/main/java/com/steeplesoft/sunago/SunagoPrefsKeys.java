package com.steeplesoft.sunago;

/**
 *
 * @author jason
 */
public enum SunagoPrefsKeys {
    ITEM_COUNT("itemCount");
    private final String key;

    SunagoPrefsKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return "sunago." + key;
    }

}
