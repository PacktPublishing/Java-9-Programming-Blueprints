package com.steeplesoft.photobeans.manager;

import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author jason
 */
public interface PhotoManager extends Lookup.Provider {
    void scanSourceDirs();
    List<String> getYears();
    List<String> getMonths(int year);
    List<String> getPhotos(int year, int month);
}
