package com.steeplesoft.sunago.twitter;

import com.steeplesoft.sunago.SunagoPrefsKeys;
import com.steeplesoft.sunago.api.SocialMediaItem;
import com.steeplesoft.sunago.api.SunagoPreferences;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.api.SocialMediaClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public final class TwitterClient implements SocialMediaClient {

    private static final long HOMETIMELINE = -1;
    private final static Twitter twitter = TwitterFactory.getSingleton();
    private static TwitterClient instance;
    private final SunagoPreferences prefs;
    private boolean isAuthenticated = false;
    private RequestToken requestToken;
    private final Map<Long, Long> sinceForList = new HashMap<>();

    static {
        Properties props = new Properties();
        try (InputStream input = TwitterClient.class.getResourceAsStream("/twitter.properties")) {
            props.load(input);
        } catch (IOException ex) {
        }
        //TODO: Do something smarter
        twitter.setOAuthConsumer(props.getProperty("apiKey"), props.getProperty("apiSecret"));
    }

    public static TwitterClient instance() {
        if (instance == null) {
            instance = new TwitterClient();
        }

        return instance;
    }

    public TwitterClient() {
        prefs = SunagoUtil.getSunagoPreferences();
        final String token = prefs.getPreference(TwitterPrefsKeys.TOKEN.getKey());
        if (token != null) {
            authenticateUser(token, prefs.getPreference(TwitterPrefsKeys.TOKEN_SECRET.getKey()));
        }
    }

    @Override
    public String getAuthorizationUrl() {
        try {
            return getOAuthRequestToken().getAuthorizationURL();
        } catch (TwitterException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void authenticateUser(String token, String tokenSecret) {
        twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));
        try {
            twitter.verifyCredentials();
            isAuthenticated = true;
        } catch (TwitterException | IllegalStateException ex) {
        }
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public List<SocialMediaItem> getItems() {
        List<SocialMediaItem> tweets = new ArrayList<>();

        if (Boolean.parseBoolean(prefs.getPreference(TwitterPrefsKeys.HOME_TIMELINE.getKey(), "false"))) {
            tweets.addAll(processList(HOMETIMELINE));
        }
        getSelectedLists(prefs).forEach(l -> {
            tweets.addAll(processList(l));
        });

        return tweets;
    }

    public AccessToken getAcccessToken(RequestToken requestToken, String pin) throws TwitterException {
        return twitter.getOAuthAccessToken(requestToken, pin);
    }

    public RequestToken getOAuthRequestToken() throws TwitterException {
        if (requestToken == null) {
            requestToken = twitter.getOAuthRequestToken();
        }
        return requestToken;
    }

    public List<UserList> getLists() {
        try {
            return twitter.getUserLists(twitter.getId());
        } catch (TwitterException | IllegalStateException ex) {
            Logger.getLogger(TwitterClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.emptyList();
    }

    public List<Long> getSelectedLists(final SunagoPreferences prefs) {
        String selectedLists = prefs.getPreference(TwitterPrefsKeys.SELECTED_LISTS.getKey());
        return (selectedLists != null)
                ? Arrays.asList(selectedLists.split(",")).stream().map(s -> Long.parseLong(s)).collect(Collectors.toList())
                : Collections.emptyList();
    }

    private List<Tweet> processList(long listId) {
        List<Tweet> tweets = new ArrayList<>();

        try {
            final AtomicLong sinceId = new AtomicLong(getSinceId(listId));
            final Paging paging = new Paging(1,
                    prefs.getPreference(SunagoPrefsKeys.ITEM_COUNT.getKey(), 50),
                    sinceId.get());
            List<Status> statuses = (listId == HOMETIMELINE) ? twitter.getHomeTimeline(paging)
                    : twitter.getUserListStatuses(listId, paging);
            statuses.forEach(s -> {
                if (s.getId() > sinceId.get()) { // TODO: always reverse chrono?
                    sinceId.set(s.getId());
                }
                tweets.add(new Tweet(s));
            });
            saveSinceId(listId, sinceId.get());
        } catch (TwitterException ex) {
            Logger.getLogger(TwitterClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tweets;
    }

    private long getSinceId(long listId) {
        Long since = sinceForList.get(listId);
        return since != null ? since : 1;
    }

    private void saveSinceId(long listId, long sinceId) {
        sinceForList.put(listId, sinceId);
    }
}
