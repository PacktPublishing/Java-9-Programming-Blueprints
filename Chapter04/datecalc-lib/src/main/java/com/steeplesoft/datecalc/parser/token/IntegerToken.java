package com.steeplesoft.datecalc.parser.token;

import com.steeplesoft.datecalc.DateCalcException;

/**
 *
 * @author jason
 */
public class IntegerToken extends Token<Integer> {

    public static final String REGEX = "\\d+";

    public static class Info implements Token.Info {

        @Override
        public String getRegex() {
            return REGEX;
        }

        @Override
        public IntegerToken getToken(String text) {
            return of(text);
        }
    }

    private IntegerToken(Integer value) {
        this.value = value;
    }

    public static IntegerToken of(String text) {
        try {
            return new IntegerToken(Integer.valueOf(text));
        } catch (NumberFormatException ex) {
            throw new DateCalcException("Invalid number: " + text);
        }
    }
}
