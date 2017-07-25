package com.steeplesoft.mailfilter;

import com.steeplesoft.mailfilter.model.Account;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

public class MailFilter {
    private int moved;
    private int deleted;
    private final String fileName; 

    /**
     * @param args the command line arguments
     */
    public static void main(String... args) {
        try {
            final MailFilter mailFilter = new MailFilter(args.length > 0 ? args[1] : null);
            mailFilter.run();
            System.out.println("\tDeleted count: " + mailFilter.getDeleted());
            System.out.println("\tMove count:    " + mailFilter.getMoved());
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }
    
    public MailFilter() {
        this.fileName = null;
    }
    
    public MailFilter(String fileName) {
        this.fileName = fileName;
    }

public void run() {
    try {
        AccountService service = new AccountService(fileName);

        for (Account account : service.getAccounts()) {
            AccountProcessor processor = new AccountProcessor(account);
            processor.process();
            deleted += processor.getDeleteCount();
            moved += processor.getMoveCount();
        }
    } catch (MessagingException ex) {
        Logger.getLogger(MailFilter.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    public int getMoved() {
        return moved;
    }

    public int getDeleted() {
        return deleted;
    }

}
