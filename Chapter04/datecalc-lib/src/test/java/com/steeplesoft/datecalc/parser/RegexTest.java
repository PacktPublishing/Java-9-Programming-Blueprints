/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.datecalc.parser;

import com.steeplesoft.datecalc.parser.token.DateToken;
import com.steeplesoft.datecalc.parser.token.IntegerToken;
import com.steeplesoft.datecalc.parser.token.OperatorToken;
import com.steeplesoft.datecalc.parser.token.TimeToken;
import com.steeplesoft.datecalc.parser.token.TimeZoneToken;
import com.steeplesoft.datecalc.parser.token.UnitOfMeasureToken;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author jason
 */
public class RegexTest {
    @Test
    public void dateTokenRegex() {
        testPattern(DateToken.REGEX, "2016-01-01");
        testPattern(DateToken.REGEX, "today");
    }

    @Test
    public void integerTokenRegex() {
        testPattern(IntegerToken.REGEX, "12");
        testPattern(IntegerToken.REGEX, "this sentence has 12 in the middle");
    }

    @Test
    public void operatorTokenRegex() {
        testPattern(OperatorToken.REGEX, "1 + 1");
        testPattern(OperatorToken.REGEX, "1+1");
        testPattern(OperatorToken.REGEX, "1 - 1");
        testPattern(OperatorToken.REGEX, "1-1");
        testPattern(OperatorToken.REGEX, "12:00 to 6");
    }

    @Test
    public void timeTokenRegex() {
        testPattern(TimeToken.REGEX, "11:42Am", true);
        testPattern(TimeToken.REGEX, "1:42", true);
        testPattern(TimeToken.REGEX, "11:42", true);
        testPattern(TimeToken.REGEX, "11:42am", true);
        testPattern(TimeToken.REGEX, "11:42aM", true);
        testPattern(TimeToken.REGEX, "11:42AM", true);
        testPattern(TimeToken.REGEX, "11:42pm", true);
        testPattern(TimeToken.REGEX, "11:42Pm", true);
        testPattern(TimeToken.REGEX, "11:42pM", true);
        testPattern(TimeToken.REGEX, "11:42PM", true);
        testPattern(TimeToken.REGEX, "11:42 PM", true);
        testPattern(TimeToken.REGEX, "11:42 AM", true);
        testPattern(TimeToken.REGEX, "11:42       AM", true);
        testPattern(TimeToken.REGEX, "now", true);
    }

    @Test
    public void unitOfMeasureRegex() {
        testPattern(UnitOfMeasureToken.REGEX, "years");
        testPattern(UnitOfMeasureToken.REGEX, "year");
        testPattern(UnitOfMeasureToken.REGEX, "y");
        testPattern(UnitOfMeasureToken.REGEX, "months");
        testPattern(UnitOfMeasureToken.REGEX, "month");
        testPattern(UnitOfMeasureToken.REGEX, "days");
        testPattern(UnitOfMeasureToken.REGEX, "day");
        testPattern(UnitOfMeasureToken.REGEX, "d");
        testPattern(UnitOfMeasureToken.REGEX, "weeks");
        testPattern(UnitOfMeasureToken.REGEX, "week");
        testPattern(UnitOfMeasureToken.REGEX, "w");
        testPattern(UnitOfMeasureToken.REGEX, "hours");
        testPattern(UnitOfMeasureToken.REGEX, "hour");
        testPattern(UnitOfMeasureToken.REGEX, "h");
        testPattern(UnitOfMeasureToken.REGEX, "minutes");
        testPattern(UnitOfMeasureToken.REGEX, "minute");
        testPattern(UnitOfMeasureToken.REGEX, "m");
        testPattern(UnitOfMeasureToken.REGEX, "seconds");
        testPattern(UnitOfMeasureToken.REGEX, "second");
        testPattern(UnitOfMeasureToken.REGEX, "s");
        
        testPattern(UnitOfMeasureToken.REGEX, "3 years 4 months");
        testPattern(UnitOfMeasureToken.REGEX, "8 days a week");
    }
    
    @Test
    public void timeZones() {
        testPattern(TimeZoneToken.REGEX, "UTC-5");
        testPattern(TimeZoneToken.REGEX, "UTC+5");
        testPattern(TimeZoneToken.REGEX, "GMT-5");
        testPattern(TimeZoneToken.REGEX, "GMT+5");
        testPattern(TimeZoneToken.REGEX, "+5");
        testPattern(TimeZoneToken.REGEX, "-5");
    }

    private void testPattern(String pattern, String text) {
        testPattern(pattern, text, false);
    }

    private void testPattern(String pattern, String text, boolean exact) {
        Pattern p = Pattern.compile("(" + pattern + ")");
        final Matcher matcher = p.matcher(text);

        Assert.assertTrue(matcher.find());
        if (exact) {
            Assert.assertEquals(matcher.group(), text);
        }
    }
}
