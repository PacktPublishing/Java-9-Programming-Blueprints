/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.datecalc.parser.token;

import com.steeplesoft.datecalc.DateCalcException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 *
 * @author jason
 */
public class DateToken extends Token<LocalDate> {
    private static final String TODAY = "today";
    public static String REGEX = "\\d{4}[-/][01]\\d[-/][0123]\\d|today"; // YYYY-MM-DD or YYYY/MM/DD
    
    public static class Info implements Token.Info {
        @Override
        public String getRegex() {
            return REGEX;
        }

        @Override
        public DateToken getToken(String text) {
            return of(text);
        }
    }

    private DateToken(LocalDate value) {
        this.value = value;
    }

    public static DateToken of(String text) {
        try {
            return TODAY.equals(text.toLowerCase())
                    ? new DateToken(LocalDate.now())
                    : new DateToken(LocalDate.parse(text.replace("/", "-")));
        } catch (DateTimeParseException ex) {
            throw new DateCalcException("Invalid date format: " + text);
        }
    }
}
