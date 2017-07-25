package com.steeplesoft.sunago.instagram;

import android.content.SharedPreferences;

import com.steeplesoft.sunago.R;
import com.steeplesoft.sunago.Sunago;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.api.SocialMediaClient;
import com.steeplesoft.sunago.api.SocialMediaItem;

import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jason
 */
public final class InstagramClient implements SocialMediaClient {

    private static InstagramClient instance;
    private final InstagramService service;
    private Instagram instagram;
    private final SharedPreferences prefs = SunagoUtil.getPreferences();
    private boolean isAuthenticated = false;
    private final Map<String, String> sinceForUser = new HashMap<>();

    private InstagramClient() {
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
        final String token = prefs.getString(Sunago.getAppContext().getString(R.string.instagram_token), null);
        final String tokenSecret = prefs.getString(Sunago.getAppContext().getString(R.string.instagram_token_secret), null);
        if (token != null && tokenSecret != null) {
            authenticateUser(token, tokenSecret);
        }
    }

    public static InstagramClient instance() {
        if (instance == null) {
            instance = new InstagramClient();
        }

        return instance;
    }

    @Override
    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    @Override
    public void authenticateUser(String token, String tokenSecret) {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Sunago.getAppContext().getString(R.string.instagram_token), token);
        editor.putString(Sunago.getAppContext().getString(R.string.instagram_token_secret), tokenSecret);
        editor.commit();
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
            for (UserFeedData userList : instagram.getUserFollowList("self").getUserList()) {
            userList.setId("self");
                items.addAll(processMediaForUser(userList));
            }
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
            final List<MediaFeedData> data = instagram.getRecentMediaFeed(id,
                    prefs.getInt(Sunago.getAppContext().getString(R.string.item_count), 50),
                    getSinceForUser(id), null, null, null).getData();
            for (MediaFeedData m : data) {
                userMedia.add(new Photo(m));
            }
            if (!userMedia.isEmpty()) {
                setSinceForUser(id, userMedia.get(0).getId()); // First in list is oldest
            }
        } catch (InstagramException ex) {
            Logger.getLogger(InstagramClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userMedia;
    }

    private String getSinceForUser(String user) {
        return SunagoUtil.getPreferences().getString("instagram_since_"+user, "1");
    }

    private void setSinceForUser(String user, String since) {
        SharedPreferences.Editor editor = SunagoUtil.getPreferences().edit();
        editor.putString("instagram_since_"+user, since);
        editor.commit();
    }
}
