package com.steeplesoft.cloudnotice.api;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class CloudNoticeDaoTest {
    private final CloudNoticeDAO dao = new CloudNoticeDAO(true);

    @Test
    public void getRecipients() {
        dao.getRecipients();
    }
    
    @Test
    public void addRecipient() {
        Recipient recip = new Recipient("SMS", "test@example.com");
        dao.saveRecipient(recip);
        List<Recipient> recipients = dao.getRecipients();
        Assert.assertEquals(1, recipients.size());
    }
    
    @Test
    public void deleteRecipient() {
        Recipient recip = new Recipient("SMS", "test2@example.com");
        
        dao.saveRecipient(recip);
        Assert.assertEquals(1, dao.getRecipients().size());
        
        dao.deleteRecipient(recip);
        Assert.assertEquals(0, dao.getRecipients().size());
    }
}
