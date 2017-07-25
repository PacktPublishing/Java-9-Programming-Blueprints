package com.steeplesoft.deskdroid.model;

import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {
    @Override
    public int compare(Message message1, Message message2) {
        if (message2.getDate() == null) {
            return -1;
        }
        if (message1.getDate() == null) {
            return -1;
        }
        return message1.getDate().compareTo(message2.getDate());
    }
}