package com.steeplesoft.datecalc.parser.token;

import com.steeplesoft.datecalc.DateCalcException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jason
 */
public class UnitOfMeasureToken extends Token<ChronoUnit> {
    public static final String REGEX = 
            "years|year|y|months|month|weeks|week|w|days|day|d|hours|hour|h|minutes|minute|m|seconds|second|s";
            //"(?:y(?:ear[s]?)?)|(?:month[s]?)|(?:w(?:eek[s]?)?)|(?:d(?:ay[s]?)?)|(?:h(?:our[s])?)|(?:m(?:inute[s]?)?)|(?:s(?:econd[s]?)?)";
    private static final Map<String, ChronoUnit> VALID_UNITS = new HashMap<>();

    static {
        VALID_UNITS.put("year", ChronoUnit.YEARS);
        VALID_UNITS.put("years", ChronoUnit.YEARS);
        VALID_UNITS.put("months", ChronoUnit.MONTHS);
        VALID_UNITS.put("month", ChronoUnit.MONTHS);
        VALID_UNITS.put("weeks", ChronoUnit.WEEKS);
        VALID_UNITS.put("week", ChronoUnit.WEEKS);
        VALID_UNITS.put("w", ChronoUnit.WEEKS);
        VALID_UNITS.put("days", ChronoUnit.DAYS);
        VALID_UNITS.put("day", ChronoUnit.DAYS);
        VALID_UNITS.put("d", ChronoUnit.DAYS);
        VALID_UNITS.put("hours", ChronoUnit.HOURS);
        VALID_UNITS.put("hour", ChronoUnit.HOURS);
        VALID_UNITS.put("h", ChronoUnit.HOURS);
        VALID_UNITS.put("minutes", ChronoUnit.MINUTES);
        VALID_UNITS.put("minute", ChronoUnit.MINUTES);
        VALID_UNITS.put("m", ChronoUnit.MINUTES);
        VALID_UNITS.put("seconds", ChronoUnit.SECONDS);
        VALID_UNITS.put("second", ChronoUnit.SECONDS);
        VALID_UNITS.put("s", ChronoUnit.SECONDS);
    }

    public static class Info implements Token.Info {

        @Override
        public String getRegex() {
            return REGEX;
        }

        @Override
        public UnitOfMeasureToken getToken(String text) {
            return of(text);
        }
    }

    public UnitOfMeasureToken(ChronoUnit value) {
        this.value = value;
    }

    public static UnitOfMeasureToken of(String text) {
        ChronoUnit uom = VALID_UNITS.get(text.toLowerCase());
        if (uom != null) {
            return new UnitOfMeasureToken(uom);
        } else {
            throw new DateCalcException("Invalid unit of measure: " + text);
        }
    }
}
