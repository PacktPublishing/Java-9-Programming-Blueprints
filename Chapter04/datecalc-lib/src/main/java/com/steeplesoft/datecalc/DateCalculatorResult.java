package com.steeplesoft.datecalc;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Optional;

/**
 *
 * @author jason
 */
public class DateCalculatorResult {
    private Period period;
    private Duration duration;
    private LocalDate date;
    private LocalTime time;
    private String expression;

    public DateCalculatorResult(Period period) {
        this.period = period;
    }

    public DateCalculatorResult(Duration duration) {
        this.duration = duration;
    }

    public DateCalculatorResult(LocalDate date) {
        this.date = date;
    }

    public DateCalculatorResult(LocalTime time) {
        this.time = time;
    }

    public Optional<Period> getPeriod() {
        return Optional.ofNullable(period);
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<LocalDate> getDate() {
        return Optional.ofNullable(date);
    }

    public Optional<LocalTime> getTime() {
        return Optional.ofNullable(time);
    }

    public String getExpression() {
        return expression;
    }
}
