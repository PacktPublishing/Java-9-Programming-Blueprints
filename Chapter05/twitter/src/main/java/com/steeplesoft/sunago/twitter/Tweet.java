package com.steeplesoft.sunago.twitter;

import com.steeplesoft.sunago.api.SocialMediaItem;
import java.util.Date;
import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 *
 * @author jason
 */
public class Tweet implements SocialMediaItem {

    private final Status status;
    private final String url;
    private final String body;

    public Tweet(Status status) {
        this.status = status;
        body = String.format("@%s: %s (%s)", status.getUser().getScreenName(),
                status.getText(), status.getCreatedAt().toString());
        url = String.format("https://twitter.com/%s/status/%d",
                status.getUser().getScreenName(), status.getId());
    }

    @Override
    public String getProvider() {
        return "Twitter";
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Date getTimestamp() {
        return status.getCreatedAt();
    }

    @Override
    public String getImage() {
        MediaEntity[] mediaEntities = status.getMediaEntities();
        if (mediaEntities.length > 0) {
            return mediaEntities[0].getMediaURLHttps();
        } else {
            Status retweetedStatus = status.getRetweetedStatus();
            if (retweetedStatus != null) {
                if (retweetedStatus.getMediaEntities().length > 0) {
                    return retweetedStatus.getMediaEntities()[0].getMediaURLHttps();
                }
            }
        }
        return null;
    }
}
