package com.steeplesoft.deskdroid;

import android.Manifest;

public enum Permissions {
    READ_SMS(Manifest.permission.READ_SMS, 1),
    SEND_SMS(Manifest.permission.SEND_SMS, 2),
    RECEIVE_SMS(Manifest.permission.RECEIVE_SMS, 3),
    RECEIVE_MMS(Manifest.permission.RECEIVE_MMS, 4),
    READ_CONTACTS(Manifest.permission.READ_CONTACTS, 5);
    final String permission;
    final int code;

    Permissions(String text, int code) {
        this.permission = text;
        this.code = code;
    }
}