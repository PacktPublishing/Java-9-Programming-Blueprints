package com.steeplesoft.datecalc;

import com.steeplesoft.datecalc.parser.DateCalcExpressionParser;
import com.steeplesoft.datecalc.parser.token.DateToken;
import com.steeplesoft.datecalc.parser.token.IntegerToken;
import com.steeplesoft.datecalc.parser.token.OperatorToken;
import com.steeplesoft.datecalc.parser.token.TimeToken;
import com.steeplesoft.datecalc.parser.token.Token;
import com.steeplesoft.datecalc.parser.token.UnitOfMeasureToken;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Queue;

/**
 *
 * @author jason
 */
public class DateCalculator {
    public DateCalculatorResult calculate(String text) {
        final DateCalcExpressionParser parser = new DateCalcExpressionParser();
        final Queue<Token> tokens = parser.parse(text);

        try {
            if (!tokens.isEmpty()) {
                if (tokens.peek() instanceof DateToken) {
                    return handleDateExpression(tokens);
                } else if (tokens.peek() instanceof TimeToken) {
                    return handleTimeExpression(tokens);
                }
            }
        } catch (UnsupportedTemporalTypeException utte) {
            throw new DateCalcException(utte.getLocalizedMessage());
        }
        throw new DateCalcException("An invalid expression was given: " + text);
    }

    private DateCalculatorResult handleDateExpression(final Queue<Token> tokens) {
        DateToken startDateToken = (DateToken) tokens.poll();
        validateToken(tokens.peek(), OperatorToken.class);
        OperatorToken operatorToken = (OperatorToken) tokens.poll();
        Token thirdToken = tokens.peek();

        if (thirdToken instanceof IntegerToken) {
            return performDateMath(startDateToken, operatorToken, tokens);
        } else if (thirdToken instanceof DateToken) {
            return getDateDiff(startDateToken, tokens.poll());
        } else {
            throw new DateCalcException("Invalid expression");
        }
    }

    private DateCalculatorResult handleTimeExpression(final Queue<Token> tokens) {
        TimeToken startTimeToken = (TimeToken) tokens.poll();
        validateToken(tokens.peek(), OperatorToken.class);
        OperatorToken operatorToken = (OperatorToken) tokens.poll();
        Token thirdToken = tokens.peek();

        if (thirdToken instanceof IntegerToken) {
            return doTimeMath(operatorToken, startTimeToken, tokens);
        } else if (thirdToken instanceof TimeToken) {
            return getTimeDiff(operatorToken, startTimeToken, tokens.poll());
        } else {
            throw new DateCalcException("Invalid expression");
        }
    }

    private DateCalculatorResult doTimeMath(final OperatorToken operatorToken, final TimeToken startTimeToken, final Queue<Token> tokens) {
        int negate = operatorToken.isAddition() ? 1 : -1;
        LocalTime result = startTimeToken.getValue();

        while (!tokens.isEmpty()) {
            validateToken(tokens.peek(), IntegerToken.class);
            int amount = ((IntegerToken) tokens.poll()).getValue() * negate;
            validateToken(tokens.peek(), UnitOfMeasureToken.class);
            result = result.plus(amount, ((UnitOfMeasureToken) tokens.poll()).getValue());
        }
        return new DateCalculatorResult(result);
    }

    private DateCalculatorResult getTimeDiff(final OperatorToken operatorToken, final TimeToken startTimeToken, final Token thirdToken) throws DateCalcException {
        if (operatorToken.isAddition()) {
            throw new DateCalcException("Time differences should be expressed as TIME1 - TIME2. To do time math, please use units of measure (e.g., now + 15 minutes");
        }
        LocalTime startTime = startTimeToken.getValue();
        LocalTime endTime = ((TimeToken) thirdToken).getValue();
        return new DateCalculatorResult(Duration.between(startTime, endTime).abs());
        //        return (startTime.isBefore(endTime))
        //                ? new DateCalculatorResult(Duration.between(startTime, endTime))
        //                : new DateCalculatorResult(Duration.between(endTime, startTime));
    }

    private DateCalculatorResult getDateDiff(final DateToken startDateToken, final Token thirdToken) {
        LocalDate one = startDateToken.getValue();
        LocalDate two = ((DateToken) thirdToken).getValue();
        return (one.isBefore(two))
                ? new DateCalculatorResult(Period.between(one, two))
                : new DateCalculatorResult(Period.between(two, one));
    }

    private DateCalculatorResult performDateMath(final DateToken startDateToken, final OperatorToken operatorToken,
            final Queue<Token> tokens) {
        LocalDate result = startDateToken.getValue();
        int negate = operatorToken.isAddition() ? 1 : -1;
//    assertThat(tokens.size() % 2 == 0,
//            "Date math requires that all right hand values be pairs of units and units of measure (e.g., '2 weeks'");

        while (!tokens.isEmpty()) {
            validateToken(tokens.peek(), IntegerToken.class);
            int amount = ((IntegerToken) tokens.poll()).getValue() * negate;
            validateToken(tokens.peek(), UnitOfMeasureToken.class);
            result = result.plus(amount, ((UnitOfMeasureToken) tokens.poll()).getValue());
        }

        return new DateCalculatorResult(result);
    }

    private void validateToken(final Token token, final Class<? extends Token> expected) {
        if (token == null || !token.getClass().isAssignableFrom(expected)) {
            throw new DateCalcException(String.format("Invalid format: Expected %s, found %s",
                    expected, token != null ? token.getClass().getSimpleName() : "null"));
        }
    }

    private void assertThat(boolean isTrue, String message) {
        if (!isTrue) {
            throw new DateCalcException(message);
        }
    }
}
