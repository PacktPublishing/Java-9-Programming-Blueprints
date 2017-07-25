package com.steeplesoft.deskdroid.model;

/**
 *
 * @author jason
 */
public class Participant {
    private String name;
    private String phoneNumber;
    private String thumbnail;
    private boolean hasThumbnail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        hasThumbnail = true;
    }
    
    public boolean hasThumbnail() {
        return hasThumbnail;
    }
}
