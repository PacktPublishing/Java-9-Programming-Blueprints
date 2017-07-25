package com.steeplesoft.photobeans.manager.impl;

/**
 *
 * @author jason
 */
public class Photo {
    private final String sourceDir;
    private final int year;
    private final int month;
    private final String image;

    public Photo(String sourceDir, int year, int month, String image) {
        this.sourceDir = sourceDir;
        this.year = year;
        this.month = month;
        this.image = image;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public String getImage() {
        return image;
    }
}
