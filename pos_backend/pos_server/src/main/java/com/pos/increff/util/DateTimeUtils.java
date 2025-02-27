package com.pos.increff.util;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtils {
    private static final ZoneId UTC = ZoneId.of("UTC");

    public static ZonedDateTime getCurrentUtcDate() {
        return ZonedDateTime.now(UTC);
    }

    public static ZonedDateTime getStartOfDay(ZonedDateTime date) {
        return date.toLocalDate().atStartOfDay(UTC);
    }

    public static ZonedDateTime getEndOfDay(ZonedDateTime date) {
        return date.toLocalDate().atTime(23, 59, 59, 999999999).atZone(UTC);
    }

    public static Date toDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }

    public static ZonedDateTime fromDate(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), UTC);
    }
} 