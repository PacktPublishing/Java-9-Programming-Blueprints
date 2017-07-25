package com.steeplesoft.sunago.api;

import java.io.Serializable;
import java.util.Date;

public interface SocialMediaItem extends Serializable {
    String getProvider();
    String getTitle();
    String getBody();
    String getUrl();
    String getImage();
    Date getTimestamp();
}
