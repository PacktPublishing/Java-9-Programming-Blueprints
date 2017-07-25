/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.datecalc.parser.token;

import com.steeplesoft.datecalc.DateCalcException;
import java.time.ZoneId;

/**
 *
 * @author jason
 */
public class TimeZoneToken extends Token<ZoneId> {
    public static final String REGEX = "(?:UTC|GMT)?(?:[\\+-][01]*[0-9]+)";
    public static class Info implements Token.Info {
        @Override
        public String getRegex() {
            return REGEX;
        }

        @Override
        public TimeZoneToken getToken(String text) {
            return of(text);
        }
    }
    
    public TimeZoneToken(ZoneId value) {
        this.value = value;
    }
    
    public static TimeZoneToken of(String text) {
        TimeZoneToken token;

        try {
            token = new TimeZoneToken(ZoneId.of(text));
        } catch (Exception e) {
            throw new DateCalcException("Invalid time zone specified: " + text);
        }
        
        return token;
    }
}
