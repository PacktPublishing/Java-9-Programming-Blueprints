package com.steeplesoft.sunago.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.steeplesoft.sunago.MainActivity;
import com.steeplesoft.sunago.R;
import com.steeplesoft.sunago.Sunago;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.api.SocialMediaClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public final class TwitterClient implements SocialMediaClient {
    private static String TWITTER_CALLBACK_URL = "oauth://com.steeplesoft.sunago";
    private static final long HOMETIMELINE = -1;
    private final static Twitter twitter = TwitterFactory.getSingleton();
    private static TwitterClient instance;
    private boolean isAuthenticated = false;
    private static RequestToken requestToken;

    static {
        Properties props = new Properties();
        try (InputStream input = TwitterClient.class.getResourceAsStream("/twitter.properties")) {
            props.load(input);
            //TODO: Do something smarter
            twitter.setOAuthConsumer(props.getProperty("apiKey"), props.getProperty("apiSecret"));
        } catch (IOException ex) {
        }
    }

    public static TwitterClient instance() {
        if (instance == null) {
            instance = new TwitterClient();
        }

        return instance;
    }

    public TwitterClient() {
        Context context = Sunago.getAppContext();
        final String token = SunagoUtil.getPreferences().getString(context.getString(R.string.twitter_oauth_token), null);
        final String secret = SunagoUtil.getPreferences().getString(context.getString(R.string.twitter_oauth_secret), null);
        if (token != null && secret != null) {
            authenticateUser(token, secret);
        }
    }

    public void logout() {
        twitter.setOAuthAccessToken(null);
        instance = null;
        requestToken = null;
    }

    @Override
    public String getAuthorizationUrl() {
        try {
            return getRequestToken().getAuthorizationURL();
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
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public List<Tweet> getItems() {
        List<Tweet> tweets = new ArrayList<>();

        if (SunagoUtil.getPreferences().getBoolean(Sunago.getAppContext().getString(R.string.twitter_show_home_timeline), false)) {
            tweets.addAll(processList(HOMETIMELINE));
        }
        for (Long list : getSelectedLists()) {
            tweets.addAll(processList(list));
        }

        return tweets;
    }

    public AccessToken getAcccessToken(RequestToken requestToken, String pin) throws TwitterException {
        return twitter.getOAuthAccessToken(requestToken, pin);
    }

    public RequestToken getRequestToken() throws TwitterException {
        if (requestToken == null) {
            requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
        }
        return requestToken;
    }

    public List<UserList> getLists() {
        try {
            return twitter.getUserLists(twitter.getId());
        } catch (TwitterException | IllegalStateException ex) {
            Log.e(MainActivity.LOG_TAG, ex.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    public List<Long> getSelectedLists() {
        Set<String> selectedLists = SunagoUtil.getPreferences().getStringSet(
                Sunago.getAppContext().getString(R.string.twitter_selected_lists),
                Collections.emptySet());
        List<Long> lists = new ArrayList<>();

        if (selectedLists != null) {
            for (String list : selectedLists) {
                lists.add(Long.parseLong(list));
            }
        }

        return lists;
    }

    private List<Tweet> processList(long listId) {
        List<Tweet> tweets = new ArrayList<>();

        try {
            final AtomicLong sinceId = new AtomicLong(getSinceId(listId));
            final Paging paging = new Paging(1,
                    SunagoUtil.getPreferences().getInt(
                            Sunago.getAppContext().getString(R.string.item_count), 50),
                    sinceId.get());
            List<Status> statuses = (listId == HOMETIMELINE) ? twitter.getHomeTimeline(paging)
                    : twitter.getUserListStatuses(listId, paging);
            for (Status s : statuses) {
                if (s.getId() > sinceId.get()) {
                    sinceId.set(s.getId());
                }
                tweets.add(new Tweet(s));
            }
            ;
            saveSinceId(listId, sinceId.get());
        } catch (TwitterException ex) {
            Log.e(MainActivity.LOG_TAG, ex.getLocalizedMessage());
        }
        return tweets;
    }

    private long getSinceId(long listId) {
        return SunagoUtil.getPreferences().getLong("twitter_since_" + listId, 1);
    }

    private void saveSinceId(long listId, long sinceId) {
        SharedPreferences.Editor editor = SunagoUtil.getPreferences().edit();
        editor.putLong("twitter_since_" + listId, sinceId);
        editor.commit();
    }
}
