package com.steeplesoft.deskdroid.model;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author jason
 */
public class ConversationComparator implements Comparator<Conversation> {

    @Override
    public int compare(Conversation o1, Conversation o2) {
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        List<Message> m1 = o1.getMessages();
        List<Message> m2 = o2.getMessages();

        if (m1 == null || m1.isEmpty()) {
            return 1;
        }
        if (m2 == null || m2.isEmpty()) {
            return -1;
        }

        Message last1 = m1.get(m1.size() - 1);
        Message last2 = m2.get(m2.size() - 1);

        return last2.getDate().compareTo(last1.getDate());
    }
}