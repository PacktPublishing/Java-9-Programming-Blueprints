package com.steeplesoft.monumentum.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author jason
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    private static final Pattern JS_DATE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z");
    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter JS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public LocalDateTime unmarshal(String date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.parse(date, 
            (JS_DATE.matcher(date).matches())
                ? JS_FORMAT : DEFAULT_FORMAT);
    }

    @Override
    public String marshal(LocalDateTime date) {
        return date != null ? DEFAULT_FORMAT.format(date) : null;
    }
}
