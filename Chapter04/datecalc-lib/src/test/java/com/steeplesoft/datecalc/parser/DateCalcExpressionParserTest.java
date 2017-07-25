package com.steeplesoft.datecalc.parser;

import com.steeplesoft.datecalc.parser.DateCalcExpressionParser;
import com.steeplesoft.datecalc.DateCalcException;
import com.steeplesoft.datecalc.parser.token.DateToken;
import com.steeplesoft.datecalc.parser.token.IntegerToken;
import com.steeplesoft.datecalc.parser.token.OperatorToken;
import com.steeplesoft.datecalc.parser.token.TimeToken;
import com.steeplesoft.datecalc.parser.token.Token;
import com.steeplesoft.datecalc.parser.token.UnitOfMeasureToken;
import java.util.Queue;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author jason
 */
public class DateCalcExpressionParserTest {
    private final DateCalcExpressionParser parser = new DateCalcExpressionParser();
    
    @Test
    public void todayWithSpaces() {
        Queue<Token> words = parser.parse("today + 2 weeks");
        Assert.assertTrue(words.poll() instanceof DateToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof IntegerToken);
        Assert.assertTrue(words.poll() instanceof UnitOfMeasureToken);
    }
    
    @Test
    public void todayWithoutSpaces() {
        Queue<Token> words = parser.parse("today+2w");
        Assert.assertTrue(words.poll() instanceof DateToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof IntegerToken);
        Assert.assertTrue(words.poll() instanceof UnitOfMeasureToken);
    }
   
    @Test
    public void nowWithSpacesAndTime() {
        Queue<Token> words = parser.parse("now + 03:04");
        Assert.assertTrue(words.poll() instanceof TimeToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof TimeToken);
    }
    
    @Test
    public void nowWithSpacesAndUnitOfMeasure() {
        Queue<Token> tokens = parser.parse("now + 3h4m");
        Assert.assertEquals(tokens.size(), 6);
        Assert.assertTrue(tokens.poll() instanceof TimeToken);
        Assert.assertTrue(tokens.poll() instanceof OperatorToken);
        Assert.assertTrue(tokens.poll() instanceof IntegerToken);
        Assert.assertTrue(tokens.poll() instanceof UnitOfMeasureToken);
        Assert.assertTrue(tokens.poll() instanceof IntegerToken);
        Assert.assertTrue(tokens.poll() instanceof UnitOfMeasureToken);
    }

    @Test
    public void nowWithoutSpacesAndTime() {
        Queue<Token> words = parser.parse("now+03:04");
        Assert.assertTrue(words.poll() instanceof TimeToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof TimeToken);
    }
 
    @Test
    public void nowWithoutSpacesAndUnitOfMeasure() {
        Queue<Token> tokens = parser.parse("now+3h4m");
        Assert.assertEquals(tokens.size(), 6);
        Assert.assertTrue(tokens.poll() instanceof TimeToken);
        Assert.assertTrue(tokens.poll() instanceof OperatorToken);
        Assert.assertTrue(tokens.poll() instanceof IntegerToken);
        Assert.assertTrue(tokens.poll() instanceof UnitOfMeasureToken);
        Assert.assertTrue(tokens.poll() instanceof IntegerToken);
        Assert.assertTrue(tokens.poll() instanceof UnitOfMeasureToken);
    }
   
    @Test
    public void timeAdditionWithSpaces() {
        Queue<Token> words = parser.parse("12:37 + 0:42");
        Assert.assertTrue(words.poll() instanceof TimeToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof TimeToken);
    }
    
    @Test
    public void timeAdditionWithoutSpaces() {
        Queue<Token> words = parser.parse("12:37+0:42");
        Assert.assertTrue(words.poll() instanceof TimeToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof TimeToken);
    }
    
    @Test
    public void dateMathWithSpaces() {
        Queue<Token> words = parser.parse("2016/09/27 - 3 months");
        Assert.assertTrue(words.poll() instanceof DateToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof IntegerToken);
        Assert.assertTrue(words.poll() instanceof UnitOfMeasureToken);
    }
    
    @Test
    public void dateDiffWithoutSpaces() {
        Queue<Token> words = parser.parse("2016/12/25-2016/07/04");
        Assert.assertTrue(words.poll() instanceof DateToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof DateToken);
    }

    @Test
    public void dateMinusDuration() {
        Queue<Token> tokens = parser.parse("2016/09/27 - 3 months");
        Assert.assertEquals(tokens.size(), 4);
        Assert.assertTrue(tokens.poll() instanceof DateToken);
        Assert.assertTrue(tokens.poll() instanceof OperatorToken);
        Assert.assertTrue(tokens.poll() instanceof IntegerToken);
        Assert.assertTrue(tokens.poll() instanceof UnitOfMeasureToken);
    }

    @Test
    public void dateMinusDate() {
        Queue<Token> tokens = parser.parse("2016/12/25-2016/07/04");
        Assert.assertEquals(tokens.size(), 3);
        Assert.assertTrue(tokens.poll() instanceof DateToken);
        Assert.assertTrue(tokens.poll() instanceof OperatorToken);
        Assert.assertTrue(tokens.poll() instanceof DateToken);
    }

    @Test
    public void dateMinusDateWithSpaces() {
        Queue<Token> tokens = parser.parse("2016/12/25 - 2016/07/04");
        Assert.assertEquals(tokens.size(), 3);
        Assert.assertTrue(tokens.poll() instanceof DateToken);
        Assert.assertTrue(tokens.poll() instanceof OperatorToken);
        Assert.assertTrue(tokens.poll() instanceof DateToken);
    }

    @Test
    public void dateMinusDateWithInvalidOperator() {
        try {
            Queue<Token> tokens = parser.parse("2016/12/25 * 2016/07/04");
            Assert.fail("A DateCalcException should have been thrown for an illegal character");
        } catch (DateCalcException dce) {
        }
    }

    @Test
    public void timePlusDuration() {
        Queue<Token> tokens = parser.parse("12:37+42m");
        Assert.assertEquals(tokens.size(), 4);
        Assert.assertTrue(tokens.poll() instanceof TimeToken);
        Assert.assertTrue(tokens.poll() instanceof OperatorToken);
        Assert.assertTrue(tokens.poll() instanceof IntegerToken);
        Assert.assertTrue(tokens.poll() instanceof UnitOfMeasureToken);
    }

    @Test
    public void timePlusDurationWithSpaces() {
        Queue<Token> tokens = parser.parse("12:37 + 42 m");
        Assert.assertEquals(tokens.size(), 4);
        Assert.assertTrue(tokens.poll() instanceof TimeToken);
        Assert.assertTrue(tokens.poll() instanceof OperatorToken);
        Assert.assertTrue(tokens.poll() instanceof IntegerToken);
        Assert.assertTrue(tokens.poll() instanceof UnitOfMeasureToken);
    }

    @Test
    public void invalidStringsShouldFail() {
        try {
            parser.parse("2016/12/25 this is nonsense");
            Assert.fail("A DateCalcException should have been thrown (Unable to identify token)");
        } catch (DateCalcException dce) {

        }
    }
    
    @Test
    public void testWithAm() {
        Queue<Token> words = parser.parse("12:37am");
        Assert.assertEquals(words.size(), 1);
        Assert.assertTrue(words.poll() instanceof TimeToken);
    }

    @Test
    public void testTimeDiffWithAm() {
        Queue<Token> words = parser.parse("12:37am-5:00am");
        Assert.assertEquals(words.size(), 3);
        Assert.assertTrue(words.poll() instanceof TimeToken);
        Assert.assertTrue(words.poll() instanceof OperatorToken);
        Assert.assertTrue(words.poll() instanceof TimeToken);
    }

    @Test(expectedExceptions = {DateCalcException.class})
    public void shouldRejectBadTimes() {
        parser.parse("22:89");
    }
    
    @Test
    public void paddedWithSpaces() {
        parser.parse("    now + 15 weeks   ");
    }
}
