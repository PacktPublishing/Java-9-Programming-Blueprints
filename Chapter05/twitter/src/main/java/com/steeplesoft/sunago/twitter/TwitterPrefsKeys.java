package com.steeplesoft.sunago.twitter;

/**
 *
 * @author jason
 */
public enum TwitterPrefsKeys {
    HOME_TIMELINE("showHomeTimeline"), 
    SELECTED_LISTS("selectedLists"), 
    SINCE_ID("sinceId"),
    TOKEN("token"), 
    TOKEN_SECRET("tokenSecret");
    private final String key;

    TwitterPrefsKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return "twitter." + key;
    }

}
