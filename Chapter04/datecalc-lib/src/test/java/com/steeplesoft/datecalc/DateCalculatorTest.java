package com.steeplesoft.datecalc;

import com.steeplesoft.datecalc.DateCalculator;
import com.steeplesoft.datecalc.DateCalculatorResult;
import java.time.LocalTime;
import java.time.Period;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author jason
 */
public class DateCalculatorTest {
    private final DateCalculator dc = new DateCalculator();
    
    @Test
    public void testDateMath() {
        final String expression = "today + 2 weeks 3 days";
        DateCalculatorResult result = dc.calculate(expression);
        Assert.assertNotNull(result.getDate().get(), "'" + expression + "' should have returned a result.");
    }
    
    @Test
    public void testDateDiff() {
        final String expression = "2016/07/04 - 1776/07/04";
        DateCalculatorResult result = dc.calculate(expression);
        Assert.assertEquals(result.getPeriod().get(),  Period.of(240,0,0), "'" + expression + "' should...");
    }
    
    @Test
    public void timeMath() {
        final String expression = "12:37 + 42 m";
        DateCalculatorResult result = dc.calculate(expression);
        Assert.assertEquals(result.getTime().get(), LocalTime.parse("13:19"));
    }
    
    @Test
    public void timeDiff() {
        final String expression = "12:37 - 7:15";
        DateCalculatorResult result = dc.calculate(expression);
        Assert.assertEquals(result.getDuration().get().toHoursPart(), 5);
        Assert.assertEquals(result.getDuration().get().toMinutesPart(), 22);
    }
}
