package com.steeplesoft.datecalc.parser.token;

import com.steeplesoft.datecalc.DateCalcException;

/**
 *
 * @author jason
 */
public class OperatorToken extends Token<String> {

    public static final String MINUS = "-";
    public static final String PLUS = "+";
    public static final String REGEX = "\\+|-|to";

    public static class Info implements Token.Info {
        @Override
        public String getRegex() {
            return REGEX;
        }

        @Override
        public OperatorToken getToken(String text) {
            return of(text);
        }
    }

    private OperatorToken(String value) {
        this.value = value;
    }

    public boolean isAddition() {
        return PLUS.equals(value);
    }

    public static OperatorToken of(String text) {
        if (PLUS.equals(text) || MINUS.equals(text)) {
            return new OperatorToken(text);
        } else {
            throw new DateCalcException("Invalid operator specified: " + text);
        }
    }
}
