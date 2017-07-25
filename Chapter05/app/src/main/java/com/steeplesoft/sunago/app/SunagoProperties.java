package com.steeplesoft.sunago.app;

import com.steeplesoft.sunago.api.SunagoPreferences;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jason
 */
public class SunagoProperties implements SunagoPreferences {
    private Properties props = new Properties();
    private final String FILE = System.getProperty("user.home") + File.separator + ".sunago.properties";

    public SunagoProperties() {
        try (InputStream input = new FileInputStream(FILE)) {
            props.load(input);
        } catch (IOException ex) {
        }
    }

    @Override
    public String getPreference(String key) {
        return props.getProperty(key);
    }

    @Override
    public String getPreference(String key, String defaultValue) {
        String value = props.getProperty(key);
        return (value == null) ? defaultValue : value;
    }

    @Override
    public Integer getPreference(String key, Integer defaultValue) {
        String value = props.getProperty(key);
        return (value == null) ? defaultValue : Integer.parseInt(value);
    }

    @Override
    public void putPreference(String key, String value) {
        props.put(key, value);
        store();
    }

    @Override
    public void putPreference(String key, Integer value) {
        if (value != null) {
            putPreference(key, value.toString());
        }
    }

    private void store() {
        try (FileOutputStream output = new FileOutputStream(FILE)) {
            props.store(output, null);
        } catch (IOException e) {
        }
    }
}
