/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.monumentum.security;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;


/**
 *
 * @author jason
 */
@Singleton
public class KeyGenerator {
    private Key key;
    
    public Key getKey() {
        if (key == null) {
            String keyString = System.getProperty("signing.key", "replace for production");
            key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
        }
        
        return key;
    }
}
