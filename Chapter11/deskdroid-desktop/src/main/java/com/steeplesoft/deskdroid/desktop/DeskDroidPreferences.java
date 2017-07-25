package com.steeplesoft.deskdroid.desktop;

import java.util.prefs.Preferences;

/**
 *
 * @author jason
 */
public class DeskDroidPreferences {
    private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    private static class Holder {
        private static final DeskDroidPreferences INSTANCE = new DeskDroidPreferences();
    }

    public static DeskDroidPreferences getInstance() {
        return Holder.INSTANCE;
    }
    
    private DeskDroidPreferences() {
        
    }
    
    public String getPhoneAddress() {
        return prefs.get("phoneAddress", "");
    }
    
    public void setPhoneAddress(String address) {
        prefs.put("phoneAddress", address);
    }
    
    public String getToken() {
        return prefs.get("token", null);
    }
    
    public void setToken(String token) {
        prefs.put("token", token);
    }
}
