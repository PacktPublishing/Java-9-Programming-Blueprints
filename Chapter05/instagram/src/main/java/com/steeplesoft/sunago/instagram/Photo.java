package com.steeplesoft.sunago.instagram;

import com.steeplesoft.sunago.api.SocialMediaItem;
import java.util.Date;
import org.jinstagram.entity.users.feed.MediaFeedData;

/**
 *
 * @author jason
 */
public class Photo implements SocialMediaItem {
    private final MediaFeedData data;
    public Photo(MediaFeedData data) {
        this.data = data;
    }
    
    public String getId() {
        return data.getId();
    }
    
    @Override
    public String getProvider() {
        return "Instagram";
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public String getBody() {
        
        return String.format("%s: %s (%s)", data.getUser().getFullName(),
                data.getCaption().getText(),getTimestamp().toString());
    }

    @Override
    public String getUrl() {
        return data.getLink();
    }

    @Override
    public Date getTimestamp() {
        return new Date(Long.parseLong(data.getCreatedTime())*1000);
    }

    @Override
    public String getImage() {
        return data.getImages().getThumbnail().getImageUrl();
    }
}
