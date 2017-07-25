package com.steeplesoft.datecalc.parser.token;

import com.steeplesoft.datecalc.DateCalcException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

/**
 *
 * @author jason
 */
public class TimeToken extends Token<LocalTime> {
    private static final String NOW = "now";
    public static final String REGEX = 
            "(?:[01]?\\d|2[0-3]):[0-5]\\d *(?:[AaPp][Mm])?|now";
    
    public static class Info implements Token.Info {

        @Override
        public String getRegex() {
            return REGEX;
        }

        @Override
        public Token getToken(String text) {
            return of(text);
        }
    }

    private TimeToken(LocalTime value) {
        this.value = value;
    }

public static TimeToken of(final String text) {
    String time = text.toLowerCase();
    if (NOW.equals(time)) {
        return new TimeToken(LocalTime.now());
    } else {
        try {
            // For hours < 10 (e.g., 1:30), the string will be less than 5 characters. LocalTime.parse(), though, requires, in its 
            // simplest form, HH:MM. If the string is less than 5 characters, we're going to add a zero at the beginning to put
            // the string in the correct format hopefully. If the string is truly an invalid format (e.g., '130'), then
            // LocalDate.parse() will fail, so there's no need to do sohpisticated validation here. We'll coerce the string as
            // needed to the correct length, then let the JDK do the heavy lifting.
            if (time.length() <5) {
                time = "0" + time;
            }
            if (time.contains("am") || time.contains("pm")) {
                final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("h:mma")
                        .toFormatter();
                return new TimeToken(LocalTime.parse(time.replaceAll(" ", ""), formatter));
            } else {
                return new TimeToken(LocalTime.parse(time));
            }
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
            throw new DateCalcException("Invalid time format: " + text);
        }
    }
}
}
