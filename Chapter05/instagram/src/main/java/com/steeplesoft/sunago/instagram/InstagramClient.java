package com.steeplesoft.sunago.instagram;

import com.steeplesoft.sunago.SunagoPrefsKeys;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.api.SocialMediaClient;
import com.steeplesoft.sunago.api.SocialMediaItem;
import com.steeplesoft.sunago.api.SunagoPreferences;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

/**
 *
 * @author jason
 */
public final class InstagramClient implements SocialMediaClient {

    private final InstagramService service;
    private Instagram instagram;
    private final SunagoPreferences prefs = SunagoUtil.getSunagoPreferences();
    private boolean isAuthenticated = false;
    private final Map<String, String> sinceForUser = new HashMap<>();

    public InstagramClient() {
        Properties props = new Properties();
        try (InputStream input = InstagramClient.class.getResourceAsStream("/instagram.properties")) {
            props.load(input);
        } catch (IOException ex) {
        }
        service = new InstagramAuthService()
                .apiKey(props.getProperty("apiKey"))
                .apiSecret(props.getProperty("apiSecret"))
                .callback("http://blogs.steeplesoft.com")
                .scope("basic public_content relationships follower_list")
                .build();
        final String token = prefs.getPreference(InstagramPrefsKeys.TOKEN.getKey());
        final String tokenSecret = prefs.getPreference(InstagramPrefsKeys.TOKEN_SECRET.getKey());
        if (token != null && tokenSecret != null) {
            authenticateUser(token, tokenSecret);
        }
    }

    @Override
    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    @Override
    public void authenticateUser(String token, String tokenSecret) {
        prefs.putPreference(InstagramPrefsKeys.TOKEN.getKey(), token);
        prefs.putPreference(InstagramPrefsKeys.TOKEN_SECRET.getKey(), tokenSecret);
        instagram = new Instagram(new Token(token, tokenSecret));
        isAuthenticated = true;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public List<? extends SocialMediaItem> getItems() {
        List<Photo> items = new ArrayList<>();
        try {
            UserFeed follows = instagram.getUserFollowList("self");
            follows.getUserList().forEach(u -> items.addAll(processMediaForUser(u)));
        } catch (InstagramException ex) {
            Logger.getLogger(InstagramClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return items;
    }

    public Token verifyCodeAndGetAccessToken(String token) {
        Verifier verifier = new Verifier(token);
        Token accessToken = service.getAccessToken(verifier);
        return accessToken;
    }

    private List<Photo> processMediaForUser(UserFeedData u) {
        List<Photo> userMedia = new ArrayList<>();
        try {
            final String id = u.getId();
            instagram.getRecentMediaFeed(id,
                    prefs.getPreference(SunagoPrefsKeys.ITEM_COUNT.getKey(), 50),
                    getSinceForUser(id), null, null, null).getData().forEach(m -> userMedia.add(new Photo(m)));
            if (!userMedia.isEmpty()) {
                setSinceForUser(id, userMedia.get(0).getId()); // First in list is oldest
            }
        } catch (InstagramException ex) {
            Logger.getLogger(InstagramClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userMedia;
    }

    private String getSinceForUser(String user) {
        return sinceForUser.containsKey(user) ? sinceForUser.get(user) : "1";
    }

    private void setSinceForUser(String user, String since) {
        sinceForUser.put(user, since);
    }
}
