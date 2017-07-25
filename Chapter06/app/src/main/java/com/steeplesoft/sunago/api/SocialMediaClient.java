package com.steeplesoft.sunago.api;

import java.util.List;

/**
 *
 * @author jason
 */
public interface SocialMediaClient {
    void authenticateUser(String token, String tokenSecret);
    String getAuthorizationUrl();
    List<? extends SocialMediaItem> getItems();
    boolean isAuthenticated();
}
