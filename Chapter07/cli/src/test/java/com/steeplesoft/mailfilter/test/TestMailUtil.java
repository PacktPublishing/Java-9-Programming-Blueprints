package com.steeplesoft.mailfilter.test;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author jason
 */
public class TestMailUtil {

    private static final String TEST_RECIP = "to@localhost.com";
    private static TestMailUtil INSTANCE;
    private GreenMail greenMail;

    public static TestMailUtil instance() {
        if (INSTANCE == null) {
            INSTANCE = new TestMailUtil();
        }
        return INSTANCE;
    }

    private TestMailUtil() {
        greenMail = new GreenMail(ServerSetup.ALL);
        greenMail.start();
    }

    public void createTestMessages() {
        try {
            GreenMailUser user = greenMail.setUser(TEST_RECIP, "password");

            final String subject = GreenMailUtil.random();
            final String body = GreenMailUtil.random();
            user.deliver(createMimeMessage("themaster@timelords.com", subject, body, null));
            user.deliver(createMimeMessage("ad1@adco.net", "Great stuff cheap!", "body", null));
            user.deliver(createMimeMessage("newsletter@spam.com", "Happening this month", "stuff", null));
            user.deliver(createMimeMessage("pmp@training.net", "Get certified today!", "Cheap!", null));
            user.deliver(createMimeMessage("joe.blow@blargh.net", "It's been a long time!", "Let's talk", null));
            user.deliver(createMimeMessage("themaster@timelords.com", "The Sound of Drums", "boom boom boom", 
                     Date.from(LocalDate.now().minusYears(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));

            Session imapSession = greenMail.getImap().createSession();
            Store store = imapSession.getStore("imap");
            store.connect(TEST_RECIP, "password");
            
            Folder defaultFolder = store.getDefaultFolder();  
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            Folder ads = defaultFolder.getFolder("Ads");
            ads.create(Folder.HOLDS_MESSAGES);   
            
            Folder spam = defaultFolder.getFolder("Spam");
            spam.create(Folder.HOLDS_MESSAGES); 
        } catch (MessagingException ex) {
            Logger.getLogger(TestMailUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private MimeMessage createMimeMessage(String from, String subject, String body, Date date) throws AddressException, MessagingException {
        MimeMessage msg = new MimeMessage(greenMail.getSmtp().createSession());
        msg.setFrom(new InternetAddress(from));
        msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(TEST_RECIP));
        msg.setSubject(subject);
        msg.setText(body);
        msg.setSentDate(date != null ? date : new Date());
        return msg;
    }
}
