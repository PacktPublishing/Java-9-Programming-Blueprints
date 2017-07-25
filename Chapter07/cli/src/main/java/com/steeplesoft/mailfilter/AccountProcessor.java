package com.steeplesoft.mailfilter;

import com.steeplesoft.mailfilter.model.Account;
import com.steeplesoft.mailfilter.model.Rule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 *
 * @author jdlee
 */
public class AccountProcessor {

    private static final Flags FLAGS_DELETED = new Flags(Flags.Flag.DELETED);

    final private Account account;
    final private Map<String, Folder> folders = new HashMap<>();
    private int deleteCount;
    private int moveCount;
    private Store store;

    public AccountProcessor(Account account) throws MessagingException {
        this.account = account;
    }

    public void process() throws MessagingException {
        try {
            getImapSession();

            for (Map.Entry<String, List<Rule>> entry : getRulesByFolder(account.getRules()).entrySet()) {
                processFolder(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeFolders();
            if (store != null) {
                store.close();
            }
        }
    }

    private void getImapSession() throws MessagingException, NoSuchProviderException {
        Properties props = new Properties();
        props.put("mail.imap.ssl.trust", "*");
        props.put("mail.imaps.ssl.trust", "*");
        props.setProperty("mail.imap.starttls.enable", Boolean.toString(account.isUseSsl()));
        Session session = Session.getInstance(props, null);
        store = session.getStore(account.isUseSsl() ? "imaps" : "imap");
        store.connect(account.getServerName(), account.getUserName(), account.getPassword());
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public int getMoveCount() {
        return moveCount;
    }

    private void processFolder(String folder, List<Rule> rules) throws MessagingException {
        Arrays.stream(getFolder(folder, Folder.READ_WRITE).getMessages())
                .parallel()
                .forEach(message
                        -> rules.stream()
                        .filter(rule -> rule.getSearchTerm().match(message))
                        .forEach(rule -> {
                            switch (rule.getType()) {
                                case MOVE:
                                    moveMessage(message, getFolder(rule.getDestFolder(), Folder.READ_WRITE));
                                    break;
                                case DELETE:
                                    deleteMessage(message);
                                    break;
                            }
                        }));
    }

    private Map<String, List<Rule>> getRulesByFolder(List<Rule> rules) {
        return rules.stream().collect(
                Collectors.groupingBy(r -> r.getSourceFolder(),
                        Collectors.toList()));
    }

    private void deleteMessage(Message toDelete) {
        if (toDelete != null) {
            try {
                final Folder source = toDelete.getFolder();
                source.setFlags(new Message[]{toDelete}, FLAGS_DELETED, true);
                deleteCount++;
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void moveMessage(Message toMove, Folder dest) {
        if (toMove != null) {
            try {
                final Folder source = toMove.getFolder();
                final Message[] messages = new Message[]{toMove};
                source.setFlags(messages, FLAGS_DELETED, true);
                source.copyMessages(messages, dest);
                moveCount++;
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private Folder getFolder(String folderName, int mode) {
        Folder source = null;
        try {
            if (folders.containsKey(folderName)) {
                source = folders.get(folderName);
            } else {
                source = store.getFolder(folderName);
                if (source == null || !source.exists()) {
                    throw new IllegalArgumentException("Invalid folder: " + folderName);
                }
                folders.put(folderName, source);
            }
            if (!source.isOpen()) {
                source.open(mode);
            }
        } catch (MessagingException ex) {
            Logger.getLogger(AccountProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return source;
    }

    private void closeFolders() {
        folders.values().stream()
                .filter(f -> f.isOpen())
                .forEachOrdered(f -> {
                    try {
                        f.close(true);
                    } catch (MessagingException e) {
                    }
                });
    }
}
