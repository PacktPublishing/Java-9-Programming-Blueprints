package com.steeplesoft.datecalc.cli;

import com.steeplesoft.datecalc.DateCalcException;
import com.steeplesoft.datecalc.DateCalculator;
import com.steeplesoft.datecalc.DateCalculatorResult;
import java.time.Duration;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tomitribe.crest.api.Command;

/**
 *
 * @author jason
 */
public class DateCalc {

    @Command
    public void dateCalc(String... args) {
        final String expression = String.join(" ", args);
        final DateCalculator dc = new DateCalculator();
        try {
            final DateCalculatorResult dcr = dc.calculate(expression);
            String result = "";
            if (dcr.getDate().isPresent()) {
                result = dcr.getDate().get().toString();
            } else if (dcr.getTime().isPresent()) {
                result = dcr.getTime().get().toString();
            } else if (dcr.getDuration().isPresent()) {
                result = processDuration(dcr.getDuration().get());
                processIso8601String(dcr.getDuration().get().toString());
            } else if (dcr.getPeriod().isPresent()) {
                result = processPeriod(dcr.getPeriod().get());
                processIso8601String(dcr.getPeriod().get().toString());
            }
            System.out.println(String.format("'%s' equals '%s'", expression, result));
        } catch (DateCalcException dce) {
            System.err.println(dce.getMessage());
        }
    }

    private String processDuration(Duration d) {
        long hours = d.toHoursPart();
        long minutes = d.toMinutesPart();
        long seconds = d.toSecondsPart();
        String result = "";

        if (hours > 0) {
            result += hours + " hours, ";
        }
        result += minutes + " minutes, ";
        if (seconds > 0) {
            result += seconds + " seconds";
        }

        return result;
    }

    private String processPeriod(Period p) {
        long years = p.getYears();
        long months = p.getMonths();
        long days = p.getDays();
        String result = "";

        if (years > 0) {
            result += years + " years, ";
        }
        if (months > 0) {
            result += months + " months, ";
        }
        if (days > 0) {
            result += days + " days";
        }
        return result;
    }

    protected String processIso8601String(String iso) {
        final Pattern pattern = Pattern.compile("([0-9]+\\.*[0-9]*)([YDWHMS])?+");
        final boolean isTime = iso.startsWith("PT");
        Matcher matcher = pattern.matcher(iso);
        String result = "";
        while (matcher.find()) {
            String number = matcher.group(1);
            String type = matcher.group(2);
            String measure = "<UNKNOWN>";
            switch (type) {
                case "Y":
                    measure = "years";
                    break;
                case "M":
                    measure = isTime ? "minutes" : "months";
                    break;
                case "W":
                    measure = "weeks";
                    break;
                case "D":
                    measure = "days";
                    break;
                case "H":
                    measure = "hours";
                    break;
                case "S":
                    measure = "seonds";
                    break;
            }
            result += String.format("%s %s ", number, measure);
        }
        return result;
    }
}
