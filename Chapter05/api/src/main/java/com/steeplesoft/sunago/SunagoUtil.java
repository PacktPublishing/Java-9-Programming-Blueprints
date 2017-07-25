package com.steeplesoft.sunago;

import com.steeplesoft.sunago.api.SunagoPreferences;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author jason
 */
public class SunagoUtil {
    private static SunagoPreferences preferences;

    public static synchronized SunagoPreferences getSunagoPreferences() {
        if (preferences == null) {
            ServiceLoader<SunagoPreferences> spLoader = ServiceLoader.load(SunagoPreferences.class);
            Iterator<SunagoPreferences> iterator = spLoader.iterator();
            preferences = iterator.hasNext() ? iterator.next() : null;
        }
        return preferences;
    }
}
