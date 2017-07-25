package com.steeplesoft.sunago.twitter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author jason
 */
public class MessageBundle {
    ResourceBundle messages = ResourceBundle.getBundle("Messages", Locale.getDefault());

    private MessageBundle() {

    }

    public final String getString(String key) {
        return messages.getString(key);
    }
    
    private static class LazyHolder {
        private static final MessageBundle INSTANCE = new MessageBundle();
    }

    public static MessageBundle getInstance() {
        return LazyHolder.INSTANCE;
    }
}
