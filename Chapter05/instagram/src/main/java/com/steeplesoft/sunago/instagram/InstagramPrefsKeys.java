/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.sunago.instagram;


/**
 *
 * @author jason
 */
public enum InstagramPrefsKeys {
    TOKEN("token"), 
    TOKEN_SECRET("tokenSecret");
    private final String key;

    InstagramPrefsKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return "instagram." + key;
    }
}
