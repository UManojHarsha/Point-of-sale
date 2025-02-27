package com.pos.invoice.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ZonedDateTime getCurrentUtcDate() {
        return ZonedDateTime.now(UTC);
    }

    public static String toString(ZonedDateTime dateTime) {
        return dateTime != null ? dateTime.format(formatter) : "";
    }

    public static Date toDate(ZonedDateTime dateTime) {
        return dateTime != null ? Date.from(dateTime.toInstant()) : null;
    }

    public static ZonedDateTime fromDate(Date date) {
        return date != null ? date.toInstant().atZone(UTC) : null;
    }

    public static String formatDate(Date date) {
        return date != null ? fromDate(date).format(formatter) : "";
    }

    public static Date toDateFromZonedDateTime(ZonedDateTime dateTime) {
        return dateTime != null ? Date.from(dateTime.toInstant()) : null;
    }

    public static ZonedDateTime toZonedDateTime(Date date) {
        return date != null ? date.toInstant().atZone(UTC) : null;
    }
} 